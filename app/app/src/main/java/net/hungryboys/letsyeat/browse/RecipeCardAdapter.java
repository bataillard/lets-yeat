package net.hungryboys.letsyeat.browse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.data.RecipeID;
import net.hungryboys.letsyeat.data.RecipeStub;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeCardAdapter extends RecyclerView.Adapter<RecipeCardAdapter.RecipeListItemViewHolder> {
    private List<Recipe> recipes;

    // Provide a reference to the views for each data item
    public static class RecipeListItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView time;
        public TextView difficulty;

        public RecipeListItemViewHolder(CardView v) {
            super(v);
            card = v;
            image = v.findViewById(R.id.recipe_card_image);
            name = v.findViewById(R.id.recipe_card_name);
            time = v.findViewById(R.id.recipe_card_time);
            difficulty = v.findViewById(R.id.recipe_card_difficulty);
        }
    }

    public interface RecipeOnSelectListener {
        void onSelect(RecipeID recipeID);
    }

    public RecipeCardAdapter() {
        recipes = new ArrayList<>();
    }

    public void setRecipes(List<RecipeStub> recipeList) {
        recipes.clear();
        recipes.addAll(recipeList);
        notifyDataSetChanged();
    }

    public void setOnSelectListener(RecipeOnSelectListener listener) {
        this.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecipeCardAdapter.RecipeListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                         int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elem_recipe_card, parent, false);

        RecipeListItemViewHolder vh = new RecipeListItemViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecipeListItemViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final RecipeStub recipe = recipes.get(position);

        holder.name.setText(recipe.getName());
        holder.difficulty.setText(String.format(Locale.getDefault(),"%.1f", recipe.getDifficulty()));
        holder.time.setText(recipe.getTimeString());

        if (position == 0) {
            setCardMargin(holder, TOP_CARD_MARGIN);
        } else {
            setCardMargin(holder, 0f);
        }

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSelect(recipe.getId());
                }
            }
        });

        holder.image.setImageResource(R.drawable.placeholder_recipe_photo); // TODO get photo from recipe
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return recipes.size();
    }

    private static void setCardMargin(ViewHolder holder, float dp) {
        float scale = holder.card.getResources().getDisplayMetrics().density;
        int margin_px = (int) (dp * scale + 0.5f);

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.card.getLayoutParams();
        params.setMargins(params.leftMargin, margin_px, params.rightMargin, params.bottomMargin);
        holder.card.setLayoutParams(params);
    }
}

