package net.hungryboys.letsyeat.recipe;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.hungryboys.letsyeat.api.APICaller;
import net.hungryboys.letsyeat.api.bodies.NotificationBody;
import net.hungryboys.letsyeat.login.LoginRepository;
import net.hungryboys.letsyeat.data.Recipe;
import net.hungryboys.letsyeat.data.RecipeID;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel that handles data for {@link RecipeFragment}. Performs server requests in background and
 * updates its LiveData observable recipe asynchronously
 */
public class RecipeViewModel extends ViewModel {
    private static final String TAG_RECIPE = "RecipeViewModel";

    private MutableLiveData<Recipe> recipe = new MutableLiveData<>();
    private Calendar cookDate = null;
    private RecipeID id;

    /**
     * @return an observable LiveData object containing the recipe that was requested to server
     */
    public LiveData<Recipe> getRecipe() {
        if (recipe == null) {
            recipe = new MutableLiveData<>();
        }

        if (id != null) {
            loadRecipe();
        }

        return recipe;
    }

    /**
     * @param id sets id of recipe that needs to requested from server, performs request in background
     */
    public void setId(@NonNull RecipeID id) {
        if (!id.equals(this.id)) {
            this.id = id;
            loadRecipe();
        }
    }

    /**
     * Signal that user has chosen to cook particular recipe, will alert server so it can send a
     * notification at the correct time
     */
    public void cookConfirm(Context context) {
        if (id == null || !LoginRepository.getInstance(context).isLoggedIn()) {
            Log.i(TAG_RECIPE, "Could not login or id null: " + id);
            return;
        }

        String email = LoginRepository.getInstance(context).getUserEmail();
        String dateString = null;
        if (cookDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date cookTime = cookDate.getTime();
            dateString = sdf.format(cookTime);
        }

        NotificationBody body = new NotificationBody(email, id, dateString);

        Call<String> call = APICaller.getApiCall().registerNotification(body);
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

    public void setCookCustomDate(int year, int month, int dayOfMonth) {
        if (cookDate == null) {
            cookDate = Calendar.getInstance();
        }

        cookDate.set(Calendar.YEAR, year);
        cookDate.set(Calendar.MONTH, month);
        cookDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    }

    public void setCustomTime(int hourOfDay, int minute) {
        if (cookDate == null) {
            cookDate = Calendar.getInstance();
        }

        cookDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cookDate.set(Calendar.MINUTE, minute);
    }

    private void loadRecipe() {
        if (id != null) {
            Call<Recipe> call = APICaller.getApiCall().getRecipe(id.getId());
            call.enqueue(new Callback<Recipe>() {
                @Override
                public void onResponse(Call<Recipe> call, Response<Recipe> response) {
                    if (response.isSuccessful()) {
                        recipe.postValue(response.body());
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
