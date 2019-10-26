package net.hungryboys.letsyeat.data;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

/**
 * Immutable class representing a Recipe Stub, used in browse activity, avoids sending all
 * recipe information at once, contains only essential data to be shown in list
 * Implements Parcelable so it can be passed around in app efficiently
 */
public final class RecipeStub implements Parcelable {
    private final RecipeID id;
    private final String name;
    private final String pictureUrl;
    private final int time;
    private final double difficulty;

    /**
     * @return a new placeholder RecipeStub with sample data
     */
    public static RecipeStub placeholder() {
        return new RecipeStub(RecipeID.placeholder(),
                "Pasta Bolognese", "", 170, 3.0);
    }

    /**
     * Creates a new recipe stub
     * @param id id of recipe
     * @param name title of recipe
     * @param pictureUrl url to picture for recipe
     * @param time time in minutes to cook
     * @param difficulty difficulty of recipe
     */
    public RecipeStub(RecipeID id, String name, String pictureUrl, int time, double difficulty) {
        this.id = id;
        this.name = name;
        this.pictureUrl = pictureUrl;
        this.time = time;
        this.difficulty = difficulty;
    }

    /**
     * @return RecipeID of recipe
     */
    public RecipeID getId() {
        return id;
    }

    /**
     * @return title of recipe
     */
    public String getName() {
        return name;
    }

    /**
     * @return url to picture of recipe
     */
    public String getPictureUrl() {
        return pictureUrl;
    }

    /**
     * @return cook time of recipe in minutes
     */
    public int getTime() {
        return time;
    }

    /**
     * @return the time to cook recipe formatted as "HH:MM"
     */
    public String getTimeString() {
        return String.format(Locale.getDefault(),"%2d:%2d", time / 60,  time % 60);
    }

    /**
     * @return difficulty of recipe
     */
    public double getDifficulty() {
        return difficulty;
    }

    /**
     * @return used by parcelable, ignored in this case
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes given recipe stub into supplied parcel
     * @param dest parcel to be written to
     * @param flags flags for parcel
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle(getClass().getClassLoader());
        bundle.putSerializable("id", id);
        bundle.putString("name", name);
        bundle.putString("pictureUrl", pictureUrl);
        bundle.putInt("time", time);
        bundle.putDouble("difficulty", difficulty);

        dest.writeBundle(bundle);
    }

    /**
     * Creator for unpacking Parcel into RecipeStub
     */
    public static final Parcelable.Creator<RecipeStub> CREATOR = new Parcelable.Creator<RecipeStub>() {
        @Override
        public RecipeStub createFromParcel(Parcel source) {
            return new RecipeStub(source);
        }

        @Override
        public RecipeStub[] newArray(int size) {
            return new RecipeStub[size];
        }
    };

    // Used to create new recipe from Parcel
    private RecipeStub(Parcel in) {
        Bundle bundle = in.readBundle(getClass().getClassLoader());

        id = (RecipeID) bundle.getSerializable("id");
        name = bundle.getString("name");
        pictureUrl = bundle.getString("pictureUrl");
        time = bundle.getInt("time");
        difficulty = bundle.getDouble("difficulty");
    }
}
