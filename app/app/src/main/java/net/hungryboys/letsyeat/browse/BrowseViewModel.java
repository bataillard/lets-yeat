package net.hungryboys.letsyeat.browse;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.hungryboys.letsyeat.data.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class BrowseViewModel extends ViewModel {
    private MutableLiveData<List<Recipe>> recipes;

    public LiveData<List<Recipe>> getRecipes() {
        if (recipes == null) {
            recipes = new MutableLiveData<>();
            loadRecipes();
        }

        return recipes;
    }

    // TODO replace hardcoded values with calls to server/recipe cache
    private void loadRecipes() {
        List<Recipe> recipeList = new ArrayList<>();
        for (int i = 0; i < 30; i++){
            recipeList.add(Recipe.placeholder());
        }

        recipes.setValue(recipeList);
    }
}
