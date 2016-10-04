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
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chatdevelopmentkit.adapter.QBMessagesAdapter;
import com.quickblox.chatviewcontroller.R;
import com.quickblox.chatviewcontroller.sample.utils.ChatHelper;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static android.widget.LinearLayout.VERTICAL;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = ChatActivity.class.getSimpleName();

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
        chatDialog = new QBChatDialog("57ecdca7a0eb47557900000a");

        messagesListView = (RecyclerView) findViewById(R.id.list_chat_messages);
        progressBar = (ProgressBar) findViewById(R.id.progress_chat);
        loadChatHistory();
    }


    private void loadChatHistory() {
        ChatHelper.getInstance().loadChatHistory(chatDialog, skipPagination, new QBEntityCallback<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {
                Log.d(TAG, "loadChatHistoryonSuccess " + messages);
                Collections.reverse(messages);
                if (chatAdapter == null) {
                    chatAdapter = new QBMessagesAdapter(ChatActivity.this, messages);

                    LinearLayoutManager layoutManager
                            = new LinearLayoutManager(ChatActivity.this, VERTICAL, false);
                    messagesListView.setLayoutManager(layoutManager);
                    messagesListView.setAdapter(chatAdapter);
                    progressBar.setVisibility(View.GONE);
                } else {
                    chatAdapter.addList(messages);
                }
            }

            @Override
            public void onError(QBResponseException e) {
//                progressBar.setVisibility(View.GONE);
//                skipPagination -= ChatHelper.CHAT_HISTORY_ITEMS_PER_PAGE;
//                snackbar = showErrorSnackbar(R.string.connection_error, e, null);
            }
        });
        skipPagination += ChatHelper.CHAT_HISTORY_ITEMS_PER_PAGE;
    }
}
