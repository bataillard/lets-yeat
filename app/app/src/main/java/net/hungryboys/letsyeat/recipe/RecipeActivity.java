package net.hungryboys.letsyeat.recipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.data.model.RecipeID;
import net.hungryboys.letsyeat.navigation.NavigationFragment;

public class RecipeActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        RecipeID recipeID = (RecipeID) getIntent().getExtras().getSerializable(EXTRA_RECIPE_ID);

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            FragmentTransaction recipeTransaction = fragmentManager.beginTransaction();
            RecipeFragment recipeFragment = RecipeFragment.newInstance(recipeID);
            recipeTransaction.replace(R.id.recipe_container, recipeFragment);
            recipeTransaction.commit();

            FragmentTransaction navigationTransaction = fragmentManager.beginTransaction();
            NavigationFragment navigationFragment = NavigationFragment.newInstance(R.id.navigation_browse);
            navigationTransaction.replace(R.id.recipe_navigation_container, navigationFragment);
            navigationTransaction.commit();
        }
    }
}
