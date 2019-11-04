package net.hungryboys.letsyeat.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.hungryboys.letsyeat.data.User;

import java.io.Serializable;

/**
 * Authentication result : success or error message.
 */
public class LoginResult implements Serializable {

    @Expose
    @SerializedName("success")
    private boolean success;

    @Expose
    @SerializedName("needsRegistration")
    private boolean needsRegistration;

    @Expose
    @SerializedName("serverAuthToken")
    private String serverAuthToken;

    @StringRes private Integer errorString;

    @Nullable private User user;

    /**
     * Creates a new login result failure with error message as a String Resource
     * @param errorString the resource id of the error string
     * @return the new LoginResult
     */
    public static LoginResult failure(@StringRes Integer errorString) {
        return new LoginResult(false, false,
                null, errorString);
    }

    /**
     * Creates a new login result success, specifies if user needs further registration
     * @param needsRegistration true if user needs to be registered
     * @param serverAuthToken auth token for server communication
     * @return the new LoginResult
     */
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

    /**
     * @return true if login procedure was a success, i.e. user was logged in or needs to be
     * registered. false if login failed (incorrect password, account exists already, etc...)
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return true if user needs to register before he can login
     */
    public boolean needsRegistration() {
        return needsRegistration;
    }

    /**
     * @return the server auth token used to communicated with server
     */
    public String getServerAuthToken() {
        return serverAuthToken;
    }

    /**
     * @return null or the error string resource id if isSuccess is false
     */
    @Nullable
    public Integer getErrorString() {
        return errorString;
    }

    /**
     * @return the user associated with this login request, can be null
     */
    @Nullable
    public User getUser() {
        return user;
    }

    /**
     * @param user the user that is associated to this login attempt (set by client)
     */
    void setUser(@NonNull User user) {
        this.user = user;
    }
}
