package net.hungryboys.letsyeat.data.model;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class Ingredient {
    private final String name;
    private final String quantity;
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
        return quantity;
    }

    public String getUnit() {
        return unit;
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
        return String.format(Locale.getDefault(), "%.1f %s - %s", quantity, unit, name);
    }
}