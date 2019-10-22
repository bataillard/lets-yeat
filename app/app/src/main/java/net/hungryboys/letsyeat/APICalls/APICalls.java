package net.hungryboys.letsyeat.APICalls;

import net.hungryboys.letsyeat.data.model.Recipe;
import net.hungryboys.letsyeat.data.model.RecipeID;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface APICalls {

    @GET("/recipe/id")
    Call<Recipe> getRecipe(@Query("id") RecipeID recipeID);

    @GET("/recipe/suggest")
    Call<RecipeID> getRecipeSuggestion();

    @GET("/test")
    Call<String> getTest();

    @POST("/addUser")
    Call<Integer> addUser(@Query("email") String email,@Query("password") String password, @Query("diff") int diff, @Query("pref") List<String> pref, @Query("cooktime") int cooktime);

    @GET("/checkUser")
    Call<String> checkUser (@Query("email") String email, @Query("password") String password);
}

