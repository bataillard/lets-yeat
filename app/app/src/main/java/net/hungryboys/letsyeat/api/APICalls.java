package net.hungryboys.letsyeat.api;

import net.hungryboys.letsyeat.api.bodies.NotificationBody;
import net.hungryboys.letsyeat.api.bodies.RegistrationBody;
import net.hungryboys.letsyeat.data.Recipe;
import net.hungryboys.letsyeat.data.RecipeID;
import net.hungryboys.letsyeat.data.RecipeStub;
import net.hungryboys.letsyeat.data.RegistrationChoice;
import net.hungryboys.letsyeat.data.User;
import net.hungryboys.letsyeat.login.LoginResult;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Defines all REST calls to server made by this app
 */
public interface APICalls {

    /* Recipe API Calls */

    @GET("/recipe/id")
    Call<Recipe> getRecipe(@Query("id") String id);

    @GET("/recipe/suggest")
    Call<RecipeID> getRecipeSuggestion(@Query("email") String email);

    @GET("/recipe/list")
    Call<List<RecipeStub>> getRecipeList(@Query("email") String email,
                                         @Query("max") int max);

    @GET("/recipe/list")
    Call<List<RecipeStub>> getRecipeList(@Query("email") String email,
                                         @Query("max") int max,
                                         @Query("search") String search,
                                         @Query("tags") List<String> tags);


    /* Notification / Firebase API Calls */

    @POST("/notification/new")
    Call<String> registerNotification(@Body NotificationBody notificationBody);


    @PATCH("/user/token")
    Call<String> updateFirebaseToken(@Query("email") String email, @Query("token") String token);


    /* Authentication API Calls */

    @POST("/user/login")
    Call<LoginResult> login(@Body User user);

    @POST("/user/register")
    Call<LoginResult> register(@Body RegistrationBody body);
}

