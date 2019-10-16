package net.hungryboys.letsyeat.data.model;

import java.util.Arrays;
import java.util.UUID;


/**
 * Immutable class representing a recipe, as received by HTTP API
 */
public class Recipe {
    private final RecipeID id;
    private final String url;
    private final String name;
    private final String pictureUrl;
    private final int time;
    private final double difficulty;
    private final Ingredient[] ingredients;
    private final String[] tags;
    private final String[] instructions;

    public static Recipe placeholder() {
        Ingredient[] ingredients =
                {Ingredient.placeholder(), Ingredient.placeholder(), Ingredient.placeholder()};
        String[] tags = {"italian", "beef", "cheap"};
        String[] instructions = {"Buy ingredients", "Cook for a certain amount of time", "Eat food"};

        return new Recipe(RecipeID.placeholder(),
                "https://www.budgetbytes.com/the-best-weeknight-pasta-sauce/",
                "Pasta Bolognese", "", 170, 3.0,
                ingredients, instructions,tags);
    }

    public Recipe(RecipeID id, String url, String name, String pictureUrl, int time, double difficulty,
                  Ingredient[] ingredients, String[] instructions, String[] tags) {
        this.id = id;
        this.url = url;
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

    public String getUrl() {
        return url;
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
