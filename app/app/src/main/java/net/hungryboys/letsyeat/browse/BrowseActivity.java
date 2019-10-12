package net.hungryboys.letsyeat.browse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.data.model.Recipe;


public class BrowseActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        Recipe[] recipes = new Recipe[30];
        for (int i = 0; i < 30; i++){
            recipes[i] = Recipe.placeholder();
        }

        recyclerView = (RecyclerView) findViewById(R.id.browse_recipe_list);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new RecipeCardAdapter(recipes);
        recyclerView.setAdapter(mAdapter);
    }
}
