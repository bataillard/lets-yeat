package net.hungryboys.letsyeat.APICalls.RESTcalls;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.hungryboys.letsyeat.MyApplication;

public class user {
    @SerializedName("password")
    @Expose
    public String password;

    @SerializedName("email")
    @Expose
    public String email;

    public user(String email, String password){
        this.password = password;
        this.email = email;
    }


    public user startUser(String email, String password){
        user curUser = new user(email, password);
        return curUser;
    }
}
