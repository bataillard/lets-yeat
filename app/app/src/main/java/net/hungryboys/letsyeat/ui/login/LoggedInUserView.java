package net.hungryboys.letsyeat.ui.login;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import net.hungryboys.letsyeat.data.model.RegistrationChoice;
import net.hungryboys.letsyeat.data.model.LoggedInUser;

/**
 * Class exposing authenticated user details to the UI.
 */
public class LoggedInUserView implements Parcelable {
    private LoggedInUser user;
    private RegistrationChoice choice;

    public static final Parcelable.Creator<LoggedInUserView> CREATOR = new Creator<LoggedInUserView>() {
        @Override
        public LoggedInUserView createFromParcel(Parcel source) {
            return new LoggedInUserView(source);
        }

        @Override
        public LoggedInUserView[] newArray(int size) {
            return new LoggedInUserView[size];
        }
    };

    LoggedInUserView(LoggedInUser user) {
        this.user = user;
    }

    private LoggedInUserView(Parcel in) {
        user = (LoggedInUser) in.readSerializable();
        choice = (RegistrationChoice) in.readSerializable();
    }

    public String getDisplayName() {
        return user.getDisplayName();
    }

    public String getUserID() {
        return user.getUserId();
    }

    public RegistrationChoice getChoice() {
        return choice;
    }

    public void setChoice(@NonNull RegistrationChoice choice) {
        this.choice = choice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(user);
        dest.writeSerializable(choice);
    }

    @Override
    @NonNull
    public String toString() {
        return "LoggedInUserView{" +
                "user=" + user +
                ", choice=" + choice +
                '}';
    }
}
