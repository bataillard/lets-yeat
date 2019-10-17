package net.hungryboys.letsyeat.data.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser implements Serializable {

    private String userId;
    private String displayName;

    public LoggedInUser(String userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    @NonNull
    public String toString() {
        return "LoggedInUser{" +
                "userId='" + userId + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
