package net.hungryboys.letsyeat.data;

import net.hungryboys.letsyeat.data.model.LoggedInUser;
import net.hungryboys.letsyeat.APICalls.RESTcalls.user;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Response;
import com.squareup.picasso.Callback;
import android.util.Log;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<user> login(String email, String password) {
        user fakeUser = new user(email, password);
        try {
            // TODO: handle loggedInUser authentication
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public Result<user> register(String email, String password) {
        try {
            // TODO: handle loggedInUser authentication
            user fakeUser = new user( email, password);
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
