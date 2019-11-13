package net.hungryboys.letsyeat.api.bodies;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.hungryboys.letsyeat.data.RecipeID;

import java.util.Date;

public class NotificationBody {
    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("id")
    @Expose
    private RecipeID id;

    @SerializedName("time")
    @Expose
    private Date date;

    public NotificationBody(String email, RecipeID id, Date date) {
        this.email = email;
        this.id = id;
        this.date = date;
    }
}
