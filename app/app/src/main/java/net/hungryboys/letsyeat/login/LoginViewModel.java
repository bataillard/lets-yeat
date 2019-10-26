package net.hungryboys.letsyeat.login;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import net.hungryboys.letsyeat.api.APICaller;
import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.data.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * View model for {@link LoginActivity}. Handles data such a firebaseToken. Makes request to auth
 * server asynchronously on behalf of the Activity, then updates the LiveData observable with the
 * login result
 */
public class LoginViewModel extends ViewModel {

    public static final String TAG_LOGIN_VM = "LoginViewModel";

    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private String firebaseToken;

    /**
     * @return an observable LoginResult containing the results of the login attempt
     */
    LiveData<LoginResult> getLoginResult() {
        // If user already in logged in repository, supply a login success directly instead of
        // querying server
        LoginRepository login = LoginRepository.getInstance();

        if (login.isLoggedIn()) {
            loginResult.setValue(LoginResult.success(false, login.getServerAuthToken()));
        }

        return loginResult;
    }

    /**
     * Attempt login with provided email and password, performs request asynchronously then updates
     * the LiveData observable. Only performs login request if firebase token has been set
     * @param email email of user
     * @param password password of user
     */
    public void login(String email, String password) {
        if (firebaseToken != null) {
            User user = new User(email, password, firebaseToken);
            loginToServer(user);
        } else {
            Log.e(TAG_LOGIN_VM, "Firebase Token is null");
        }
    }

    /**
     * Perform login to server using the data provided by GoogleSignIn. performs request asynchronously
     * then updates the LiveData observable. Only performs login request if firebase token has been set
     * @param completedLogin the google login task returned by GSI activity
     */
    public void loginFromGoogle(Task<GoogleSignInAccount> completedLogin) {
        if (firebaseToken == null) {
            Log.e(TAG_LOGIN_VM, "Firebase token is null");
            return;
        }

        try {
            GoogleSignInAccount account = completedLogin.getResult(ApiException.class);

            if (account != null) {
                User user = new User(account, firebaseToken);
                loginToServer(user);
            } else {
                Log.e(TAG_LOGIN_VM, "Account null while Google Sign In");
                loginResult.postValue(LoginResult.failure(R.string.google_login_failed));
            }
        } catch (ApiException e) {
            Log.e(TAG_LOGIN_VM, "Api exception while Google Sign In", e);
            loginResult.postValue(LoginResult.failure(R.string.google_login_failed));
        }
    }

    private void loginToServer(final User user) {
        // Actually perform API call to server, add user to LoginResult when it returns.
        // If success, save user to LoginRepository

        Call<LoginResult> call = APICaller.getApiCall().login(user);
        call.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if (response.isSuccessful()) {
                    LoginResult result = response.body();
                    result.setUser(user);

                    loginResult.postValue(response.body());
                    LoginRepository.getInstance().saveUserCredentials(user, result);
                } else {
                    Log.e(TAG_LOGIN_VM, "Incorrect response from server " + response.message());
                    loginResult.postValue(LoginResult.failure(R.string.login_failed));
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Log.e(TAG_LOGIN_VM, "Call to server failed: ", t);
                loginResult.postValue(LoginResult.failure(R.string.login_failed));
            }
        });
    }

    /**
     * Performs registration request to server, i.e. tries to login {@see LoginViewModel.login}
     * @param email email of user
     * @param password password of user
     */
    public void register(String email, String password) {
        login(email, password);
    }

    /**
     * Informs ViewModel of firebase token, so that it can be sent with the login request later
     * @param token firebase token as string
     */
    public void setFirebaseToken(String token) {
        this.firebaseToken = firebaseToken;
    }
}
