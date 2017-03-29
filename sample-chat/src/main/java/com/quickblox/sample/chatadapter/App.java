package com.quickblox.sample.chatadapter;

import android.app.Application;

import com.quickblox.auth.session.QBSettings;
import com.quickblox.sample.chatadapter.utils.Consts;


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
