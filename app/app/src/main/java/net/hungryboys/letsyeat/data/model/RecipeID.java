package net.hungryboys.letsyeat.data.model;

import java.util.UUID;

public class RecipeID {
    private final UUID id;

    public static RecipeID placeholder() {
        return new RecipeID(UUID.fromString("placeholder"));
    }

    public RecipeID(UUID id) {
        this.id = id;
    }
}
