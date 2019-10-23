package net.hungryboys.letsyeat.recipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.data.RecipeID;
import net.hungryboys.letsyeat.navigation.NavigationFragment;

public class RecipeActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";
    private static final String TAG_RECIPE = "RecipeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            RecipeID recipeID = (RecipeID) extras.getSerializable(EXTRA_RECIPE_ID);

            FragmentManager fragmentManager = getSupportFragmentManager();

            FragmentTransaction recipeTransaction = fragmentManager.beginTransaction();
            RecipeFragment recipeFragment = RecipeFragment.newInstance(recipeID);
            recipeTransaction.replace(R.id.recipe_container, recipeFragment);
            recipeTransaction.commit();

            FragmentTransaction navigationTransaction = fragmentManager.beginTransaction();
            NavigationFragment navigationFragment = NavigationFragment.newInstance(R.id.navigation_browse);
            navigationTransaction.replace(R.id.recipe_navigation_container, navigationFragment);
            navigationTransaction.commit();
        } else {
            Log.e(TAG_RECIPE, "No RecipeID passed to RecipeActivity");
            finish();
        }
    }
}
