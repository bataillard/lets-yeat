package net.hungryboys.letsyeat.ui.login;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView implements Parcelable {
    private String displayName;
    //... other data fields that may be accessible to the UI

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

    LoggedInUserView(String displayName) {
        this.displayName = displayName;
    }

    private LoggedInUserView(Parcel in) {

    }

    String getDisplayName() {
        return displayName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(displayName);
    }
}
