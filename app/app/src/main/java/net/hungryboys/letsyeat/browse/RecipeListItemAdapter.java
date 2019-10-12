package net.hungryboys.letsyeat.browse;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.data.Recipe;

import java.util.Locale;

public class RecipeListItemAdapter extends RecyclerView.Adapter<RecipeListItemAdapter.RecipeListItemViewHolder> {
    private Recipe[] recipes;

    // Provide a reference to the views for each data item
    public static class RecipeListItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView time;
        public TextView difficulty;

        public RecipeListItemViewHolder(CardView v) {
            super(v);
            image = v.findViewById(R.id.recipe_card_image);
            name = v.findViewById(R.id.recipe_card_name);
            time = v.findViewById(R.id.recipe_card_time);
            difficulty = v.findViewById(R.id.recipe_card_difficulty);
        }
    }

    public RecipeListItemAdapter(Recipe[] recipes) {
        this.recipes = recipes;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecipeListItemAdapter.RecipeListItemViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_card, parent, false);

        RecipeListItemViewHolder vh = new RecipeListItemViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecipeListItemViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Recipe recipe = recipes[position];

        holder.name.setText(recipe.getName());
        holder.difficulty.setText(String.format(Locale.getDefault(),"%f", recipe.getDifficulty()));
        holder.time.setText(recipe.getTimeString());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return recipes.length;
    }
}

