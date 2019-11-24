package net.hungryboys.letsyeat.api.bodies;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.hungryboys.letsyeat.data.RecipeID;

public class NotificationBody {
    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("id")
    @Expose
    private RecipeID id;

    @SerializedName("time")
    @Expose
    private String date;

    public NotificationBody(String email, RecipeID id, String date) {
        this.email = email;
        this.id = id;
        this.date = date;
    }
}
