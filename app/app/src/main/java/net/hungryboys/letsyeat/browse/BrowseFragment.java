package net.hungryboys.letsyeat.browse;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.data.model.RecipeID;
import net.hungryboys.letsyeat.data.model.RecipeStub;
import net.hungryboys.letsyeat.recipe.RecipeActivity;

import java.util.List;

public class BrowseFragment extends Fragment {

    private static final String ARG_CURR_ACTIVITY = "current_activity";

    // Recycle view attributes
    private RecyclerView recipeView;
    private RecipeCardAdapter recipeCardAdapter;

    // Search view
    private EditText searchBar;
    private TagButton tagsButton;
    private RecyclerView tagsGridView;
    private TagGridAdapter tagGridAdapter;

    private View rootView;
    private BrowseViewModel browseViewModel;

    public BrowseFragment(){
        // Required empty constructor
    }

    public static BrowseFragment newInstance() {
        return new BrowseFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }

        rootView = inflater.inflate(R.layout.fragment_browse, container, false);

        createRecipeView();
        createSearchView();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        browseViewModel = ViewModelProviders.of(this).get(BrowseViewModel.class);
        browseViewModel.getRecipeStubs().observe(this, new Observer<List<RecipeStub>>() {
            @Override
            public void onChanged(List<RecipeStub> recipeStubs) {
                recipeCardAdapter.setRecipes(recipeStubs);
            }
        });

        recipeCardAdapter.setOnSelectListener(new RecipeCardAdapter.RecipeOnSelectListener() {
            @Override
            public void onSelect(RecipeID recipeID) {
                Intent intent = new Intent(getActivity(), RecipeActivity.class);
                intent.putExtra(RecipeActivity.EXTRA_RECIPE_ID, recipeID);
                startActivity(intent);
            }
        });

        searchBar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    browseViewModel.searchTextChanged(searchBar.getText().toString());
                }

                return true;
            }
        });

        tagsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tagsButton.toggle(browseViewModel.hasTagsSelected());

                if (tagsButton.isChecked()) {
                    tagsGridView.setVisibility(View.VISIBLE);
                } else {
                    tagsGridView.setVisibility(View.GONE);
                }
            }
        });

        ToggleButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ToggleButton button = (ToggleButton) buttonView;
                browseViewModel.tagChanged(button.getText().toString(), button.isChecked());
            }
        };

        tagGridAdapter.setListener(listener);
    }

    private void createSearchView() {
        searchBar = rootView.findViewById(R.id.browse_search_bar);
        tagsButton = rootView.findViewById(R.id.browse_tags_toggle);
        searchBar.setText("");

        createTagsGrid();
    }

    private void createTagsGrid() {
        tagsGridView = rootView.findViewById(R.id.browse_tags_grid);
        tagsGridView.setHasFixedSize(true);

        tagsGridView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        String[] tags = getResources().getStringArray(R.array.tag_strings);

        tagGridAdapter = new TagGridAdapter(tags);
        tagsGridView.setAdapter(tagGridAdapter);
        tagsGridView.setVisibility(View.GONE);
    }

    private void createRecipeView() {
        recipeView = rootView.findViewById(R.id.browse_recipe_list);
        recipeView.setHasFixedSize(true);

        recipeView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recipeCardAdapter = new RecipeCardAdapter();
        recipeView.setAdapter(recipeCardAdapter);


    }


}
