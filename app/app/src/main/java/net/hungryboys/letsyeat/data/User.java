package net.hungryboys.letsyeat.data;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Immutable class that represents a User login attempt. This object will be sent to server who will
 * determine if user can login and reply with a {@link net.hungryboys.letsyeat.login.LoginResult}
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


    /**
     * Creates a new User that logs in with email and password
     * @param email email of user
     * @param password user password
     * @param firebaseToken firebase token used to send notifications to device
     */
    public User(String email, String password, String firebaseToken) {
        fromGoogle = false;

        this.email = email;
        this.secret = password;
        this.firebaseToken = firebaseToken;

    }

    /**
     * Creates a new User that logs in via GoogleSignIn, where the secret (i.e. password) is the
     * server auth code supplied by google
     * @param account google account return by google sign-in procedure
     * @param firebaseToken firebase token used to send notifications to device
     */
    public User(GoogleSignInAccount account, String firebaseToken) {
        fromGoogle = true;

        this.email = account.getEmail();
        this.secret = account.getServerAuthCode();
        this.firebaseToken = firebaseToken;
    }

    /**
     * @return true if this user is logged in via google
     */
    public boolean fromGoogle() {
        return fromGoogle;
    }

    /**
     * @return email of user
     */
    public String getEmail() {
        return email;
    }
}
