package net.hungryboys.letsyeat.data;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Locale;


public class Ingredient {

    @SerializedName("name")
    @Expose
    private final String name;

    @SerializedName("quantity")
    @Expose
    private final String quantity;

    @SerializedName("unit")
    @Expose
    private final String unit;

    public static Ingredient placeholder() {
        return new Ingredient("Cheese", "3000.0", "ton");
    }

    public Ingredient(String name, String quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public String getQuantity() {
        return quantity == null ? "some" : quantity;
    }

    public String getUnit() {
        return unit == null ?  "" : unit;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return  quantity.equals(that.quantity) &&
                name.equals(that.name) &&
                unit.equals(that.unit);
    }

    @Override
    public int hashCode() {
        Object[] fields = {name, quantity, unit};
        return Arrays.hashCode(fields);
    }

    @Override
    @NonNull
    public String toString() {
        return String.format(Locale.getDefault(), "%s %s - %s", getQuantity(), getUnit(), getName());
    }
}