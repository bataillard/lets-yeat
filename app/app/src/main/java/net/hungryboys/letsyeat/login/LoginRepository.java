package net.hungryboys.letsyeat.login;

import net.hungryboys.letsyeat.data.User;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    public static final String TAG_LOGIN_REPO = "LoginRepository";

    private static volatile LoginRepository instance;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private User user = null;
    private String serverAuthToken = null;

    // private constructor : singleton access
    private LoginRepository() {}

    public static LoginRepository getInstance() {
        if (instance == null) {
            instance = new LoginRepository();
        }

        return instance;
    }

    public void saveUserCredentials(User user, LoginResult body) {
        this.user = user;
        this.serverAuthToken = body.getServerAuthToken();
    }

    public boolean isLoggedIn() {
        return user != null && serverAuthToken != null;
    }

    public void removeUserCredentials() {
        user = null;
        serverAuthToken = null;
    }

    public String getServerAuthToken() {
        return serverAuthToken;
    }

    public String getUserEmail() {
        return user == null ? null : user.getEmail();
    }
}
