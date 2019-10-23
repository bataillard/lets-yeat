package net.hungryboys.letsyeat.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.hungryboys.letsyeat.data.User;

/**
 * Authentication result : success (user details) or error message.
 */
public class LoginResult {

    @Expose
    @SerializedName("success")
    private boolean success;

    @Expose
    @SerializedName("loggedIn")
    private boolean loggedIn;

    @Expose
    @SerializedName("needsRegistration")
    private boolean needsRegistration;

    @Expose
    @SerializedName("serverAuthToken")
    private String serverAuthToken;

    @StringRes private Integer errorString;

    @Nullable private User user;

    public static LoginResult failure(@StringRes Integer errorString) {
        return new LoginResult(false, false,
                null, errorString);
    }

    public static LoginResult success(boolean needsRegistration, String serverAuthToken) {
        return new LoginResult(true, needsRegistration, serverAuthToken, null);
    }

    private LoginResult(boolean success, boolean needsRegistration,
                        String serverAuthToken, @StringRes Integer errorString) {
        this.success = success;
        this.needsRegistration = needsRegistration;
        this.serverAuthToken = serverAuthToken;
        this.errorString = errorString;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean needsRegistration() {
        return needsRegistration;
    }

    public String getServerAuthToken() {
        return serverAuthToken;
    }

    @Nullable
    public Integer getErrorString() {
        return errorString;
    }

    @Nullable
    public User getUser() {
        return user;
    }

    void setUser(@NonNull User user) {
        this.user = user;
    }
}
