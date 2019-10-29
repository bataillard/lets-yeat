package net.hungryboys.letsyeat.registration;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.api.APICaller;
import net.hungryboys.letsyeat.data.RegistrationChoice;
import net.hungryboys.letsyeat.data.User;
import net.hungryboys.letsyeat.login.LoginRepository;
import net.hungryboys.letsyeat.login.LoginResult;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;

/**
 * ViewModel that handles data and asynchronous registration server requests. Builds a
 * RegistrationChoice as the activity advances then sends it to server along with User to be
 * registered
 */
public class RegistrationViewModel extends ViewModel {

    public static final String TAG_REGISTRATION_VM = "RegistrationViewModel";

    private MutableLiveData<LoginResult> registrationResult = new MutableLiveData<>();

    private RegistrationChoice.Builder choice = new RegistrationChoice.Builder();
    private Set<String> selectedTags = new HashSet<>();
    private User user;

    /**
     * @return an observable LoginResult which is the result of the login request made to server
     * once registration is finished
     */
    public LiveData<LoginResult> getRegistrationResult() {
        return registrationResult;
    }

    /**
     * Saves the User object passed to LoginActivity, will be used to make registration request
     * @param user User object containing tokens, emails and secrets to be sent to server
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Alert view model that selected tag status has changed
     * @param tag tag that that changed status
     * @param selected new status of tag
     */
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

    /**
     * Alert the ViewModel that the difficulty selected by user has changed
     * @param difficulty the new difficulty
     */
    public void difficultyChanged(double difficulty) {
        choice.setDifficulty(difficulty);
    }

    /**
     * Alert the ViewModel that the time selected by user has changed
     * @param time the new time
     */
    public void timeChanged(Calendar time) {
        choice.setTime(time);
    }

    /**
     * Signals to the ViewModel that user has finished choosing his preferences and that a registration
     * request should be made to server. This is done asynchronously and the login result LiveData
     * will be updated when finished
     */
    public void finish() {
        final User user = this.user;

        RegistrationChoice finalChoice = choice.build();

        Call<LoginResult> call = APICaller.getApiCall().register(user, finalChoice);
        call.enqueue(new retrofit2.Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if (response.isSuccessful()) {
                    LoginResult login = response.body();

                    if (login.isSuccess() && !login.needsRegistration()) {
                        LoginRepository.getInstance().saveUserCredentials(user, response.body());
                    }

                    registrationResult.postValue(response.body());
                } else {
                    Log.e(TAG_REGISTRATION_VM, "Registration failed" + response.message());
                    registrationResult.postValue(LoginResult.failure(R.string.registration_failed));
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Log.e(TAG_REGISTRATION_VM, "Call to register failed", t);
                registrationResult.postValue(LoginResult.failure(R.string.registration_failed));
            }
        });
    }
}
