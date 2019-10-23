package net.hungryboys.letsyeat.browse;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.hungryboys.letsyeat.api.APICaller;
import net.hungryboys.letsyeat.data.RecipeStub;
import net.hungryboys.letsyeat.login.LoginRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrowseViewModel extends ViewModel {
    private final static String TAG_BROWSE_VM = "BrowseViewModel";

    private MutableLiveData<List<RecipeStub>> recipes;

    private Set<String> selectedTags = new HashSet<>();
    private String searchText = "";

    private final int NUM_RECIPES = 25;

    public LiveData<List<RecipeStub>> getRecipeStubs() {
        if (recipes == null) {
            recipes = new MutableLiveData<>();
            loadRecipes();
        }

        return recipes;
    }

    public boolean hasTagsSelected() {
        return selectedTags.size() > 0;
    }

    public void searchTextChanged(String search) {
        String old = searchText;
        searchText = search;

        if (!searchText.equals(old)) {
            search();
        }
    }

    public void tagChanged(String tag, boolean selected) {
        Set<String> previous = new HashSet<>(selectedTags);

        if (selected) {
            selectedTags.add(tag);
        } else {
            selectedTags.remove(tag);
        }

        if (!selectedTags.equals(previous)) {
            search();
        }
    }

    private void search() {
        LoginRepository login = LoginRepository.getInstance();
        List<String> tags = new ArrayList<>(selectedTags);

        if (login.isLoggedIn()) {
            Call<List<RecipeStub>> call = APICaller.getApiCall().getRecipeList(
                    login.getUserEmail(), NUM_RECIPES, searchText, tags);

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
