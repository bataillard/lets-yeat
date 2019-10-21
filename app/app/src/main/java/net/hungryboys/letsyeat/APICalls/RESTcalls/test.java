package net.hungryboys.letsyeat.APICalls.RESTcalls;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class test implements Serializable {

    @SerializedName("test")
    @Expose
    public String test;


    public test( String test){
        this.test = test;
    }

}
