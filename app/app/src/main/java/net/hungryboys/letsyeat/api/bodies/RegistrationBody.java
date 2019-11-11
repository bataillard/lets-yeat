package net.hungryboys.letsyeat.api.bodies;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.hungryboys.letsyeat.data.RegistrationChoice;
import net.hungryboys.letsyeat.data.User;

public class RegistrationBody {
    @Expose
    @SerializedName("user")
    private final User user;

    @Expose
    @SerializedName("choice")
    private final RegistrationChoice choice;

    public RegistrationBody(User user, RegistrationChoice choice) {
        this.user = user;
        this.choice = choice;
    }
}
