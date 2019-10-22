package net.hungryboys.letsyeat.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.UUID;


/**
 * Immutable class representing a recipe, as received by HTTP API
 */
public class Recipe {
    public static final double MAX_DIFF = 5f;
    public static final double MIN_DIFF = 0f;

    @SerializedName("id")
    @Expose
    private final RecipeID id;

    @SerializedName("name")
    @Expose
    private final String name;

    @SerializedName("pictureURL")
    @Expose
    private final String pictureUrl;

    @SerializedName("time")
    @Expose
    private final int time;

    @SerializedName("difficulty")
    @Expose
    private final double difficulty;

    @SerializedName("ingredients")
    @Expose
    private final Ingredient[] ingredients;

    @SerializedName("tags")
    @Expose
    private final String[] tags;

    @SerializedName("instructions")
    @Expose
    private final String[] instructions;

    public static Recipe placeholder() {
        Ingredient[] ingredients =
                {Ingredient.placeholder(), Ingredient.placeholder(), Ingredient.placeholder()};
        String[] tags = {"italian", "beef", "cheap"};
        String[] instructions = {"Buy ingredients", "Cook for a certain amount of time", "Eat food"};

        return new Recipe(RecipeID.placeholder(),
                "Pasta Bolognese", "", 170, 3.0,
                ingredients, instructions,tags);
    }

    public Recipe(RecipeID id, String name, String pictureUrl, int time, double difficulty,
                  Ingredient[] ingredients, String[] instructions, String[] tags) {
        this.id = id;
        this.name = name;
        this.pictureUrl = pictureUrl;
        this.time = time;
        this.difficulty = difficulty;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.tags = tags;
    }

    public RecipeID getId() {
        return id;
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

    public String[] getInstructions() {
        return Arrays.copyOf(instructions, instructions.length);
    }

    public String getTimeString() {
        return (time / 60) + ":" + (time % 60);
    }

    public double getDifficulty() {
        return difficulty;
    }

    public Ingredient[] getIngredients() {
        return Arrays.copyOf(ingredients, ingredients.length);
    }

    public String[] getTags() {
        return Arrays.copyOf(tags, tags.length);
    }
}
