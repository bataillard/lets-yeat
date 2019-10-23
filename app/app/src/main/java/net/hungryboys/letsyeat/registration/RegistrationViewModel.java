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

public class RegistrationViewModel extends ViewModel {

    public static final String TAG_REGISTRATION_VM = "RegistrationViewModel";

    private MutableLiveData<LoginResult> registrationResult = new MutableLiveData<>();

    private RegistrationChoice.Builder choice = new RegistrationChoice.Builder();
    private Set<String> selectedTags = new HashSet<>();
    private User user;

    public LiveData<LoginResult> getRegistrationResult() {
        return registrationResult;
    }

    public void setUser(User user) {
        this.user = user;
    }

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

    public void finish() {
        final User user = this.user;

        RegistrationChoice finalChoice = choice.build();

        Call<LoginResult> call = APICaller.getApiCall().register(user, finalChoice);
        call.enqueue(new retrofit2.Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if (response.isSuccessful()) {
                    registrationResult.postValue(response.body());
                    LoginRepository.getInstance().saveUserCredentials(user, response.body());
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
