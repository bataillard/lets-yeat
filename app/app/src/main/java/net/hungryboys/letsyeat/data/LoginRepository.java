package net.hungryboys.letsyeat.data;

import net.hungryboys.letsyeat.data.model.LoggedInUser;
import net.hungryboys.letsyeat.APICalls.RESTcalls.user;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private user user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(user user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public user getLoggedInUser() {
        return user;
    }

    public Result<user> login(String email, String password) {
        // handle login
        Result<user> result = dataSource.login(email, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<user>) result).getData());
        }
        return result;
    }

    public Result<user> register(String username, String password) {
        Result<user> result = dataSource.register(username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<user>) result).getData());
        }
        return result;
    }
}
