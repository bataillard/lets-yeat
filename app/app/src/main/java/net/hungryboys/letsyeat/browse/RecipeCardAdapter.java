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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Custom RecyclerView Adapter that holds the list of RecipeStubs, displays them as CardViews and
 * handles eventual clicks on each Card
 */
public class RecipeCardAdapter extends RecyclerView.Adapter<RecipeCardAdapter.ViewHolder> {

    private static final float TOP_CARD_MARGIN = 65.0f;

    private List<RecipeStub> recipes;
    private RecipeOnSelectListener listener;

    /**
     * Inner ViewHolder class for a single RecipeStub, containing the layout for a single CardView
     * This class will be accessed to change the view's contents to suit the correct recipe when a
     * user scrolls to that position
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView card;
        public ImageView image;
        public TextView name;
        public TextView time;
        public TextView difficulty;

        public ViewHolder(CardView v) {
            super(v);
            card = v;
            image = v.findViewById(R.id.recipe_card_image);
            name = v.findViewById(R.id.recipe_card_name);
            time = v.findViewById(R.id.recipe_card_time);
            difficulty = v.findViewById(R.id.recipe_card_difficulty);
        }
    }

    /**
     * Interface that specifies the listener that will be called when a card is
     * clicked. Such an object is passed in from the activity
     */
    public interface RecipeOnSelectListener {
        void onSelect(RecipeID recipeID);
    }

    /**
     * Creates a new RecipeCardAdapter with empty contents
     */
    public RecipeCardAdapter() {
        recipes = new ArrayList<>();
    }

    /**
     * Sets the list of recipes in the adapter to passed in list
     * @param recipeList new list of recipe stubs
     */
    public void setRecipes(List<RecipeStub> recipeList) {
        recipes.clear();
        recipes.addAll(recipeList);
        notifyDataSetChanged();
    }

    /**
     * Sets the listener that will be called when a card is clicked, if listener is null, nothing
     * will occur on click
     * @param listener the new listener to be called
     */
    public void setOnSelectListener(RecipeOnSelectListener listener) {
        this.listener = listener;
    }

    /**
     * Create new ViewHolder for the RecylcerView, invoked by RV LayoutManager, only inflates the
     * layout without populating the data, which is {@link this.onBindViewHolder}'s job
     * @param parent parent ViewGroup of new ViewHolder
     * @param viewType view type of new view (ignored in this case)
     * @return a new ViewHolder containing the inflated CardView
     */
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elem_recipe_card, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /**
     * Gets element of list of recipe stubs at given position and sets view contained in ViewHolder
     * to reflect that given recipe stub
     * @param holder ViewHolder to be modified
     * @param position position of element in data set
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RecipeStub recipe = recipes.get(position);

        holder.name.setText(recipe.getName());
        holder.difficulty.setText(String.format(Locale.getDefault(),"%.1f", recipe.getDifficulty()));
        holder.time.setText(recipe.getTimeString());

        if (position == 0) {
            setCardTopMargin(holder, TOP_CARD_MARGIN);
        } else {
            setCardTopMargin(holder, 0f);
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

    /**
     * @return size of data set (i.e. number of recipe stubs)
     */
    @Override
    public int getItemCount() {
        return recipes.size();
    }

    private static void setCardTopMargin(ViewHolder holder, float topMarginDp) {
        final float EXTRA_CONVERSION_MARGIN = 0.5f;

        // Convert dp into px
        float scale = holder.card.getResources().getDisplayMetrics().density;
        int topMarginPx = (int) (topMarginDp * scale + EXTRA_CONVERSION_MARGIN);

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.card.getLayoutParams();
        params.setMargins(params.leftMargin, topMarginPx, params.rightMargin, params.bottomMargin);
        holder.card.setLayoutParams(params);
    }
}

