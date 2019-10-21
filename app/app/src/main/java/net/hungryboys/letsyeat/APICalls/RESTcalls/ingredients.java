package net.hungryboys.letsyeat.APICalls.RESTcalls;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ingredients {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("quantity")
    @Expose
    public int quantity;
    @SerializedName("unit")
    @Expose
    public String unit;


    public ingredients(String name, int quantity, String unit){
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }
}
