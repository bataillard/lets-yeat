package net.hungryboys.letsyeat.data.model;

import java.util.Arrays;
import java.util.UUID;


/**
 * Immutable class representing a recipe, as received by HTTP API
 */
public class Recipe {
    public static final double MAX_DIFF = 5f;
    public static final double MIN_DIFF = 0f;

    private final RecipeID id;
    private final String url;
    private final String name;
    private final String pictureUrl;
    private final int time;
    private final double difficulty;
    private final Ingredient[] ingredients;
    private final String[] tags;

    public static Recipe placeholder() {
        Ingredient[] ingredients =
                {Ingredient.placeholder(), Ingredient.placeholder(), Ingredient.placeholder()};
        String[] tags = {"italian", "beef", "cheap"};

        return new Recipe(RecipeID.placeholder(),
                "https://www.budgetbytes.com/the-best-weeknight-pasta-sauce/",
                "Pasta Bolognese", "", 170, 3.0, ingredients, tags);
    }

    public Recipe(RecipeID id, String url, String name, String pictureUrl, int time, double difficulty,
                  Ingredient[] ingredients, String[] tags) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.pictureUrl = pictureUrl;
        this.time = time;
        this.difficulty = difficulty;
        this.ingredients = ingredients;
        this.tags = tags;
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
