package net.hungryboys.letsyeat.registration;

import android.util.Log;

import androidx.lifecycle.ViewModel;
import retrofit2.Call;
import retrofit2.Response;

import net.hungryboys.letsyeat.REST.RESTHandler;
import net.hungryboys.letsyeat.data.LoginRepository;
import net.hungryboys.letsyeat.data.model.LoggedInUser;
import net.hungryboys.letsyeat.data.model.RegistrationChoice;
import net.hungryboys.letsyeat.APICalls.RESTcalls.user;

import java.util.Arrays;
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

    public RegistrationChoice finish(user curUser) {
        Log.w("debugging101", "free me please2222");
        RegistrationChoice finalChoice = choice.build();
        LoginRepository loginRepository = LoginRepository.getInstance(null);

        if (loginRepository.isLoggedIn()) {
            user user = loginRepository.getLoggedInUser();
            Log.w("debugging101", "free me please3322" + user.email + user.password);
            Log.w("debugging101", "free me please3322" + curUser.email + curUser.password);
            Call<Integer> call = net.hungryboys.letsyeat.APICalls.CreateRetrofit.getApiCall().addUser(curUser.email, curUser.password, (int) finalChoice.getDifficulty(), Arrays.asList(finalChoice.getTags()), 500);
            call.enqueue(new retrofit2.Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    String message = "Successsss";
                    Log.w("goodgood", "changing intent");

                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    String message = "fail";
                    //Intent intent = new Intent(AddCredits.this, PostAddCredit.class);
                    //intent.putExtra("message", message);
                    //showProgress(false);
                    //startActivity(intent);
                    Log.w("doing gods work", "signInResult:fail, null pointer exception");

                }
            });
            //
            // Password placeholder used for MVP
            //
//            RESTHandler.postUser(user.getUserId(), password, finalChoice,new RESTHandler.RequestHandler<Response>() {
//                @Override
//                public void onRequestFinished(Response result) {
//                    Log.d(TAG,"Registered completed with server response: " + result.getResponseCode());
//                }
//            });
        }

        return finalChoice;
    }

}
