package net.hungryboys.letsyeat.data;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Locale;

/**
 * Immutable class that models an Ingredient for a Recipe
 */
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

    /**
     * @return a new placeholder Ingredient with sample data, to be used in UI testing
     */
    public static Ingredient placeholder() {
        return new Ingredient("Cheese", "3000.0", "ton");
    }

    /**
     * Creates a new Ingredient
     * @param name name of the ingredient
     * @param quantity quantity as a string, will have been computed server-side
     * @param unit unit of quantity
     */
    public Ingredient(String name, String quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    /**
     * @return name of ingredient
     */
    public String getName() {
        return name;
    }

    /**
     * @return quantity or "some" if quantity is null
     */
    public String getQuantity() {
        return quantity == null ? "some" : quantity;
    }

    /**
     * @return the unit used or "" if null
     */
    public String getUnit() {
        return unit == null ?  "" : unit;
    }

    /**
     * Compares this and another object, if both are the same ingredients (same name, qty, unit)
     * then returns true
     * @param o other object
     * @return true if both are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return  quantity.equals(that.quantity) &&
                name.equals(that.name) &&
                unit.equals(that.unit);
    }

    /**
     * @return a hashcode for this ingredient
     */
    @Override
    public int hashCode() {
        Object[] fields = {name, quantity, unit};
        return Arrays.hashCode(fields);
    }

    /**
     * @return a formatted version of the ingredient
     */
    @Override
    @NonNull
    public String toString() {
        return String.format(Locale.getDefault(), "%s %s - %s", getQuantity(), getUnit(), getName());
    }
}