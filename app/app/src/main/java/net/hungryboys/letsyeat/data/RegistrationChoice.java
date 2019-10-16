package net.hungryboys.letsyeat.data;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.sql.Time;
import java.util.Arrays;

public class RegistrationChoice implements Serializable {

    private final String[] tags;
    private final Time time;
    private final float difficulty;

    public static class Builder implements Serializable {
        private String[] tags;
        private Time time;
        private float difficulty;

        public void setTags(String[] tags) {
            this.tags = Arrays.copyOf(tags, tags.length);
        }

        public void setTime(Time time) {
            this.time = new Time(time.getTime());
        }

        public void setDifficulty(float difficulty) {
            this.difficulty = difficulty;
        }

        public RegistrationChoice build() {
            if (tags == null || time == null) {
                throw new IllegalStateException("Builder must have tags and time set before building");
            } else {
                return new RegistrationChoice(tags, time, difficulty);
            }
        }
    }

    public RegistrationChoice(@NonNull String[] tags, @NonNull Time time, float difficulty) {
        this.tags = Arrays.copyOf(tags, tags.length);
        this.time = new Time(time.getTime());
        this.difficulty = difficulty;
    }

    public String[] getTags() {
        return Arrays.copyOf(tags, tags.length);
    }

    public Time getTime() {
        return new Time(time.getTime());
    }

    public float getDifficulty() {
        return difficulty;
    }

}
