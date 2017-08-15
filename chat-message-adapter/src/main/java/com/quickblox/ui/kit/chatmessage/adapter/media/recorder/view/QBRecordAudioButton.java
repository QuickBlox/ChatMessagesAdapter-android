package com.quickblox.ui.kit.chatmessage.adapter.media.recorder.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by roman on 8/11/17.
 */

public class QBRecordAudioButton extends ImageButton {

    private RecordTouchEventListener recordTouchEventListener;
    private final RecordTouchListener recordTouchListener;
    private final RecordLongClickListener recordLongClickListener;

    private final float halfWidthScreen;
    private boolean isSpeakButtonLongPressed;


    public interface RecordTouchEventListener {
        void onStartClick(View view);

        void onCancelClick(View view);

        void onStopClick(View view);
    }

    public QBRecordAudioButton(Context context) {
        this(context, null);
    }

    public QBRecordAudioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QBRecordAudioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        halfWidthScreen = getMiddleWidthScreen();
        recordTouchListener = new RecordTouchListener();
        recordLongClickListener = new RecordLongClickListener();
    }

    public void setRecordTouchListener(final RecordTouchEventListener recordTouchEventListener) {
        this.recordTouchEventListener = recordTouchEventListener;
        initListeners();
    }

    private void initListeners() {
        setOnLongClickListener();
        setOnTouchListener();
    }

    private void setOnTouchListener() {
        this.setOnTouchListener(recordTouchListener);
    }

    private void setOnLongClickListener() {
        this.setOnLongClickListener(recordLongClickListener);
    }

    private boolean canCancel(float x, float halfWidthScreen) {
        return x < halfWidthScreen;
    }

    private float getMiddleWidthScreen() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return displayMetrics.widthPixels / 2;
    }

    private final class RecordTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            view.onTouchEvent(motionEvent);

            float xRaw = motionEvent.getRawX();

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    if (canCancel(xRaw, halfWidthScreen) && isSpeakButtonLongPressed) {
                        recordTouchEventListener.onCancelClick(view);
                        isSpeakButtonLongPressed = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (isSpeakButtonLongPressed) {
                        recordTouchEventListener.onStopClick(view);
                        isSpeakButtonLongPressed = false;
                    }
                    break;
            }
            return true;
        }
    }

    private final class RecordLongClickListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            isSpeakButtonLongPressed = true;
            recordTouchEventListener.onStartClick(v);

            return false;
        }
    }
}