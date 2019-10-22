package net.hungryboys.letsyeat.navigation;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.hungryboys.letsyeat.APICalls.CreateRetrofit;
import net.hungryboys.letsyeat.data.model.RecipeID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavigationViewModel extends ViewModel {

    public static final String TAG_NAV = "NavigationViewModel";

    private MutableLiveData<RecipeID> recipeId;

    public LiveData<RecipeID> getRecipeId() {
        if (recipeId == null) {
            recipeId = new MutableLiveData<>();
            loadSuggestion();
        }

        return recipeId;
    }

    public void yeetClicked(){
       loadSuggestion();
    }

    private void loadSuggestion() {
        Call<RecipeID> call = CreateRetrofit.getApiCall().getRecipeSuggestion();
        call.enqueue(new Callback<RecipeID>() {
            @Override
            public void onResponse(Call<RecipeID> call, Response<RecipeID> response) {
                if (response.isSuccessful()) {
                    recipeId.postValue(response.body());
                } else {
                    Log.e(TAG_NAV, "Could not get recipe suggestion" + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<RecipeID> call, Throwable t) {
                Log.e(TAG_NAV, "Could not get recipe suggestion", t);
            }
        });
    }
}
