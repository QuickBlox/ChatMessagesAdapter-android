package com.quickblox.ui.kit.chatmessage.adapter.media.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import com.quickblox.ui.kit.chatmessage.adapter.R;
import com.quickblox.ui.kit.chatmessage.adapter.media.AudioController;
import com.quickblox.ui.kit.chatmessage.adapter.media.MediaController;

import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;

import static com.google.android.exoplayer2.ui.PlaybackControlView.DEFAULT_FAST_FORWARD_MS;
import static com.google.android.exoplayer2.ui.PlaybackControlView.DEFAULT_REWIND_MS;

/**
 * Created by Roman on 16.07.2017.
 */

public class PlayerControllerView extends LinearLayout {
    private static String TAG = PlayerControllerView.class.getSimpleName();

    private final View playButton;
    private final View pauseButton;
    private final View fastForwardButton;
    private final View rewindButton;

    private final TextView durationView;
    private final Timeline.Window window;
    private final TextView positionView;
    private final TimeBar timeBar;
    private final Timeline.Period period;
    private long[] adBreakTimesMs;
    private final StringBuilder formatBuilder;
    private final Formatter formatter;

    private int rewindMs;
    private int fastForwardMs;

    private ExoPlayer player;

    private MediaController mediaController;
    private final ComponentListener componentListener;
    private EventListener eventListener;

    private boolean isAttachedToWindow;

    private boolean multiWindowTimeBar;
    private boolean scrubbing;

    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private final Runnable hideAction = new Runnable() {
        @Override
        public void run() {
//            hide();
        }
    };

    public interface EventListener {
        void hideView();
    }

    public PlayerControllerView(Context context) {
        this(context, null);
    }

    public PlayerControllerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int controllerLayoutId = R.layout.control_view;
        rewindMs = DEFAULT_REWIND_MS;
        fastForwardMs = DEFAULT_FAST_FORWARD_MS;

        componentListener = new ComponentListener();
        LayoutInflater.from(context).inflate(controllerLayoutId, this);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);

        window = new Timeline.Window();
        period = new Timeline.Period();

        adBreakTimesMs = new long[0];
        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());

        playButton = findViewById(R.id.player_play);
        if (playButton != null) {
            playButton.setOnClickListener(componentListener);
        }
        pauseButton = findViewById(R.id.player_pause);
        if (pauseButton != null) {
            pauseButton.setOnClickListener(componentListener);
        }
        fastForwardButton = findViewById(R.id.player_ffwd);
        if (fastForwardButton != null) {
            fastForwardButton.setOnClickListener(componentListener);
        }
        rewindButton = findViewById(R.id.player_rew);
        if (rewindButton != null) {
            rewindButton.setOnClickListener(componentListener);
        }
        durationView = (TextView) findViewById(R.id.player_duration);
        positionView = (TextView) findViewById(R.id.player_position);
        timeBar = (TimeBar) findViewById(R.id.player_progress);
        if (timeBar != null) {
            timeBar.setListener(componentListener);
        }
    }

    public void initMediaController(MediaController mediaController) {
        this.mediaController = mediaController;
    }

    public void initMediaController(MediaController mediaController, AudioController.EventListener eventListener) {
        this.mediaController = mediaController;
        setEventListener(eventListener);
    }

    private void setEventListener(PlayerControllerView.EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void initPlayer(ExoPlayer player) {
        if (this.player == player) {
            return;
        }
        if (this.player != null) {
            this.player.removeListener(componentListener);
        }
        this.player = player;
        if (player != null) {
            player.addListener(componentListener);
        }
        updateAll();
    }

    public void release() {
        Log.d(TAG, "release");
        if (this.player != null) {
            this.player.removeListener(componentListener);
            player = null;
            updateAll();
        }
    }

    private void updateAll() {
        updatePlayPauseButton();
        updateNavigation();
        updateProgress();
    }
//
//    public void hide() {
//        if (isVisible()) {
//            setVisibility(GONE);
//            if (visibilityListener != null) {
//                visibilityListener.onVisibilityChange(getVisibility());
//            }
//            removeCallbacks(updateProgressAction);
//            removeCallbacks(hideAction);
//            hideAtMs = C.TIME_UNSET;
//        }
//    }

    private void updatePlayPauseButton() {
        if (!isAttachedToWindow) {
            return;
        }
        boolean requestPlayPauseFocus = false;
        boolean playing = player != null && player.getPlayWhenReady();
        if (playButton != null) {
            requestPlayPauseFocus |= playing && playButton.isFocused();
            playButton.setVisibility(playing ? View.GONE : View.VISIBLE);
        }
        if (pauseButton != null) {
            requestPlayPauseFocus |= !playing && pauseButton.isFocused();
            pauseButton.setVisibility(!playing ? View.GONE : View.VISIBLE);
        }
        if (requestPlayPauseFocus) {
            requestPlayPauseFocus();
        }
    }

    private void updateNavigation() {
        if (!isAttachedToWindow) {
            return;
        }
        Timeline timeline = player != null ? player.getCurrentTimeline() : null;
        boolean haveNonEmptyTimeline = timeline != null && !timeline.isEmpty();
        boolean isSeekable = false;
        if (haveNonEmptyTimeline) {
            int windowIndex = player.getCurrentWindowIndex();
            timeline.getWindow(windowIndex, window);
            isSeekable = window.isSeekable;
        }
        setButtonEnabled(fastForwardMs > 0 && isSeekable, fastForwardButton);
        setButtonEnabled(rewindMs > 0 && isSeekable, rewindButton);
        if (timeBar != null) {
            timeBar.setEnabled(isSeekable);
        }
    }

    private void updateProgress() {
        if (!isAttachedToWindow) {
            return;
        }

        long position = 0;
        long bufferedPosition = 0;
        long duration = 0;
        if (player != null) {
            if (multiWindowTimeBar) {
                Timeline timeline = player.getCurrentTimeline();
                int windowCount = timeline.getWindowCount();
                int periodIndex = player.getCurrentPeriodIndex();
                long positionUs = 0;
                long bufferedPositionUs = 0;
                long durationUs = 0;
                boolean isInAdBreak = false;
                boolean isPlayingAd = false;
                int adBreakCount = 0;
                for (int i = 0; i < windowCount; i++) {
                    timeline.getWindow(i, window);
                    for (int j = window.firstPeriodIndex; j <= window.lastPeriodIndex; j++) {
                        if (timeline.getPeriod(j, period).isAd) {
                            isPlayingAd |= j == periodIndex;
                            if (!isInAdBreak) {
                                isInAdBreak = true;
                                if (adBreakCount == adBreakTimesMs.length) {
                                    adBreakTimesMs = Arrays.copyOf(adBreakTimesMs,
                                            adBreakTimesMs.length == 0 ? 1 : adBreakTimesMs.length * 2);
                                }
                                adBreakTimesMs[adBreakCount++] = C.usToMs(durationUs);
                            }
                        } else {
                            isInAdBreak = false;
                            long periodDurationUs = period.getDurationUs();
                            Log.d("AMBRA", "periodDurationUs = " + periodDurationUs);
                            Assertions.checkState(periodDurationUs != C.TIME_UNSET);
                            long periodDurationInWindowUs = periodDurationUs;
                            if (j == window.firstPeriodIndex) {
                                periodDurationInWindowUs -= window.positionInFirstPeriodUs;
                            }
                            if (i < periodIndex) {
                                positionUs += periodDurationInWindowUs;
                                bufferedPositionUs += periodDurationInWindowUs;
                            }
                            durationUs += periodDurationInWindowUs;
                        }
                    }
                }
                position = C.usToMs(positionUs);
                bufferedPosition = C.usToMs(bufferedPositionUs);
                duration = C.usToMs(durationUs);
                if (!isPlayingAd) {
                    position += player.getCurrentPosition();
                    bufferedPosition += player.getBufferedPosition();
                }
                if (timeBar != null) {
                    timeBar.setAdBreakTimesMs(adBreakTimesMs, adBreakCount);
                }
            } else {
                position = player.getCurrentPosition();
                bufferedPosition = player.getBufferedPosition();
                duration = player.getDuration();
            }
        }
        if (durationView != null) {
            durationView.setText(Util.getStringForTime(formatBuilder, formatter, duration));
        }
        if (positionView != null && !scrubbing) {
            positionView.setText(Util.getStringForTime(formatBuilder, formatter, position));
        }
        if (timeBar != null) {
            timeBar.setPosition(position);
            timeBar.setBufferedPosition(bufferedPosition);
            timeBar.setDuration(duration);
        }

        // Cancel any pending updates and schedule a new one if necessary.
        removeCallbacks(updateProgressAction);
        int playbackState = player == null ? ExoPlayer.STATE_IDLE : player.getPlaybackState();
        if (playbackState != ExoPlayer.STATE_IDLE && playbackState != ExoPlayer.STATE_ENDED) {
            long delayMs;
            if (player.getPlayWhenReady() && playbackState == ExoPlayer.STATE_READY) {
                delayMs = 1000 - (position % 1000);
                if (delayMs < 200) {
                    delayMs += 1000;
                }
            } else {
                delayMs = 1000;
            }
            postDelayed(updateProgressAction, delayMs);
        }
    }

    private void setButtonEnabled(boolean enabled, View view) {
        if (view == null) {
            return;
        }
        view.setEnabled(enabled);
        if (Util.SDK_INT >= 11) {
            setViewAlphaV11(view, enabled ? 1f : 0.3f);
            view.setVisibility(VISIBLE);
        } else {
            view.setVisibility(enabled ? VISIBLE : INVISIBLE);
        }
    }

    @TargetApi(11)
    private void setViewAlphaV11(View view, float alpha) {
        view.setAlpha(alpha);
    }

    private void requestPlayPauseFocus() {
        boolean playing = player != null && player.getPlayWhenReady();
        if (!playing && playButton != null) {
            playButton.requestFocus();
        } else if (playing && pauseButton != null) {
            pauseButton.requestFocus();
        }
    }

    private void seekTo(long positionMs) {
        seekTo(player.getCurrentWindowIndex(), positionMs);
    }

    private void seekTo(int windowIndex, long positionMs) {
        player.seekTo(windowIndex, positionMs);
    }

    private void seekToTimebarPosition(long timebarPositionMs) {
        if (multiWindowTimeBar) {
            Timeline timeline = player.getCurrentTimeline();
            int windowCount = timeline.getWindowCount();
            long remainingMs = timebarPositionMs;
            for (int i = 0; i < windowCount; i++) {
                timeline.getWindow(i, window);
                for (int j = window.firstPeriodIndex; j <= window.lastPeriodIndex; j++) {
                    if (!timeline.getPeriod(j, period).isAd) {
                        long periodDurationMs = period.getDurationMs();
                        if (periodDurationMs == C.TIME_UNSET) {
                            // Should never happen as canShowMultiWindowTimeBar is true.
                            throw new IllegalStateException();
                        }
                        if (j == window.firstPeriodIndex) {
                            periodDurationMs -= window.getPositionInFirstPeriodMs();
                        }
                        if (i == windowCount - 1 && j == window.lastPeriodIndex
                                && remainingMs >= periodDurationMs) {
                            // Seeking past the end of the last window should seek to the end of the timeline.
                            seekTo(i, window.getDurationMs());
                            return;
                        }
                        if (remainingMs < periodDurationMs) {
                            seekTo(i, period.getPositionInWindowMs() + remainingMs);
                            return;
                        }
                        remainingMs -= periodDurationMs;
                    }
                }
            }
        } else {
            seekTo(timebarPositionMs);
        }
    }


    private long rewindPositionMs() {
        return Math.max(player.getCurrentPosition() - rewindMs, 0);
    }

    private long fastForwardPositionMs() {
        return Math.min(player.getCurrentPosition() + fastForwardMs, player.getDuration());
    }

    private int windowsIndex() {
        return player.getCurrentWindowIndex();
    }

    private void suspendPlayingIfPossible() {
        if(player != null) {
            mediaController.onPauseClicked(this);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow");
        isAttachedToWindow = true;
        updateAll();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow");
//        fire callback
//        suspendPlayingIfPossible();
        isAttachedToWindow = false;
        removeCallbacks(updateProgressAction);
    }

    private final class ComponentListener implements ExoPlayer.EventListener, TimeBar.OnScrubListener,
            OnClickListener {

        @Override
        public void onScrubStart(TimeBar timeBar) {
            removeCallbacks(hideAction);
            scrubbing = true;
        }

        @Override
        public void onScrubMove(TimeBar timeBar, long position) {
            if (positionView != null) {
                positionView.setText(Util.getStringForTime(formatBuilder, formatter, position));
            }
        }

        @Override
        public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
            scrubbing = false;
            if (!canceled && player != null) {
                seekToTimebarPosition(position);
            }
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            updatePlayPauseButton();
            updateProgress();
        }

        @Override
        public void onPositionDiscontinuity() {
            updateNavigation();
            updateProgress();
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            // Do nothing.
        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            updateNavigation();
//            updateTimeBarMode();
            updateProgress();
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
                mediaController.onPlayClicked(PlayerControllerView.this);
            } else if (pauseButton == view) {
                mediaController.onPauseClicked(view);
            }
            else if (fastForwardButton == view) {
                if (fastForwardMs <= 0) {
                    return;
                }
                mediaController.onFastForward(windowsIndex(), fastForwardPositionMs());
            } else if (rewindButton == view) {
                if (rewindMs <= 0) {
                    return;
                }
                mediaController.onRewind(windowsIndex(), rewindPositionMs());
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        Log.d(TAG, "onWindowFocusChanged hasWindowFocus= " + hasWindowFocus);
        if(!hasWindowFocus && eventListener != null) {
            eventListener.hideView();
//            fire callback
//            suspendPlayingIfPossible();
        }
    }
}
