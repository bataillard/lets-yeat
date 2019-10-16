package net.hungryboys.letsyeat.data.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
public final class RecipeStub implements Parcelable {
    private final RecipeID id;
    private final String url;
    private final String name;
    private final String pictureUrl;
    private final int time;
    private final double difficulty;

    public static RecipeStub placeholder() {
        return new RecipeStub(RecipeID.placeholder(),
                "https://www.budgetbytes.com/the-best-weeknight-pasta-sauce/",
                "Pasta Bolognese", "", 170, 3.0);
    }

    public RecipeStub(RecipeID id, String url, String name, String pictureUrl, int time, double difficulty) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.pictureUrl = pictureUrl;
        this.time = time;
        this.difficulty = difficulty;
    }

    private RecipeStub(Parcel in) {
        Bundle bundle = in.readBundle(getClass().getClassLoader());

        id = (RecipeID) bundle.getSerializable("id");
        url = bundle.getString("url");
        name = bundle.getString("name");
        pictureUrl = bundle.getString("pictureUrl");
        time = bundle.getInt("time");
        difficulty = bundle.getDouble("difficulty");
    }

    public RecipeID getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public int getTime() {
        return time;
    }

    public double getDifficulty() {
        return difficulty;
    }

    public String getTimeString() {
        return (time / 60) + ":" + (time % 60);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle(getClass().getClassLoader());
        bundle.putSerializable("id", id);
        bundle.putString("url", url);
        bundle.putString("name", name);
        bundle.putString("pictureUrl", pictureUrl);
        bundle.putInt("time", time);
        bundle.putDouble("difficulty", difficulty);

        dest.writeBundle(bundle);
    }

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
}
