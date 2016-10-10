package com.quickblox.sample.chatadapter.utils;

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
import com.quickblox.core.server.Performer;
import com.quickblox.extensions.RxJavaPerformProcessor;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
            QBChatService.setDefaultPacketReplyTimeout(20 * 1000);
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

    public void loginAndGetUsers(final QBUser user, final QBEntityCallback<ArrayList<QBUser>> callback) {
        final ArrayList<Integer> usersIds = new ArrayList<>();
        usersIds.add(Consts.userOneID);
        usersIds.add(Consts.userTwoID);

        Performer<QBSession> performer = QBAuth.createSession(user);
        Observable<QBSession> observable = performer.convertTo(RxJavaPerformProcessor.INSTANCE);

        observable
                .flatMap(new Func1<QBSession, Observable<ArrayList<QBUser>>>() {
                    @Override
                    public Observable<ArrayList<QBUser>> call(QBSession qbSession) {
                        user.setId(qbSession.getUserId());
                        return QBUsers.getUsersByIDs(usersIds, null).convertTo(RxJavaPerformProcessor.INSTANCE);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<QBUser>>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError= " + e.getMessage());
                    }

                    @Override
                    public void onNext(ArrayList<QBUser> qbUsers) {
                        Log.d(TAG, "qbUsers= " + qbUsers.toString());
                        callback.onSuccess(qbUsers, Bundle.EMPTY);
                    }
                });
    }

    public void loginToChat(final QBUser user, final QBEntityCallback<Void> callback) {
        if (qbChatService.isLoggedIn()) {
            Log.d(TAG, "qbChatService.isLoggedIn()");
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