package com.quickblox.ui.kit.chatmessage.adapter.media.view;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.quickblox.ui.kit.chatmessage.adapter.R;
import com.quickblox.ui.kit.chatmessage.adapter.media.AudioController;
import com.quickblox.ui.kit.chatmessage.adapter.media.utils.ExoPlayerEventListenerImpl;

/**
 * Created by roman on 8/1/17.
 */

public class QBPlaybackControlView extends PlaybackControlView {
    private static String TAG = QBPlaybackControlView.class.getSimpleName();

    private static final int[] STATE_SET_PLAY =
            {R.attr.state_play, -R.attr.state_pause};
    private static final int[] STATE_SET_PAUSE =
            {-R.attr.state_play, R.attr.state_pause};

    private final TextView durationView;
    private final TextView positionView;

    private final View playButton;
    private final View pauseButton;
    private final ImageView iconPlayPauseView;

    private final ComponentListener componentListener;
    private AudioController mediaController;
    private Uri uri;


    public QBPlaybackControlView(Context context) {
        this(context, null);
    }

    public QBPlaybackControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QBPlaybackControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        componentListener = new ComponentListener();

        durationView = (TextView)findViewById(R.id.msg_attach_duration);
        positionView = (TextView) findViewById(R.id.exo_position);

        playButton = findViewById(R.id.exo_play);
        if (playButton != null) {
            playButton.setOnClickListener(componentListener);
        }
        pauseButton = findViewById(R.id.exo_pause);
        if (pauseButton != null) {
            pauseButton.setOnClickListener(componentListener);
        }
        iconPlayPauseView = (ImageView)findViewById(R.id.icon_play_pause);
        if (iconPlayPauseView != null) {
            iconPlayPauseView.setOnClickListener(componentListener);
        }
        alwaysShow();
    }

    private void alwaysShow() {
        setShowTimeoutMs(0);
        show();
    }

    public void setDurationViewOnTop(){
        setPositionViewVisibility(GONE);
        setDurationViewVisibility(VISIBLE);
    }

    public void setPositionViewOnTop(){
        setPositionViewVisibility(VISIBLE);
        setDurationViewVisibility(GONE);
    }

    private void setPositionViewVisibility(int visibility){
        positionView.setVisibility(visibility);
    }

    private void setDurationViewVisibility(int visibility){
        durationView.setVisibility(visibility);
    }

    public void initView(AudioController mediaController, Uri uri) {
        initMediaController(mediaController);
        setUri(uri);
    }

    private void initMediaController(AudioController mediaController) {
        this.mediaController = mediaController;
    }

    private void setUri(Uri uri) {
        this.uri = uri;
    }

    @Override
    public void setPlayer(Player player) {
        if (getPlayer() == player) {
            return;
        }
        if (getPlayer() != null) {
            getPlayer().removeListener(componentListener);
        }
        if (player != null) {
            player.addListener(componentListener);
        }
        super.setPlayer(player);
    }

    public void restoreState(ExoPlayer player) {
        if(player != null) {
            setPlayer(player);
            updatePositionDurationViews();
            updateViewState();
        }
    }

    private void updatePositionDurationViews() {
        if(getPlayer().getCurrentPosition() == 0) {
            setDurationViewOnTop();
        } else {
            setPositionViewOnTop();
        }
    }

    private void updateViewState() {
        if(getPlayer().getPlaybackState() == Player.STATE_ENDED){
            resetPlayerPosition();
        } else {
            updatePlayPauseIconView();
        }
    }

    public void releaseView() {
        Log.d(TAG, "releaseView");
        setDurationViewOnTop();
        disposeViewPlayer();
    }

    public void disposeViewPlayer() {
        Log.d(TAG, "disposeViewPlayer");
        if (this.getPlayer() != null) {
            setPlayer(null);
            updatePlayPauseIconView();
        }
    }

    public boolean isCurrentViewPlaying() {
        return getPlayer() != null;
    }

    public void updatePlayPauseIconView() {
        if (!isVisible() || iconPlayPauseView == null) {
            return;
        }
        boolean requestPlayPauseFocus;
        requestPlayPauseFocus = getPlayer() != null && getPlayer().getPlayWhenReady();

        if(requestPlayPauseFocus){
            setPauseStateIcon();
        } else {
            setPlayStateIcon();
        }
    }

    private void setPlayStateIcon() {
        iconPlayPauseView.setActivated(false);
        iconPlayPauseView.setImageState(STATE_SET_PLAY, true);
    }

    private void setPauseStateIcon() {
        iconPlayPauseView.setActivated(true);
        iconPlayPauseView.setImageState(STATE_SET_PAUSE, true);
    }

    private final class ComponentListener extends ExoPlayerEventListenerImpl implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (playButton == view) {
                performPlayClick();
            } else if (pauseButton == view) {
                performPauseClick();
            }
            clickIconPlayPauseView();
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if(playbackState == Player.STATE_ENDED && playWhenReady) {
                resetPlayerPosition();
                clickIconPlayPauseView();
            }
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }
    }

     private void resetPlayerPosition() {
         mediaController.onStartPosition();
         setDurationViewOnTop();
     }

     public void clickIconPlayPauseView() {
         if(iconPlayPauseView != null) {
             if (!iconPlayPauseView.isActivated()) {
                 iconPlayPauseView.setImageState(STATE_SET_PAUSE, true);
                 performPlayClick();
             } else {
                 iconPlayPauseView.setImageState(STATE_SET_PLAY, true);
                 performPauseClick();
             }
             iconPlayPauseView.setActivated(!iconPlayPauseView.isActivated());
         }
     }

     private void performPlayClick() {
         setPositionViewOnTop();
         mediaController.onPlayClicked(QBPlaybackControlView.this, uri);
     }

    private void performPauseClick() {
        mediaController.onPauseClicked();
    }
}