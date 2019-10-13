package net.hungryboys.letsyeat.browse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.data.model.Recipe;

import java.util.List;

public class BrowseActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecipeCardAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);



        recyclerView = (RecyclerView) findViewById(R.id.browse_recipe_list);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
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
    }
}
