package net.hungryboys.letsyeat.recipe;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.hungryboys.letsyeat.APICalls.CreateRetrofit;
import net.hungryboys.letsyeat.APICalls.RESTcalls.user;
import net.hungryboys.letsyeat.REST.RESTHandler;
import net.hungryboys.letsyeat.data.LoginRepository;
import net.hungryboys.letsyeat.data.model.Recipe;
import net.hungryboys.letsyeat.data.model.RecipeID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeViewModel extends ViewModel {
    private static final String TAG_RECIPE = "RecipeViewModel";

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
        if (id != null) {
            user user = LoginRepository.getInstance(null).getLoggedInUser();
            Call<String> call = CreateRetrofit.getApiCall().registerNotification(user.email, id);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.d(TAG_RECIPE, "Cook confirmed");
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(TAG_RECIPE, "Could not confirm cook", t);
                }
            });


        }
    }

    private void loadRecipe() {
        Log.w("TESTINGBUGS", "In loadrecipe");
        if (id != null) {
            String resID = id.getId();
            Call<Recipe> call = CreateRetrofit.getApiCall().getRecipe(resID);
            call.enqueue(new Callback<Recipe>() {
                @Override
                public void onResponse(Call<Recipe> call, Response<Recipe> response) {
                    if (response.isSuccessful()) {
                        Log.w("DEBUG_APP", "in response of loadRecipe" +response.body().getName());

                        recipe.postValue(response.body());
                        Log.w("DEBUG_APP", "leaving response of loadRecipe");
                    } else {
                        Log.e(TAG_RECIPE, "Could not load recipe" + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<Recipe> call, Throwable t) {
                    Log.e(TAG_RECIPE, "Could not load recipe", t);
                }
            });
        }
    }
}
