package net.hungryboys.letsyeat.APICalls.RESTcalls;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Recipe {
    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("pic")
    @Expose
    public String pic;
    @SerializedName("time")
    @Expose
    public int time;
    @SerializedName("ingredients")
    @Expose
    public List<ingredients> ingredients;
    @SerializedName("instructions")
    @Expose
    public  List<String> instructions;
    @SerializedName("tags")
    @Expose
    public List<String> tags;
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("difficulty")
    @Expose
    public int diff;

    public Recipe(String url, String name, String pic, int time, List<ingredients> ingredients, List<String> instructions, List<String> tags, int id, int diff){
        this.url = url;
        this.name = name;
        this.pic = pic;
        this.time = time;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.tags = tags;
        this.id = id;
        this.diff = diff;
    }
}
