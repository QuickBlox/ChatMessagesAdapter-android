package com.quickblox.sample.chatadapter.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.sample.chatadapter.R;
import com.quickblox.sample.chatadapter.utils.ChatHelper;
import com.quickblox.sample.chatadapter.utils.Consts;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        loginToQB();
    }


    private void loginToQB() {
        QBUser qbUser = new QBUser(Consts.userTwoLogin, Consts.userPassword);
        qbUser.setId(Consts.userTwoID);
        ChatHelper.getInstance().loginAndGetUsers(qbUser, new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                Log.d(TAG, "loginToQB onSuccess");
                ChatActivity.start(SplashActivity.this, qbUsers);
            }

            @Override
            public void onError(QBResponseException e) {
            }
        });
    }
}