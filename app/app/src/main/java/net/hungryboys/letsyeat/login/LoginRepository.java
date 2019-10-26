package net.hungryboys.letsyeat.login;

import net.hungryboys.letsyeat.data.User;

/**
 * Singleton class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credential information.
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

    /**
     * @return the current instance of the login repository
     */
    public static LoginRepository getInstance() {
        if (instance == null) {
            instance = new LoginRepository();
        }

        return instance;
    }

    /**
     * Saves user credentials to memory (ev. keystore)
     * @param user the user that was logged in (contains email/secret)
     * @param body the result from the server (contains auth token)
     */
    public void saveUserCredentials(User user, LoginResult body) {
        this.user = user;
        this.serverAuthToken = body.getServerAuthToken();
    }

    /**
     * @return true if user has credentials saved
     */
    public boolean isLoggedIn() {
        return user != null && serverAuthToken != null;
    }

    /**
     * Removes user credentials saved, after logout
     */
    public void removeUserCredentials() {
        user = null;
        serverAuthToken = null;
    }

    /**
     * @return the auth token used for communication with the server
     */
    public String getServerAuthToken() {
        return serverAuthToken;
    }

    /**
     * @return the email of the user or null if not logged in
     */
    public String getUserEmail() {
        return user == null ? null : user.getEmail();
    }
}
