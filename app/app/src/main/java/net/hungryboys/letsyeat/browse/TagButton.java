package net.hungryboys.letsyeat.browse;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

import net.hungryboys.letsyeat.R;

/**
 * Wrapper class around Button to emulate ToggleButton, except with 3 states, one for an open tag
 * grid, one for closed tag grid & no tags selected (white), one for closed tag grid & tags selected
 */
public class TagButton extends AppCompatButton {

    private boolean checked = false;

    public TagButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Toggles button from one state to another. Changes appearance of button
     * @param tagsSelected true if any tags are selected
     */
    public void toggle(boolean tagsSelected) {
        checked = !checked;

        if (checked) {
            setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.browse_tags_button_on));
        } else if (tagsSelected){
            setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.browse_tags_button_off_select));
        } else {
            setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.browse_tags_button_off_no_select));
        }
    }

    /**
     * @return true if button is toggled
     */
    public boolean isChecked() {
        return checked;
    }
}
