package net.hungryboys.letsyeat.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.data.RecipeID;
import net.hungryboys.letsyeat.recipe.RecipeActivity;


/**
 * Bottom navigation bar. When one of the four navigation buttons is pressed, switches to that
 * corresponding activity. If the "Yeet" button is clicked, asks the server for a recipe suggestion
 * then switches to single recipe view based on received recipe ID
 *
 * Use the {@link NavigationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavigationFragment extends Fragment {
    private static final String ARG_CURR_ACTIVITY = "current_activity";

    @IdRes private int currentActivity;

    private View root;

    private FloatingActionButton yeatButton;
    private BottomNavigationView bottomNavigationView;
    private NavigationViewModel viewModel;

    public NavigationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param currentActivity Current Activity resource ID, sets corresponding navigation button to active
     * @return A new instance of fragment NavigationFragment.
     */
    public static NavigationFragment newInstance(@IdRes int currentActivity) {
        NavigationFragment fragment = new NavigationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CURR_ACTIVITY, currentActivity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentActivity = getArguments().getInt(ARG_CURR_ACTIVITY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (root != null) {
            return root;
        }

        viewModel = new NavigationViewModel();
        viewModel.getRecipeId().observe(this, new Observer<RecipeID>() {
            @Override
            public void onChanged(RecipeID recipeID) {
                Intent intent = new Intent(getActivity(), RecipeActivity.class);
                intent.putExtra(RecipeActivity.EXTRA_RECIPE_ID, recipeID);
                startActivity(intent);
            }
        });

        root = inflater.inflate(R.layout.fragment_navigation, container, false);
        yeatButton = root.findViewById(R.id.yeat_button);
        bottomNavigationView = root.findViewById(R.id.bottom_navigation_bar);

        yeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Yeat!", Toast.LENGTH_LONG).show();
                viewModel.yeetClicked();
            }
        });

        setupBottomNavigationButtons();

        return root;
    }

    private void setupBottomNavigationButtons() {
        if (currentActivity != 0) {
            bottomNavigationView.setSelectedItemId(currentActivity);
            bottomNavigationView.setSelected(true);
        } else {
            // By default, set browse to active
            bottomNavigationView.setSelectedItemId(R.id.navigation_browse);
            bottomNavigationView.setSelected(true);
        }


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int targetActivity = menuItem.getItemId();

                if (targetActivity == currentActivity) {
                    return true;
                }

                // TODO Replace Toast with opening activity
                switch (menuItem.getItemId()) {
                    case R.id.navigation_browse:
                        Toast.makeText(getActivity(), "Browse", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_calendar:
                        Toast.makeText(getActivity(), "Calendar", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_groceries:
                        Toast.makeText(getActivity(), "Groceries", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_profile:
                        Toast.makeText(getActivity(), "Profile", Toast.LENGTH_SHORT).show();
                        break;
                }

                return true;
            }
        });
    }

}
