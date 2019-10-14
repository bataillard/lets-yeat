package net.hungryboys.letsyeat.browse;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

import net.hungryboys.letsyeat.R;

public class TagButton extends AppCompatButton {

    private boolean checked = false;

    public TagButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

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

    public boolean isChecked() {
        return checked;
    }
}
