package net.hungryboys.letsyeat.data.model;

import java.io.Serializable;
import java.util.UUID;

public class RecipeID implements Serializable {
    private final String id;

    public static RecipeID placeholder() {
        return new RecipeID(UUID.randomUUID().toString());
    }

    public RecipeID(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
