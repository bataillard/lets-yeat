package net.hungryboys.letsyeat.APICalls;

import net.hungryboys.letsyeat.APICalls.RESTcalls.Recipe;
import net.hungryboys.letsyeat.APICalls.RESTcalls.test;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface APICalls {

    @GET("/recipe")
    Call<Recipe> getOneRecipe();

    @GET("/test")
    Call<String> getTest();

    @POST("/addUser")
    Call<Integer> addUser(@Query("email") String email,@Query("password") String password, @Query("diff") int diff, @Query("pref") List<String> pref, @Query("cooktime") int cooktime);

    @GET("/checkUser")
    Call<String> checkUser (@Query("email") String email, @Query("password") String password);
}

