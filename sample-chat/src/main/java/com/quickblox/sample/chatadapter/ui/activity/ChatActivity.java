package com.quickblox.sample.chatadapter.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.sample.chatadapter.R;
import com.quickblox.sample.chatadapter.ui.adapter.CustomMessageAdapter;
import com.quickblox.sample.chatadapter.utils.ChatHelper;
import com.quickblox.ui.kit.chatmessage.adapter.QBMessagesAdapter;
import com.quickblox.ui.kit.chatmessage.adapter.listeners.QBChatAttachClickListener;
import com.quickblox.ui.kit.chatmessage.adapter.listeners.QBChatMessageLinkClickListener;
import com.quickblox.ui.kit.chatmessage.adapter.listeners.QBMediaPlayerListener;
import com.quickblox.ui.kit.chatmessage.adapter.media.SingleMediaManager;
import com.quickblox.ui.kit.chatmessage.adapter.utils.QBMessageTextClickMovement;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.Collections;

import static android.widget.LinearLayout.VERTICAL;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = ChatActivity.class.getSimpleName();
    private static final String EXTRA_QB_USERS = "qb_user_list";
    private static final String DIALOG_ID = "57b701e8a0eb472505000039";

    private int skipPagination = 0;
    private QBChatDialog chatDialog;
    private RecyclerView messagesListView;
    private ProgressBar progressBar;
    private QBMessagesAdapter chatAdapter;
    private SingleMediaManager mediaManager;

    public static void start(Context context, ArrayList<QBUser> qbUsers) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_QB_USERS, qbUsers);
        context.startActivity(intent);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ArrayList<QBUser> qbUsers = (ArrayList<QBUser>) getIntent().getExtras().getSerializable(EXTRA_QB_USERS);
        chatDialog = new QBChatDialog(DIALOG_ID);

        messagesListView = (RecyclerView) findViewById(R.id.list_chat_messages);
        progressBar = (ProgressBar) findViewById(R.id.progress_chat);
        loadChatHistory(qbUsers);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        init playing via callback or via handleMessage
        if(mediaManager != null && mediaManager.isMediaPlayerReady()) {
            mediaManager.resumePlay();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//       release player via callback or via handleMessage
        if(mediaManager != null && mediaManager.isMediaPlayerReady()) {
            mediaManager.suspendPlay();
        }
    }

    private void loadChatHistory(final ArrayList<QBUser> qbUsers) {
        ChatHelper.getInstance().loadChatHistory(chatDialog, skipPagination, new QBEntityCallback<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {
                Log.d(TAG, "loadChatHistoryOnSuccess");
                Collections.reverse(messages);

                chatAdapter = new CustomMessageAdapter(ChatActivity.this, messages, qbUsers);

                mediaManager = chatAdapter.getMediaManagerInstance();

                chatAdapter.setMessageTextViewLinkClickListener(new QBChatMessageLinkClickListener() {
                    @Override
                    public void onLinkClicked(String linkText, QBMessageTextClickMovement.QBLinkType linkType, int positionInAdapter) {
                        Log.d(TAG, "onLinkClicked: linkText - " + linkText
                                + " linkType - " + linkType
                                + " positionInAdapter - " + positionInAdapter);
                    }

                    @Override
                    public void onLongClick(String text, int positionInAdapter) {
                        Log.d(TAG, "onLongClick: linkText - " + text + " positionInAdapter = " + positionInAdapter);
                    }

                }, false);
                chatAdapter.setAttachImageClickListener(new QBChatAttachClickListener() {
                    @Override
                    public void onLinkClicked(QBAttachment imageAttach, int positionInAdapter) {
                        Log.d(TAG, "setAttachImageClickListener: positionInAdapter - " + positionInAdapter);
                        Log.d(TAG, "setAttachImageClickListener: attachment - " + imageAttach.getUrl());
                    }
                });

                chatAdapter.setAttachAudioClickListener(new QBChatAttachClickListener() {
                    @Override
                    public void onLinkClicked(QBAttachment audioAttach, int positionInAdapter) {
                        Log.d(TAG, "onClick: audioAttach - " + audioAttach + " positionInAdapter = " + positionInAdapter);
                    }
                });

                chatAdapter.setMediaPlayerListener(new QBMediaPlayerListener() {
                    @Override
                    public void onStart(Uri uri) {
                        Log.d(TAG, "onStart uri= "+ uri);
                    }

                    @Override
                    public void onResume(Uri uri) {
                        Log.d(TAG, "onResume");
                    }

                    @Override
                    public void onPause(Uri uri) {
                        Log.d(TAG, "onPause");
                    }

                    @Override
                    public void onStop(Uri uri) {
                        Log.d(TAG, "onStop");
                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {

                    }
                });

                LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this, VERTICAL, false);
                messagesListView.setLayoutManager(layoutManager);
                messagesListView.setAdapter(chatAdapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(QBResponseException e) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "loadChatHistoryOnError " + e.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        ChatHelper.getInstance().logout();
        finish();
    }
}
