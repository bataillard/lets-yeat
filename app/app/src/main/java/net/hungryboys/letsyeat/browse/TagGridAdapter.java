package net.hungryboys.letsyeat.browse;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import net.hungryboys.letsyeat.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Custom RecyclerView ListAdapter that holds the list of tags, displays them as ToggleButtons and
 * handles clicks on individual buttons. Using ListAdapter instead of plain RecyclerView.Adapter
 * allows the item clicked listener to be handled for us.
 */
public class TagGridAdapter extends ListAdapter<String, TagGridAdapter.ViewHolder> {

    private CompoundButton.OnCheckedChangeListener listener;

    /**
     * Inner ViewHolder class for a single tag, containing the layout for a single TagButton (i.e
     * the ToggleButton
     * This class will be accessed to change the view's contents to suit the correct tag when a
     * user scrolls to that position
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ToggleButton toggleButton;

        public ViewHolder(ToggleButton button) {
            super(button);
            toggleButton = button;
        }
    }

    /**
     * Creates a new TagGridAdapter with given array of tags
     * @param tags the tags to be displayed
     */
    public TagGridAdapter(String[] tags) {
        super(CALLBACK);

        List<String> tagList = new ArrayList<>();
        Collections.addAll(tagList, tags);
        submitList(tagList);
    }

    /**
     * Creates a new TagGridAdapter with given array of tags
     * @param tags the tags to be displayed
     */
    public TagGridAdapter(List<String> tags) {
        super(CALLBACK);
        submitList(tags);
    }

    /**
     * @param listener the new listener for each button click
     */
    public void setListener(CompoundButton.OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    /**
     * Create new ViewHolder for the RecylcerView, invoked by RV LayoutManager, only inflates the
     * layout without populating the data, which is {@link this.onBindViewHolder}'s job
     * @param parent parent ViewGroup of new ViewHolder
     * @param viewType view type of new view (ignored in this case)
     * @return a new ViewHolder containing the inflated ToggleButton
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        ToggleButton tb = (ToggleButton) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elem_tag_grid_button, parent, false);

        return new ViewHolder(tb);
    }

    /**
     * Gets element of list of tags at given position and sets toggle button inside supplied
     * ViewHolder to that tag
     * @param holder ViewHolder to be modified
     * @param position position of element in data set
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String tag = getItem(position);
        holder.toggleButton.setText(tag);
        holder.toggleButton.setTextOff(tag);
        holder.toggleButton.setTextOn(tag);
        holder.toggleButton.setOnCheckedChangeListener(listener);
    }

    /**
     * Callback used by ListAdapter to determine if two items in list are the same and if their
     * contents are the same. In this case, the same applies for both methods
     */
    public static final DiffUtil.ItemCallback<String> CALLBACK = new DiffUtil.ItemCallback<String>() {
        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }
    };
}
