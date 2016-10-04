package com.quickblox.chatviewcontroller.sample;

import android.app.Application;

import com.quickblox.chatviewcontroller.sample.utils.Consts;
import com.quickblox.core.QBSettings;


public class App extends Application {

    private static App instance;

    public static synchronized App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        QBSettings.getInstance().init(getApplicationContext(), Consts.APP_ID, Consts.AUTH_KEY, Consts.AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(Consts.ACCOUNT_KEY);
    }
}
