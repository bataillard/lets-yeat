package net.hungryboys.letsyeat.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.data.model.Ingredient;
import net.hungryboys.letsyeat.data.model.Recipe;
import net.hungryboys.letsyeat.data.model.RecipeID;

import java.util.Locale;

public class RecipeFragment extends Fragment {
    private static final String ARG_RECIPE_ID = "recipe_id";

    private View rootView;

    private RecipeViewModel mViewModel;
    private Button cookButton;
    private ImageView image;
    private TextView title;
    private TextView time;
    private TextView difficulty;

    private LinearLayout ingredientsContainer;
    private LinearLayout instructionContainer;

    public static RecipeFragment newInstance(RecipeID id) {
        RecipeFragment rf = new RecipeFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_RECIPE_ID, id);
        rf.setArguments(args);

        return rf;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }

        rootView = inflater.inflate(R.layout.fragment_recipe, container, false);
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        mViewModel.setId((RecipeID) getArguments().getSerializable(ARG_RECIPE_ID));

        mViewModel.getRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(Recipe recipe) {
                cookButton.setClickable(true);
                title.setText(recipe.getName());
                time.setText(recipe.getTimeString());
                difficulty.setText(String.format(Locale.getDefault(),"%.1f", recipe.getDifficulty()));

                changeIngredients(recipe.getIngredients());
                changeInstructions(recipe.getInstructions());
            }
        });

        cookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.cookConfirm();
            }
        });
    }

    private void changeIngredients(Ingredient[] ingredients) {
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
