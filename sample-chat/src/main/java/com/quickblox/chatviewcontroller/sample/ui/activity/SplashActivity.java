package com.quickblox.chatviewcontroller.sample.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.quickblox.chatviewcontroller.R;
import com.quickblox.chatviewcontroller.sample.utils.ChatHelper;
import com.quickblox.chatviewcontroller.sample.utils.Consts;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        loginToChat();
    }

    private void loginToChat() {
        QBUser qbUser = new QBUser(Consts.userTwoLogin, Consts.userPassword);

        ChatHelper.getInstance().login(qbUser, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                Log.d(TAG, "login to chat onSuccess ");
                ChatActivity.start(SplashActivity.this);
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "onError " + e.getMessage());
            }
        });
    }
}