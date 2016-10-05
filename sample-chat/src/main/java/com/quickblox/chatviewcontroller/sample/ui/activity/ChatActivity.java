package com.quickblox.chatviewcontroller.sample.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chatdevelopmentkit.adapter.QBMessagesAdapter;
import com.quickblox.chatviewcontroller.R;
import com.quickblox.chatviewcontroller.sample.ui.adapter.CustomMessageAdapter;
import com.quickblox.chatviewcontroller.sample.utils.ChatHelper;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import java.util.ArrayList;
import java.util.Collections;

import static android.widget.LinearLayout.VERTICAL;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = ChatActivity.class.getSimpleName();

    private final String dialogID = "57b701e8a0eb472505000039";
    private int skipPagination = 0;
    private QBChatDialog chatDialog;
    private RecyclerView messagesListView;
    private ProgressBar progressBar;
    private QBMessagesAdapter chatAdapter;

    public static void start(Context context) {
        Intent intent = new Intent(context, ChatActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatDialog = new QBChatDialog(dialogID);

        messagesListView = (RecyclerView) findViewById(R.id.list_chat_messages);
        progressBar = (ProgressBar) findViewById(R.id.progress_chat);
        loadChatHistory();
    }


    private void loadChatHistory() {
        ChatHelper.getInstance().loadChatHistory(chatDialog, skipPagination, new QBEntityCallback<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {
                Log.d(TAG, "loadChatHistoryOnSuccess " + messages);
                Collections.reverse(messages);

                chatAdapter = new CustomMessageAdapter(ChatActivity.this, messages);

                LinearLayoutManager layoutManager
                        = new LinearLayoutManager(ChatActivity.this, VERTICAL, false);
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
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        ChatHelper.getInstance().logout();
        finish();
    }
}
