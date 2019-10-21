package net.hungryboys.letsyeat.ui.login;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import net.hungryboys.letsyeat.data.model.RegistrationChoice;
import net.hungryboys.letsyeat.data.model.LoggedInUser;
import net.hungryboys.letsyeat.APICalls.RESTcalls.user;

/**
 * Class exposing authenticated user details to the UI.
 */
public class LoggedInUserView implements Parcelable {
    private user user;
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

    LoggedInUserView(user user) {
        this.user = user;
    }

    private LoggedInUserView(Parcel in) {
        user = (user) in.readSerializable();
        choice = (RegistrationChoice) in.readSerializable();
    }

    public String getDisplayName() {
        return user.email;
    }

    public String getUserID() {
        return "john doe";
    }

    public RegistrationChoice getChoice() {
        return choice;
    }

    public void setChoice(@NonNull RegistrationChoice choice) {
        Log.w("debugging101", "free me please");

        this.choice = choice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.w("debugging101", "free me please");
        //dest.writeSerializable(user);
        //dest.writeSerializable(choice);
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
