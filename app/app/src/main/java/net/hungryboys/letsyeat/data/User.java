package net.hungryboys.letsyeat.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class User implements Serializable {

    @Expose
    @SerializedName("fromGoogle")
    private boolean fromGoogle;

    @Expose
    @SerializedName("email")
    private String email;

    @Expose
    @SerializedName("secret")
    private String secret;

    @Expose
    @SerializedName("firebaseToken")
    private String firebaseToken;



    public User(String email, String password, String firebaseToken) {
        fromGoogle = false;

        this.email = email;
        this.secret = password;
        this.firebaseToken = firebaseToken;

    }

    public User(GoogleSignInAccount account, String firebaseToken) {
        fromGoogle = true;

        this.email = account.getEmail();
        this.secret = account.getServerAuthCode();
        this.firebaseToken = firebaseToken;
    }



    public boolean fromGoogle() {
        return fromGoogle;
    }

    public String getEmail() {
        return email;
    }
}
