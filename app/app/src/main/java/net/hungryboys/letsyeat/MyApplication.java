package net.hungryboys.letsyeat;

import android.app.Application;
import net.hungryboys.letsyeat.APICalls.RESTcalls.user;

public class MyApplication extends Application {

    private user curUser;

    public user getCurUser() {
        return curUser;
    }

    public void setCurUser(user curUser) {
        this.curUser = curUser;
    }

}
