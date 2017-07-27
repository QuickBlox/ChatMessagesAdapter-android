package com.quickblox.sample.chatadapter.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
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
import com.quickblox.ui.kit.chatmessage.adapter.media.recorder.AudioRecorder;
import com.quickblox.ui.kit.chatmessage.adapter.media.recorder.listeners.AudioRecordListener;
import com.quickblox.ui.kit.chatmessage.adapter.utils.QBMessageTextClickMovement;
import com.quickblox.users.model.QBUser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static android.widget.LinearLayout.VERTICAL;

public class ChatActivity extends AppCompatActivity implements AudioRecordListener {
    private static final String TAG = ChatActivity.class.getSimpleName();
    private static final String EXTRA_QB_USERS = "qb_user_list";
    private static final String DIALOG_ID = "57b701e8a0eb472505000039";

    private static final int REQUEST_CODE_AUDIO_RECORD = 55;

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
                        recordClick();
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

    public void initAudioRecorder() {
        audioRecorder = AudioRecorder.with(this)
                // Required
                .useInBuildFilePathGenerator()
                .setRequestCode(REQUEST_CODE_AUDIO_RECORD)
                .setDuration(10);

                 // Optional
//                .setAudioSource(MediaRecorder.AudioSource.MIC)
//                .setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
//                .setSampleRate(AudioSampleRate.HZ_48000)


        // Start recording
//                .record();
//        new AudioRecorderHelper().record(this, REQUEST_CODE_AUDIO_RECORD);
    }

    public void recordClick() {
        Log.d(TAG, "recordClick start");
        audioLayout.setVisibility(View.VISIBLE);
        audioRecorder.startRecording();

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
        Log.d(TAG, "recordClick stop");
        audioRecorder.stopRecording();
    }

    public void cancelRecord() {
        Log.d(TAG, "recordClick cancel");
        canceled = true;
        audioLayout.setVisibility(View.INVISIBLE);
        audioRecorder.cancel();
    }

    private boolean canCancel(float x, float y) {
        return x < -350;
    }

    @Override
    public void onAudioRecorded(int requestCode, File file, int extra) {
        audioLayout.setVisibility(View.INVISIBLE);
        if(extra == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            Toast.makeText(this, "Max duration reached", Toast.LENGTH_LONG).show();
        }
        switch (requestCode) {
            case REQUEST_CODE_AUDIO_RECORD:
                processSendMessage(file);
                break;
        }
    }

    @Override
    public void onAudioRecordError(int requestCode, Exception e) {
        cancelRecord();
    }

    @Override
    public void onAudioRecordClosed(int requestCode) {
        Toast.makeText(this, "Audio is not recorded", Toast.LENGTH_LONG).show();
    }
}
