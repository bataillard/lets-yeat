package net.hungryboys.letsyeat.data.model;

public class Ingredient {
    private final String name;
    private final double quantity;
    private final String unit;

    public static Ingredient placeholder() {
        return new Ingredient("Cheese", 3000.0, "ton");
    }

    public Ingredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }


}