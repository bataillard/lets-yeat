package net.hungryboys.letsyeat.ui.login;


import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import net.hungryboys.letsyeat.MyApplication;

import net.hungryboys.letsyeat.browse.BrowseActivity;
import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.data.model.LoggedInUser;
import net.hungryboys.letsyeat.registration.RegistrationActivity;
import net.hungryboys.letsyeat.APICalls.RESTcalls.user;
import java.io.Serializable;
import net.hungryboys.letsyeat.ui.login.LoginViewModel;
import net.hungryboys.letsyeat.ui.login.LoginViewModelFactory;

import retrofit2.Call;
import retrofit2.Response;
import com.squareup.picasso.Callback;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    public static final int RC_SIGN_IN = 1;
    private user curUser;
    public static final String LOGIN_TAG = "LoginActivityLog";
    public int loggedin = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.email_login_btn);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        final Button googleLoginButton = findViewById(R.id.sign_in_button);
        final Button registerButton = findViewById(R.id.register_btn);

        //following google instructions:
        //set onClickListener
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                curUser = new user(usernameEditText.getText().toString(), passwordEditText.getText().toString());
//                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
//                intent.putExtra("userEmail", curUser.name);
//                intent.putExtra("userpassword", curUser.password);
//                startActivity(intent);

                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.register(curUser.email,
                        curUser.password);
            }
        });

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                        updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curUser = new user(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                Call<String> call = net.hungryboys.letsyeat.APICalls.CreateRetrofit.getApiCall().checkUser(curUser.email, curUser.password);
                call.enqueue(new retrofit2.Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.w("Logging in", "Login successful" + response.code());
                        if(response.code() == 200){
                            Log.w("logged in", "we in the best part");
                            loggedin = 1;
                        }
                        loadingProgressBar.setVisibility(View.VISIBLE);
                        loginViewModel.login(curUser.email,
                                curUser.password);
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.w("Logging in", "failed");
                        loadingProgressBar.setVisibility(View.VISIBLE);
                        loginViewModel.login(curUser.email,
                                curUser.password);
                    }

                });


            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        Log.w("changing screens", "********************");
        if (loggedin == 0) {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            intent.putExtra(RegistrationActivity.EXTRA_USER_DATA, model);
            intent.putExtra("userEmail", curUser.email);
            intent.putExtra("sec", curUser.password);
            String pass = curUser.password;
            Log.w("testing", pass);

            startActivity(intent);
//            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
//            intent.putExtra(RegistrationActivity.EXTRA_USER_DATA, model);
//            intent.putExtra("userEmail", curUser.email);
//            intent.putExtra("userpassword", curUser.password);
//            startActivity(intent);
        } else {
            Log.w("skipping setup", "skipped tags");
            Intent intent = new Intent(LoginActivity.this, BrowseActivity.class);
            intent.putExtra(BrowseActivity.EXTRA_USER_DATA, model);
            startActivity(intent);
//            // Pass user details to new activity and start it
//            Intent intent = new Intent(LoginActivity.this, BrowseActivity.class);
//            intent.putExtra(BrowseActivity.EXTRA_USER_DATA, model);
//            startActivity(intent);
        }
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        LoggedInUserView result;
        try {
            String email = completedTask.getResult(ApiException.class).getEmail();
            String name = completedTask.getResult(ApiException.class).getGivenName();
            String id = completedTask.getResult(ApiException.class).getIdToken();
            result = new LoggedInUserView(new user(email, "john doe"));
        } catch (NullPointerException e) {
            Log.w(LOGIN_TAG, "signInResult:fail, null pointer exception");
            showLoginFailed(null);
            return;
        } catch (ApiException e) {
            Log.w(LOGIN_TAG, "signInResult:failed code=" + e.getStatusCode());
            showLoginFailed(null);
            return;
        }

        updateUiWithUser(result);
    }
}
