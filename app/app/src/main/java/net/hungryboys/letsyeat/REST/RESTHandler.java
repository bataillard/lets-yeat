package net.hungryboys.letsyeat.REST;

import android.util.Log;

import net.hungryboys.letsyeat.data.model.Ingredient;
import net.hungryboys.letsyeat.data.model.LoggedInUser;
import net.hungryboys.letsyeat.data.model.Recipe;
import net.hungryboys.letsyeat.data.model.RecipeID;
import net.hungryboys.letsyeat.data.model.RecipeStub;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

//
// This Class implements a REST API for the application
//
public class RESTHandler {

    private static final String TAG_REST = "REST HANDLER";

    private static final String SERVER_ADDRESS = "0.0.0.0";
    private static final String RECIPE_ID = "/recipe/ID";
    private static final String RECIPE_SUGGEST = "/recipe/suggest";
    private static final String RECIPE_LIST = "/recipe/list";

    public interface RequestHandler<T> {
        void onRequestFinished(T result);
    }

    //gets a single recipe
    public static void getRecipe(RecipeID recipeID, final RequestHandler<Recipe> handler){
        String url = SERVER_ADDRESS + RECIPE_ID + "/" + recipeID.getId();

        HttpGetRequest.OnGetResponseHandler getResponseHandler = new HttpGetRequest.OnGetResponseHandler() {
            @Override
            public void onGetResponse(HttpGetRequest.Response result) {
                if (result.isValid()) {
                    Recipe recipe = parseRecipeFromJSON(result.getContent());

                    if (recipe == null) {
                        Log.e(TAG_REST, "Error while parsing json, could not parse: "
                                + result.getContent());
                    }

                    handler.onRequestFinished(recipe);
                } else {
                    Log.e(TAG_REST, "Error on HTTP response");
                }
            }
        };

        HttpGetRequest request = new HttpGetRequest(getResponseHandler);
        request.execute(url);
    }

    private static Recipe parseRecipeFromJSON(String result) {
        Recipe recipe;

        try {
            JSONObject json = new JSONObject(result);

            String id = json.getString("id");
            String name = json.getString("name");
            String pictureURL = json.getString("pictureURL");
            int time = json.getInt("time");
            double difficulty = json.getDouble("difficulty");
            Ingredient[] ingredients = parseIngredients(json.getJSONArray("ingredients"));

            List<String> instructions = new ArrayList<>();
            JSONArray instructionsJson = json.getJSONArray("instructions");
            for (int i = 0; i < instructionsJson.length(); i++) {
                instructions.add(instructionsJson.getString(i));
            }

            List<String> tags = new ArrayList<>();
            JSONArray tagJson = json.getJSONArray("tags");
            for (int i = 0; i < tagJson.length(); i++) {
                tags.add(tagJson.getString(i));
            }

            recipe = new Recipe(new RecipeID(id), name, pictureURL, time,
                    difficulty, ingredients, (String[]) instructions.toArray(), (String[]) tags.toArray());
        } catch (JSONException e) {
            recipe = null;
        }

        return recipe;
    }

    private static Ingredient[] parseIngredients(JSONArray ingredients) throws JSONException {
        List<Ingredient> ingredientList = new ArrayList<>();

        for (int i = 0; i < ingredients.length(); i++) {
            JSONObject jsonIngredient = ingredients.getJSONObject(i);

            String name = jsonIngredient.getString("name");
            String quantity = jsonIngredient.getString("quantity");
            String unit = jsonIngredient.getString("unit");

            ingredientList.add(new Ingredient(name, quantity, unit));
        }

        return (Ingredient[]) ingredientList.toArray();
    }

    //gets a single random recipe
    public static void getRecipeSuggestion(LoggedInUser user, final RequestHandler<RecipeID> handler){
        String url = SERVER_ADDRESS + RECIPE_SUGGEST + "?userid=" + user.getUserId();

        HttpGetRequest.OnGetResponseHandler getResponseHandler = new HttpGetRequest.OnGetResponseHandler() {
            @Override
            public void onGetResponse(HttpGetRequest.Response result) {
                if (result.isValid()) {
                    RecipeID recipeID = null;

                    try {
                        JSONObject jsonObject = new JSONObject(result.getContent());
                        recipeID = new RecipeID(jsonObject.getString("recipeID"));
                    } catch (JSONException e) {
                        Log.e(TAG_REST, "Error while parsing json, could not parse: "
                                + result.getContent() + e.getMessage());
                    }

                    handler.onRequestFinished(recipeID);
                } else {
                    Log.e(TAG_REST, "Error on HTTP response");
                }
            }
        };

        HttpGetRequest request = new HttpGetRequest(getResponseHandler);
        request.execute(url);
    }

    //gets a list of recipes
    public static void getRecipeList(Set<String> tags, String searchTerm, int numRecipe, final RequestHandler<List<RecipeStub>> handler){
        StringBuilder url = new StringBuilder(SERVER_ADDRESS + RECIPE_LIST);

        url.append("?SearchTerm=");
        url.append(searchTerm);
        url.append("&numRecipe=");
        url.append(numRecipe);
        if(!tags.isEmpty()) {
            url.append("&tags=");
            for (String tag : tags) {
                url.append(tag);
                url.append(",");
            }

            url.deleteCharAt(url.length() - 1); //delete last comma
        }

        HttpGetRequest.OnGetResponseHandler getResponseHandler = new HttpGetRequest.OnGetResponseHandler() {
            @Override
            public void onGetResponse(HttpGetRequest.Response result) {
                if (result.isValid()) {
                    List<RecipeStub> recipeList = parseRecipeListFromJSON(result.getContent());

                    if (recipeList == null) {
                        Log.e(TAG_REST, "Error while parsing json, could not parse: "
                                + result.getContent());
                    }

                    handler.onRequestFinished(recipeList);
                } else {
                    Log.e(TAG_REST, "Error on HTTP response");
                }
            }
        };

        HttpGetRequest request = new HttpGetRequest(getResponseHandler);
        request.execute(url.toString());
    }

    public static List<RecipeStub> parseRecipeListFromJSON(String result){
        List<RecipeStub> recipeList = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(result);

            JSONArray jsonRecipeList = json.getJSONArray("recipe");

            for(int i = 0; i < jsonRecipeList.length(); i++) {
                JSONObject jsonRecipe = jsonRecipeList.getJSONObject(i);

                RecipeID id = new RecipeID(jsonRecipe.getString("id"));
                String name = jsonRecipe.getString("name");
                String pictureURL = jsonRecipe.getString("pictureURL");
                int time = jsonRecipe.getInt("time");
                double difficulty = jsonRecipe.getDouble("difficulty");

                recipeList.add(new RecipeStub(id, name, pictureURL, time, difficulty));
            }

        } catch (JSONException e) {
            recipeList = null;
        }

        return recipeList;
    }


    //creates a new notification in the server
    public void putNotification(){
    }

    //gets user data from server
    public JSONObject getUSer(){
        return null;
    }

    //creates a new user on the server
    public void putUser(){
    }

    //updates a user on the server
    public void postUser(){
    }

    //gets user login confirmation with server using google login
    public JSONObject getUserLoginGoogle(){
        return null;
    }

    //gets user login confirmation with server using email/pass
    public JSONObject getUserLogin(){
        return null;
    }

    private String buildGetURL(String baseURL, Map<String, String> params) {
        StringBuilder stringURL = new StringBuilder(baseURL);
        stringURL.append("?");

        for (Map.Entry<String, String> kvPair : params.entrySet()) {
            String argName = kvPair.getKey();
            String argValue = kvPair.getValue();

            stringURL.append(argName);
            stringURL.append("=");
            stringURL.append(argValue);
        }

        return stringURL.toString();
    }


}
