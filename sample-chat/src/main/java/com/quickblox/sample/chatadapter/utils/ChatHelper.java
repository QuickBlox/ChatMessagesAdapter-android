package com.quickblox.sample.chatadapter.utils;

import android.os.Bundle;
import android.util.Log;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.LogLevel;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.core.server.Performer;
import com.quickblox.extensions.RxJavaPerformProcessor;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
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
        Performer<QBSession> performer = QBAuth.createSession(user);
        final Observable<QBSession> observableSession = performer.convertTo(RxJavaPerformProcessor.INSTANCE);

        Observable<Void> observableLoginToChat = Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                QBChatService.getInstance().login(user);
                return null;
            }
        });

        performMultiRequest(observableSession, observableLoginToChat, callback);
    }

    private void performMultiRequest(Observable<QBSession> session, Observable<Void> loginToChat, final QBEntityCallback<ArrayList<QBUser>> callback) {
        final ArrayList<Integer> usersIds = new ArrayList<>();
        usersIds.add(Consts.userOneID);
        usersIds.add(Consts.userTwoID);

        Observable.zip(
                loginToChat.subscribeOn(Schedulers.io()),
                session.flatMap(new Func1<QBSession, Observable<ArrayList<QBUser>>>() {
                    @Override
                    public Observable<ArrayList<QBUser>> call(QBSession qbSession) {
                        return QBUsers.getUsersByIDs(usersIds, null).convertTo(RxJavaPerformProcessor.INSTANCE);
                    }
                })
                        .subscribeOn(Schedulers.io()),
                mergeResult()
        )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<QBUser>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError " + e);
                    }

                    @Override
                    public void onNext(ArrayList<QBUser> qbUsers) {
                        Log.d(TAG, "onNext" + qbUsers);
                        callback.onSuccess(qbUsers, Bundle.EMPTY);
                    }
                });
    }

    private Func2<Void, ArrayList<QBUser>, ArrayList<QBUser>> mergeResult() {
        return new Func2<Void, ArrayList<QBUser>, ArrayList<QBUser>>() {
            @Override
            public ArrayList<QBUser> call(Void aVoid, ArrayList<QBUser> qbUsers) {
                return qbUsers;
            }
        };
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