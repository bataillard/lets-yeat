package net.hungryboys.letsyeat.navigation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.hungryboys.letsyeat.REST.RESTHandler;
import net.hungryboys.letsyeat.data.LoginRepository;
import net.hungryboys.letsyeat.data.model.LoggedInUser;
import net.hungryboys.letsyeat.data.model.RecipeID;
import net.hungryboys.letsyeat.APICalls.RESTcalls.user;

public class NavigationViewModel extends ViewModel {

    private MutableLiveData<RecipeID> recipeId;

    public LiveData<RecipeID> getRecipeId() {
        if (recipeId == null) {
            recipeId = new MutableLiveData<>();
            loadSuggestion();
        }

        return recipeId;
    }

    private void loadSuggestion() {
        LoginRepository loginRepository = LoginRepository.getInstance(null);

        if (loginRepository.isLoggedIn()) {
            user user = loginRepository.getLoggedInUser();

//            RESTHandler.getRecipeSuggestion(user, new RESTHandler.RequestHandler<RecipeID>() {
//                @Override
//                public void onRequestFinished(RecipeID result) {
//                    recipeId.postValue(result);
//                }
//            });
        }
    }
}
