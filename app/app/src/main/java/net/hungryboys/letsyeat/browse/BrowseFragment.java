package net.hungryboys.letsyeat.browse;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.data.model.Recipe;

import java.util.List;

public class BrowseFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecipeCardAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }

        rootView = inflater.inflate(R.layout.fragment_browse, container, false);

        recyclerView = rootView.findViewById(R.id.browse_recipe_list);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new RecipeCardAdapter();
        recyclerView.setAdapter(mAdapter);

        BrowseViewModel  model = ViewModelProviders.of(this).get(BrowseViewModel.class);
        model.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                mAdapter.setRecipes(recipes);
            }
        });

        return rootView;
    }


}
