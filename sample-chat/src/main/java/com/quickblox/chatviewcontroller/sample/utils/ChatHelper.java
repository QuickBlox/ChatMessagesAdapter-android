package com.quickblox.chatviewcontroller.sample.utils;

import android.os.Bundle;
import android.util.Log;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.LogLevel;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

public class ChatHelper {
    private static final String TAG = ChatHelper.class.getSimpleName();

    private static final int CHAT_SOCKET_TIMEOUT = 0;

    private static final int CHAT_HISTORY_ITEMS_PER_PAGE = 100;
    private static final String CHAT_HISTORY_ITEMS_SORT_FIELD = "date_sent";

    private static ChatHelper instance;

    private QBChatService qbChatService;

    public static synchronized ChatHelper getInstance() {
        if (instance == null) {
            QBSettings.getInstance().setLogLevel(LogLevel.DEBUG);
            QBChatService.setDebugEnabled(true);
            QBChatService.setConfigurationBuilder(buildChatConfigs());
            instance = new ChatHelper();
        }
        return instance;
    }

    private ChatHelper() {
        qbChatService = QBChatService.getInstance();
    }

    private static QBChatService.ConfigurationBuilder buildChatConfigs() {
        QBChatService.ConfigurationBuilder configurationBuilder = new QBChatService.ConfigurationBuilder();
        configurationBuilder.setKeepAlive(true)
                .setSocketTimeout(CHAT_SOCKET_TIMEOUT)
                .setAutojoinEnabled(false);

        return configurationBuilder;
    }

    public void login(final QBUser user, final QBEntityCallback<Void> callback) {
        // Create REST API session on QuickBlox
        QBAuth.createSession(user).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle args) {
                user.setId(session.getUserId());
                loginToChat(user, callback);
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG, "onError " + e.getMessage());
            }
        });
    }

    private void loginToChat(final QBUser user, final QBEntityCallback<Void> callback) {
        if (qbChatService.isLoggedIn()) {
            callback.onSuccess(null, null);
            return;
        }

        qbChatService.login(user, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void o, Bundle bundle) {
                callback.onSuccess(o, bundle);
            }

            @Override
            public void onError(QBResponseException e) {
                callback.onError(e);
            }
        });
    }

    public void loadChatHistory(QBChatDialog dialog, int skipPagination,
                                final QBEntityCallback<ArrayList<QBChatMessage>> callback) {
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setSkip(skipPagination);
        customObjectRequestBuilder.setLimit(CHAT_HISTORY_ITEMS_PER_PAGE);
        customObjectRequestBuilder.sortDesc(CHAT_HISTORY_ITEMS_SORT_FIELD);

        QBRestChatService.getDialogMessages(dialog, customObjectRequestBuilder).performAsync(
                new QBEntityCallback<ArrayList<QBChatMessage>>() {
                    @Override
                    public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                        callback.onSuccess(qbChatMessages, bundle);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        callback.onError(e);
                    }
                });
    }

    public void logout() {
        qbChatService.destroy();
    }
}