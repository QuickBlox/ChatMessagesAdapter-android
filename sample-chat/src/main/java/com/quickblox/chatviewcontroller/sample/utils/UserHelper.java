package com.quickblox.chatviewcontroller.sample.utils;

import android.os.Bundle;
import android.util.Log;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

class UserHelper {
    private static final String TAG = UserHelper.class.getSimpleName();

    static void getUsers(final QBEntityCallback<ArrayList<QBUser>> callback) {

        ArrayList<Integer> usersIds = new ArrayList<>();
        usersIds.add(Consts.userOneID);
        usersIds.add(Consts.userTwoID);
        QBUsers.getUsersByIDs(usersIds, null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                callback.onSuccess(users, params);
            }

            @Override
            public void onError(QBResponseException errors) {
                callback.onError(errors);
            }
        });
    }
}
