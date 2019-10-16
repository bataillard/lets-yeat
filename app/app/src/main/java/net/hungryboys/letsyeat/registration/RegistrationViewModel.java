package net.hungryboys.letsyeat.registration;

import androidx.lifecycle.ViewModel;

import net.hungryboys.letsyeat.data.RegistrationChoice;

import java.util.HashSet;
import java.util.Set;

public class RegistrationViewModel extends ViewModel {

    public static final String TAG = "REGISTRATION";

    private RegistrationChoice.Builder choice = new RegistrationChoice.Builder();
    private Set<String> selectedTags = new HashSet<>();

    public void tagChanged(String tag, boolean selected) {
        Set<String> previous = new HashSet<>(selectedTags);

        if (selected) {
            selectedTags.add(tag);
        } else {
            selectedTags.remove(tag);
        }

        if (!selectedTags.equals(previous)) {
            String[] tags = new String[selectedTags.size()];
            choice.setTags(selectedTags.toArray(tags));
        }
    }

}
