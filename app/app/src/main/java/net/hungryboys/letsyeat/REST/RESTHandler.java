package net.hungryboys.letsyeat.REST;

import net.hungryboys.letsyeat.data.model.RecipeID;

import org.json.JSONObject;

//
// This Class implements a REST API for the application
//
public class RESTHandler {

    private String server_address;

    //gets a single recipe
    public JSONObject getRecipe(RecipeID recipeID){

        JSONObject result;
        String resource = new String("");

        HttpGetRequest request = new HttpGetRequest(server_address, )

        return null;
    }

    //gets a single random recipe
    public JSONObject getRecipeSuggestion(){
        return null;
    }

    //gets a list of recipes
    public JSONObject getRecipeList(){
        return null;
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


}
