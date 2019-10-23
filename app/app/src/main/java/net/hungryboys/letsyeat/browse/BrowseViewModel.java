package net.hungryboys.letsyeat.browse;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.hungryboys.letsyeat.api.APICaller;
import net.hungryboys.letsyeat.data.RecipeStub;
import net.hungryboys.letsyeat.login.LoginRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        LoginRepository login = LoginRepository.getInstance();

        if (login.isLoggedIn()) {
            Call<List<RecipeStub>> call = APICaller.getApiCall().getRecipeList(
                    login.getUserEmail(), NUM_RECIPES);
            call.enqueue(new Callback<List<RecipeStub>>() {
                @Override
                public void onResponse(Call<List<RecipeStub>> call, Response<List<RecipeStub>> response) {
                    if (response.isSuccessful()) {
                        recipes.postValue(response.body());
                    } else {
                        Log.e(TAG_BROWSE_VM, "Could not get recipe stubs " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<RecipeStub>> call, Throwable t) {
                    Log.e(TAG_BROWSE_VM, "Could not get recipe stubs", t);
                }
            });
        }


    }
}
