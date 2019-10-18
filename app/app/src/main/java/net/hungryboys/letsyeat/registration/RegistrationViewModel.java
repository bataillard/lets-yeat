package net.hungryboys.letsyeat.registration;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import net.hungryboys.letsyeat.REST.RESTHandler;
import net.hungryboys.letsyeat.REST.Response;
import net.hungryboys.letsyeat.data.LoginRepository;
import net.hungryboys.letsyeat.data.model.LoggedInUser;
import net.hungryboys.letsyeat.data.model.RegistrationChoice;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class RegistrationViewModel extends ViewModel {

    public static final String TAG = "REGISTRATION";

    // this is just for MVP
    private final String password = "password";

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

    public void difficultyChanged(double difficulty) {
        choice.setDifficulty(difficulty);
    }

    public void timeChanged(Calendar time) {
        choice.setTime(time);
    }

    public RegistrationChoice finish() {
        RegistrationChoice finalChoice = choice.build();
        LoginRepository loginRepository = LoginRepository.getInstance(null);

        if (loginRepository.isLoggedIn()) {
            LoggedInUser user = loginRepository.getLoggedInUser();

            //
            // Password placeholder used for MVP
            //
            RESTHandler.postUser(user.getUserId(), password, finalChoice,new RESTHandler.RequestHandler<Response>() {
                @Override
                public void onRequestFinished(Response result) {
                    Log.d(TAG,"Registered completed with server response: " + result.getResponseCode());
                }
            });
        }

        return finalChoice;
    }

}
