package net.hungryboys.letsyeat.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.UUID;

/**
 * Immutable class that represents an ID to a recipe
 */
public class RecipeID implements Serializable {

    @Expose
    @SerializedName("id")
    private final String id;

    /**
     * @return a new placeholder Recipe with a random ID
     */
    public static RecipeID placeholder() {
        return new RecipeID(UUID.randomUUID().toString());
    }

    /**
     * Creates a new RecipeID
     * @param id ID used by server to represent recipe
     */
    public RecipeID(String id) {
        this.id = id;
    }

    /**
     * @return the ID as a string
     */
    public String getId() {
        return id;
    }
}
