package net.hungryboys.letsyeat.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Locale;


/**
 * Immutable class representing a recipe, as received by HTTP API
 */
public class Recipe {
    /** Maximum value for difficulty */
    public static final double MAX_DIFF = 5f;
    /** Minimum value for difficulty */
    public static final double MIN_DIFF = 0f;

    @SerializedName("id")
    @Expose
    private final RecipeID id;

    @SerializedName("name")
    @Expose
    private final String name;

    @SerializedName("pictureUrl")
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

    /**
     * @return a new Recipe with placeholder fields created
     */
    public static Recipe placeholder() {
        Ingredient[] ingredients =
                {Ingredient.placeholder(), Ingredient.placeholder(), Ingredient.placeholder()};
        String[] tags = {"italian", "beef", "cheap"};
        String[] instructions = {"Buy ingredients", "Cook for a certain amount of time", "Eat food"};

        return new Recipe(RecipeID.placeholder(),
                "Pasta Bolognese", "", 170, 3.0,
                ingredients, instructions,tags);
    }

    /**
     * Create a new recipe
     * @param id server id of recipe
     * @param name title of recipe
     * @param pictureUrl url to picture of recipe
     * @param time time in minutes it takes to cook recipe
     * @param difficulty difficulty of recipe
     * @param ingredients list of ingredients
     * @param instructions list of instruction
     * @param tags tags relevant to recipe
     */
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

    /**
     * @return RecipeID of recipe
     */
    public RecipeID getId() {
        return id;
    }

    /**
     * @return title of recipe
     */
    public String getName() {
        return name;
    }

    /**
     * @return url to picture of recipe
     */
    public String getPictureUrl() {
        return pictureUrl;
    }

    /**
     * @return cook time of recipe in minutes
     */
    public int getTime() {
        return time;
    }

    /**
     * @return copy of instructions
     */
    public String[] getInstructions() {
        return Arrays.copyOf(instructions, instructions.length);
    }

    /**
     * @return the time to cook recipe formatted as "HH:MM"
     */
    public String getTimeString() {
        return String.format(Locale.getDefault(),"%02d:%02d", time / 60,  time % 60);
    }

    /**
     * @return difficulty of recipe
     */
    public double getDifficulty() {
        return difficulty;
    }

    /**
     * @return copy of list of ingredients
     */
    public Ingredient[] getIngredients() {
        return Arrays.copyOf(ingredients, ingredients.length);
    }

    /**
     * @return copy of list of tags
     */
    public String[] getTags() {
        return Arrays.copyOf(tags, tags.length);
    }
}
