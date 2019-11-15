package net.hungryboys.letsyeat.recipe;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.data.Ingredient;
import net.hungryboys.letsyeat.data.Recipe;
import net.hungryboys.letsyeat.data.RecipeID;

import java.util.Calendar;
import java.util.Locale;

/**
 * Fragment that actually displays and manages the RecipeID passed to its parent activity
 *
 * Use {@link RecipeFragment#newInstance(RecipeID)} to create this fragment with specified
 * recipe id
 */
public class RecipeFragment extends Fragment {
    private static final String ARG_RECIPE_ID = "recipe_id";
    private static final String TAG_RECIPE = "RecipeFragment";

    private static final String DIFFICULTY_FORMAT = "%.1f";

    private RecipeViewModel mViewModel;
    private View rootView;

    private NestedScrollView scrollView;
    private Button cookButton;
    private ImageView image;
    private TextView title;
    private TextView time;
    private TextView difficulty;

    private LinearLayout ingredientsContainer;
    private LinearLayout instructionContainer;
    private ProgressBar progressBar;

    /**
     * Factory method that creates a new RecipeFragment with RecipeID as argument
     * @param id id of recipe that needs to be displayed
     * @return the new fragment
     */
    public static RecipeFragment newInstance(RecipeID id) {
        RecipeFragment rf = new RecipeFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_RECIPE_ID, id);
        rf.setArguments(args);

        return rf;
    }

    /**
     * Called when fragment needs to draw its user interface, only inflate layout in this method
     * @param inflater Inflater user to draw this fragment's layout
     * @param container ViewGroup that contains this fragment
     * @param savedInstanceState If non null, previous state of fragment being reconstructed
     * @return An inflated View for this fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }

        rootView = inflater.inflate(R.layout.fragment_recipe, container, false);
        scrollView = rootView.findViewById(R.id.recipe_scroll_view);
        progressBar = rootView.findViewById(R.id.recipe_progress_bar);
        cookButton = rootView.findViewById(R.id.recipe_cook_button);
        image = rootView.findViewById(R.id.recipe_image);
        title = rootView.findViewById(R.id.recipe_title);
        time = rootView.findViewById(R.id.recipe_time);
        difficulty = rootView.findViewById(R.id.recipe_difficulty);
        ingredientsContainer = rootView.findViewById(R.id.recipe_ingredient_container);
        instructionContainer = rootView.findViewById(R.id.recipe_instructions_container);

        LinearLayoutManager ingredientsLayoutManager = new LinearLayoutManager(getContext());
        ingredientsLayoutManager.setOrientation(RecyclerView.VERTICAL);

        LinearLayoutManager instructionsLayoutManager = new LinearLayoutManager(getContext());
        instructionsLayoutManager.setOrientation(RecyclerView.VERTICAL);

        return rootView;
    }

    /**
     * Called after layout has been inflated, and activity has been created, this is when we need to
     * initialise this fragment's state (the view model) and the listeners
     * @param savedInstanceState If non null, previous state of fragment being reconstructed
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);

        Bundle args = getArguments();
        RecipeID recipeID = args == null ? null : (RecipeID) args.getSerializable(ARG_RECIPE_ID);

        if (recipeID != null) {
            mViewModel.setId(recipeID);
        } else {
            Toast.makeText(getContext(), R.string.cannot_load_recipe, Toast.LENGTH_SHORT).show();
            Log.e(TAG_RECIPE, "Recipe ID in RecipeFragment is null");
        }

        mViewModel.getRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(Recipe recipe) {
                progressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);

                cookButton.setClickable(true);
                title.setText(recipe.getName());
                time.setText(recipe.getTimeString());
                difficulty.setText(String.format(Locale.getDefault(), DIFFICULTY_FORMAT, recipe.getDifficulty()));

                changeIngredients(recipe.getIngredients());
                changeInstructions(recipe.getInstructions());
            }
        });

        cookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cookPressed();
            }
        });
    }

    private void cookPressed() {
        // Ask if user wants default time or custom time

        DialogInterface.OnClickListener customTime = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cookCustomDate();
            }
        };

        DialogInterface.OnClickListener defaultTime = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mViewModel.cookConfirm(getContext());
            }
        };

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setMessage(R.string.when_cook_meal)
                .setPositiveButton(R.string.select_default_time, defaultTime)
                .setNeutralButton(R.string.select_custom_time, customTime)
                .create();

        dialog.show();
    }

    private void cookCustomDate() {
        // If user choses custom time, ask user for date

        if (getContext() == null) {
            Log.e(TAG_RECIPE, "Could not get Context in Fragment to build Dialogs");
            return;
        }

        DatePickerDialog.OnDateSetListener dateSelect = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mViewModel.setCookCustomDate(year, month, dayOfMonth);
                cookCustomTime();
            }
        };

        Calendar now = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(getContext(), dateSelect,
                now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));

        datePicker.show();

    }

    private void cookCustomTime() {
        // If user chose custom time, ask user for time

        if (getContext() == null) {
            Log.e(TAG_RECIPE, "Could not get Context in Fragment to build Dialogs");
            return;
        }

        TimePickerDialog.OnTimeSetListener timeSelect = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mViewModel.setCustomTime(hourOfDay, minute);
                mViewModel.cookConfirm(getContext());
                Toast.makeText(getContext(), R.string.cook_confirmed, Toast.LENGTH_SHORT).show();
            }
        };

        Calendar now = Calendar.getInstance();
        TimePickerDialog timePicker = new TimePickerDialog(getContext(), timeSelect,
                now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);

        timePicker.show();

    }

    private void changeIngredients(Ingredient[] ingredients) {
        // Replaces ingredients in LinearLayout with new ones provided

        ingredientsContainer.removeAllViews();

        for (Ingredient ingredient : ingredients) {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.elem_ingredient, ingredientsContainer, false);

            TextView text = view.findViewById(R.id.elem_ingredient_text);
            text.setText(ingredient.toString());

            ingredientsContainer.addView(view);
        }
    }

    private void changeInstructions(String[] instructions) {
        // Replaces instructions in LinearLayout with new ones provided

        instructionContainer.removeAllViews();

        for (String instruction : instructions) {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.elem_instruction, instructionContainer, false);

            TextView text = view.findViewById(R.id.elem_instruction_text);
            text.setText(instruction);

            instructionContainer.addView(view);
        }
    }

}
