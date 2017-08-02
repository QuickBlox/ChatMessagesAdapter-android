package com.quickblox.sample.chatadapter.ui.activity;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.quickblox.ui.kit.chatmessage.adapter.media.recorder.AudioRecordController;
import com.quickblox.ui.kit.chatmessage.adapter.media.recorder.AudioRecorder;
import com.quickblox.ui.kit.chatmessage.adapter.media.recorder.listeners.QBMediaRecordListener;
import com.quickblox.ui.kit.chatmessage.adapter.utils.QBMessageTextClickMovement;
import com.quickblox.users.model.QBUser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static android.widget.LinearLayout.VERTICAL;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = ChatActivity.class.getSimpleName();
    private static final String EXTRA_QB_USERS = "qb_user_list";
    private static final String DIALOG_ID = "57b701e8a0eb472505000039";

    private static final int REQUEST_RECORD_AUDIO_WRITE_EXTERNAL_STORAGE_PERMISSIONS = 200;

    private int skipPagination = 0;
    private QBChatDialog chatDialog;
    private RecyclerView messagesListView;
    private ProgressBar progressBar;
    private QBMessagesAdapter chatAdapter;
    private SingleMediaManager mediaManager;
    private ImageButton recordButton;
    TextView textViewCount;

    private LinearLayout audioLayout;

    private AudioRecorder audioRecorder;
    private boolean canceled;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};

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
        textViewCount = (TextView) findViewById(R.id.chat_audio_count);
        audioLayout = (LinearLayout) findViewById(R.id.layout_chat_audio_container);
        recordButton = (ImageButton) findViewById(R.id.button_chat_record_audio);

        requestPermission();
        initAudioRecorder();
        recordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: // press
                        Log.d(TAG, "onTouch ACTION_DOWN");
                        canceled = false;
                        record();
                        break;
                    case MotionEvent.ACTION_MOVE: // move
//                        use more precise logic for detecting swipe
                        Log.d(TAG, "onTouch ACTION_MOVE x= " + x + ", y= " + y);
                        if(canCancel(x, y) && !canceled) {
                            cancelRecord();
                        }
                        break;
                    case MotionEvent.ACTION_UP: // release
                        Log.d(TAG, "onTouch ACTION_UP");
                        if(!canceled) {
                            stopRecordClick();
                        }
                        break;
                }
                return true;
            }
        });

        loadChatHistory(qbUsers);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_WRITE_EXTERNAL_STORAGE_PERMISSIONS:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();
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
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        ChatHelper.getInstance().logout();
        finish();
    }

    public void processSendMessage(File file) {
        Toast.makeText(this, "Audio recorded! " + file.getPath(), Toast.LENGTH_LONG).show();
        Log.d(TAG, "processSendMessage file= " + file);
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_WRITE_EXTERNAL_STORAGE_PERMISSIONS);
    }

    public void initAudioRecorder() {
        audioRecorder = AudioRecorder.newBuilder(new QBMediaRecordListenerImpl())
                // Required
                .useInBuildFilePathGenerator()
                .setDuration(10)
                .build();
                // Optional
//                .setAudioSource(QBMediaRecorder.AudioSource.MIC)
//                .setOutputFormat(QBMediaRecorder.OutputFormat.AMR_NB);
//                .setSampleRate(AudioSampleRate.HZ_48000)

    }

    public void record() {
        Log.d(TAG, "record startRecord");
        audioLayout.setVisibility(View.VISIBLE);
        audioRecorder.startRecord();

        new CountDownTimer(10 * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                textViewCount.setText("remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                textViewCount.setText("Limit is over!");
            }
        }.start();

    }

    public void stopRecordClick() {
        Log.d(TAG, "record stopRecord");
        audioRecorder.stopRecord();
    }

    public void cancelRecord() {
        Log.d(TAG, "record cancelRecord");
        canceled = true;
        audioLayout.setVisibility(View.INVISIBLE);
        audioRecorder.cancelRecord();
    }

    private boolean canCancel(float x, float y) {
        return x < -350;
    }


    private class QBMediaRecordListenerImpl implements QBMediaRecordListener {

        @Override
        public void onMediaRecorded(File file) {
            audioLayout.setVisibility(View.INVISIBLE);
            processSendMessage(file);
        }

        @Override
        public void onMediaRecordError(Exception e) {
            cancelRecord();
        }

        @Override
        public void onMediaRecordClosed() {
            Toast.makeText(ChatActivity.this, "Audio is not recorded", Toast.LENGTH_LONG).show();
        }
    }
}
