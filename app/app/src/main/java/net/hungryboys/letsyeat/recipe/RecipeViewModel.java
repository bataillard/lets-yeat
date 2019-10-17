package net.hungryboys.letsyeat.recipe;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.hungryboys.letsyeat.data.model.Recipe;
import net.hungryboys.letsyeat.data.model.RecipeID;

public class RecipeViewModel extends ViewModel {
    private MutableLiveData<Recipe> recipe = new MutableLiveData<>();
    private RecipeID id;

    public LiveData<Recipe> getRecipe() {
        if (recipe == null) {
            recipe = new MutableLiveData<>();
        }

        if (id != null) {
            loadRecipe();
        }

        return recipe;
    }

    public void setId(@NonNull RecipeID id) {
        if (!id.equals(this.id)) {
            this.id = id;
            loadRecipe();
        }
    }

    public void cookConfirm() {
        Log.d("RECIPE", "Cooking confirmed for " + recipe.getValue().getName());
    }

    private void loadRecipe() {
        recipe.setValue(Recipe.placeholder());
    }
}
