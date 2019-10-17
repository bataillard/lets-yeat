package net.hungryboys.letsyeat.recipe;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.hungryboys.letsyeat.REST.RESTHandler;
import net.hungryboys.letsyeat.data.model.Recipe;
import net.hungryboys.letsyeat.data.model.RecipeID;

public class RecipeViewModel extends ViewModel {
    private static final String TAG = "RECIPE_VIEW_MODEL";

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
        if (id != null) {
            RESTHandler.getRecipe(id, new RESTHandler.RequestHandler<Recipe>() {
                @Override
                public void onRequestFinished(Recipe result) {
                    if (result == null) {
                        recipe.postValue(Recipe.placeholder());
                    } else {
                        recipe.postValue(result);
                    }
                }
            });
        }
    }
}
