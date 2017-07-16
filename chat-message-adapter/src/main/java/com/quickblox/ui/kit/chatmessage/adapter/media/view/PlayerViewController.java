package com.quickblox.ui.kit.chatmessage.adapter.media.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.TimeBar;
import com.quickblox.ui.kit.chatmessage.adapter.R;
import com.quickblox.ui.kit.chatmessage.adapter.media.MediaController;

/**
 * Created by Roman on 16.07.2017.
 */

public class PlayerViewController extends LinearLayout {
//    private final View previousButton;
//    private final View nextButton;
    private final View playButton;
    private final View pauseButton;

    private MediaController mediaController;

    private final ComponentListener componentListener;

    public PlayerViewController(Context context) {
        this(context, null);
    }

    public PlayerViewController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerViewController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int controllerLayoutId = R.layout.control_view;

        componentListener = new ComponentListener();
        LayoutInflater.from(context).inflate(controllerLayoutId, this);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);


        playButton = findViewById(R.id.player_play);
        if (playButton != null) {
            playButton.setOnClickListener(componentListener);
        }
        pauseButton = findViewById(R.id.player_pause);
        if (pauseButton != null) {
            pauseButton.setOnClickListener(componentListener);
        }

    }

    public void setMediaController(MediaController mediaController) {
        this.mediaController = mediaController;
    }

    private final class ComponentListener implements ExoPlayer.EventListener, TimeBar.OnScrubListener,
            OnClickListener {

        @Override
        public void onScrubStart(TimeBar timeBar) {

        }

        @Override
        public void onScrubMove(TimeBar timeBar, long position) {

        }

        @Override
        public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        }

        @Override
        public void onPositionDiscontinuity() {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            // Do nothing.
        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            // Do nothing.
        }

        @Override
        public void onTracksChanged(TrackGroupArray tracks, TrackSelectionArray selections) {
            // Do nothing.
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            // Do nothing.
        }

        @Override
        public void onClick(View view) {
            if (playButton == view) {

                mediaController.onPlayClicked(view);
                } else if (pauseButton == view) {

                mediaController.onPauseClicked(view);
                }


        }
    }
}
