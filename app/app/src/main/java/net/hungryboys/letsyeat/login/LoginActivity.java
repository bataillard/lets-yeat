package net.hungryboys.letsyeat.login;


import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.browse.BrowseActivity;
import net.hungryboys.letsyeat.data.User;
import net.hungryboys.letsyeat.registration.RegistrationActivity;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG_LOGIN = "LoginActivity";
    public static final int RC_SIGN_IN = 1;

    private LoginViewModel loginViewModel;
    private GoogleSignInClient googleSignInClient;


    private Button loginButton;
    private Button registerButton;
    private Button googleLoginButton;

    private EditText passwordEditText;
    private EditText usernameEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                loginResultChanged(loginResult);
            }
        });

        setupFirebase();
        initializeGoogleSignIn();
        setupEditTexts();
        setupLoginButtons();
    }

    private void setupFirebase() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(
            new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    InstanceIdResult result = task.getResult();
                    if (result != null) {
                        loginViewModel.setFirebaseToken(result.getToken());
                    } else {
                        Log.e(TAG_LOGIN, "Could not retrieve firebase token");
                    }
                }
            });
    }

    private void initializeGoogleSignIn() {
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupEditTexts() {
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);

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
    }

    private void setupLoginButtons() {
        loginButton = findViewById(R.id.email_login_btn);
        registerButton = findViewById(R.id.register_btn);
        googleLoginButton = findViewById(R.id.sign_in_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginViewModel.register(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });

        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    private void loginResultChanged(LoginResult loginResult) {
        if (loginResult == null) {
            return;
        }

        if (loginResult.isSuccess() && !loginResult.needsRegistration()) {
            changeToBrowse();
        } else if (loginResult.isSuccess() && loginResult.needsRegistration()) {
            changeToRegistration(loginResult.getUser());
        } else {
            showLoginFailed(loginResult.getErrorString());
        }
    }

    private void changeToRegistration(User user) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        intent.putExtra(RegistrationActivity.EXTRA_USER_DATA, user);
        startActivity(intent);
        finish();
    }

    private void changeToBrowse() {
        Intent intent = new Intent(this, BrowseActivity.class);
        startActivity(intent);
        finish();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            loginViewModel.loginFromGoogle(task);
        }

    }
}
