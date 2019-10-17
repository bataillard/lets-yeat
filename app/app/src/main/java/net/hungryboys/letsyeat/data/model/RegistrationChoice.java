package net.hungryboys.letsyeat.data.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;

public class RegistrationChoice implements Serializable {

    private final String[] tags;
    private final Calendar time;
    private final double difficulty;

    public static final int DEFAULT_HOUR = 19;
    public static final int DEFAULT_MINUTES = 30;
    public static final double DEFAULT_DIFFICULTY = 3.0;

    public static class Builder implements Serializable {
        private String[] tags = new String[0];
        private Calendar time;
        private double difficulty = DEFAULT_DIFFICULTY;

        public Builder() {
            this.time = Calendar.getInstance();
            time.set(Calendar.HOUR_OF_DAY, DEFAULT_HOUR);
            time.set(Calendar.MINUTE, DEFAULT_MINUTES);
        }

        public void setTags(@NonNull String[] tags) {
            this.tags = Arrays.copyOf(tags, tags.length);
        }

        public void setTime(@NonNull Calendar time) {
            this.time = (Calendar) time.clone();
        }

        public void setDifficulty(double difficulty) {
            this.difficulty = difficulty;
        }

        public RegistrationChoice build() {
            return new RegistrationChoice(tags, time, difficulty);
        }
    }

    public RegistrationChoice(@NonNull String[] tags, @NonNull Calendar time, double difficulty) {
        this.tags = Arrays.copyOf(tags, tags.length);
        this.time = (Calendar) time.clone();
        this.difficulty = difficulty;
    }

    public String[] getTags() {
        return Arrays.copyOf(tags, tags.length);
    }

    public Calendar getTime() {
        return (Calendar) time.clone();
    }

    public double getDifficulty() {
        return difficulty;
    }

    @Override
    @NonNull
    public String toString() {
        return "RegistrationChoice{" +
                "tags=" + Arrays.toString(tags) +
                ", time=" + time +
                ", difficulty=" + difficulty +
                '}';
    }
}
