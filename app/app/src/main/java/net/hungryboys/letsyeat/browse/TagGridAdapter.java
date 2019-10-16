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

public class TagGridAdapter extends ListAdapter<String, TagGridAdapter.ViewHolder> {

    private CompoundButton.OnCheckedChangeListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ToggleButton toggleButton;

        public ViewHolder(ToggleButton button) {
            super(button);
            toggleButton = button;
        }
    }

    public TagGridAdapter(String[] tags) {
        super(CALLBACK);

        List<String> tagList = new ArrayList<>();
        Collections.addAll(tagList, tags);
        submitList(tagList);
    }



    public TagGridAdapter(List<String> tags) {
        super(CALLBACK);
        submitList(tags);
    }

    public void setListener(CompoundButton.OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        ToggleButton tb = (ToggleButton) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elem_tag_grid_button, parent, false);

        return new ViewHolder(tb);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String tag = getItem(position);
        holder.toggleButton.setText(tag);
        holder.toggleButton.setTextOff(tag);
        holder.toggleButton.setTextOn(tag);
        holder.toggleButton.setOnCheckedChangeListener(listener);
    }

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
