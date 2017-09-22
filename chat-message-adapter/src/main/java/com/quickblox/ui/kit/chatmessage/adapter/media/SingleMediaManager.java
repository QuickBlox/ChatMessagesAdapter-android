package com.quickblox.ui.kit.chatmessage.adapter.media;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.quickblox.ui.kit.chatmessage.adapter.listeners.QBMediaPlayerListener;
import com.quickblox.ui.kit.chatmessage.adapter.media.utils.SimpleExoPlayerInitializer;
import com.quickblox.ui.kit.chatmessage.adapter.media.view.QBPlaybackControlView;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by roman on 7/14/17.
 */

public class SingleMediaManager implements MediaManager, Player.EventListener {
    private static String TAG = SingleMediaManager.class.getSimpleName();

    private QBPlaybackControlView playerView;
    private SimpleExoPlayer exoPlayer;
    private Context context;

    private Uri uri;
    private boolean shouldAutoPlay;
    private int resumeWindow;
    private long resumePosition;
    private final CopyOnWriteArraySet<QBMediaPlayerListener> listeners;

    public SingleMediaManager(Context context) {
        listeners = new CopyOnWriteArraySet<>();
        this.context = context;
    }

    public void addListener(QBMediaPlayerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(QBMediaPlayerListener listener) {
        listeners.remove(listener);
    }

    public SimpleExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    @Override
    public void playMedia(QBPlaybackControlView playerView, Uri uri) {
        if (isPlayerViewCurrent(playerView)) {
            if (isPlaying()) {
                Log.v(TAG, "playMedia: already playing");
                return;
            } else {
                Log.v(TAG, "playMedia: continue playing");
                exoPlayer.setPlayWhenReady(true);

                notifyListenersOnResume();
                return;
            }
        }
        this.uri = uri;
        updatePlayerView(playerView);
        stopResetCurrentPlayer();
        initPlayer();
        initViewPlayback(playerView);
        startPlayback();

        notifyListenersOnStart();
    }

    @Override
    public void pauseMedia() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            notifyListenersOnPause();
        }
    }

    public void suspendPlay() {
        resetMediaPlayer();
        notifyListenersOnPause();
    }

    public void resumePlay() {
        restoreMediaPlayer();
        notifyListenersOnResume();
    }


    @Override
    public void stopAnyPlayback() {
        stopResetCurrentPlayer();
    }

    @Override
    public void resetMediaPlayer() {
        Log.v(TAG, "resetMediaPlayer: should clear clearPlayerInstance");
        releasePlayer();
    }

    private boolean isPlayerViewCurrent(QBPlaybackControlView playerView) {
        return exoPlayer != null && this.playerView != null && this.playerView == playerView && playerView.isCurrentViewPlaying();
    }

    public boolean isMediaPlayerReady() {
        return uri != null;
    }

    public boolean isPlaying() {
        if (exoPlayer != null) {
            Log.d(TAG, "isPlaying playbackState= " + exoPlayer.getPlaybackState());
            return exoPlayer.getPlayWhenReady();
        }
        return false;
    }

    private void updatePlayerView(QBPlaybackControlView playerView) {
        if (this.playerView != null && this.playerView != playerView) {
            this.playerView.setDurationViewOnTop();
        }
    }

    private void stopResetCurrentPlayer() {
        Log.v(TAG, "stopResetCurrentPlayer");
        if (playerView != null) {
            releasePlayer();
            playerView.disposeViewPlayer();
        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            shouldAutoPlay = exoPlayer.getPlayWhenReady();
            updateResumePosition();
            exoPlayer.release();
            removeEventListener();
            exoPlayer = null;
        }
    }

    private void restoreMediaPlayer() {
        initPlayer();
        if (isPlayerViewCurrent(playerView)) {
            playerView.setPlayer(exoPlayer);
        }
        MediaSource mediaSource = SimpleExoPlayerInitializer.buildMediaSource(uri, context);
        boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
        if (haveResumePosition) {
            exoPlayer.seekTo(resumeWindow, resumePosition);
        }
        exoPlayer.prepare(mediaSource, !haveResumePosition, false);
        exoPlayer.setPlayWhenReady(shouldAutoPlay);
    }

    private void updateResumePosition() {
        resumeWindow = exoPlayer.getCurrentWindowIndex();
        resumePosition = exoPlayer.isCurrentWindowSeekable() ? Math.max(0, exoPlayer.getCurrentPosition())
                : C.TIME_UNSET;
    }

    private void initViewPlayback(QBPlaybackControlView playerView) {
        Log.v(TAG, "initViewPlayback");
        this.playerView = playerView;
        playerView.setPlayer(exoPlayer);
    }

    private void startPlayback() {
        exoPlayer.prepare(SimpleExoPlayerInitializer.buildMediaSource(uri, context));
        exoPlayer.setPlayWhenReady(true);
    }

    private void initPlayer() {
        exoPlayer = SimpleExoPlayerInitializer.initializeExoPlayer(context);
        setEventListener();
    }

    private void setEventListener() {
        exoPlayer.addListener(this);
    }

    private void removeEventListener() {
        exoPlayer.removeListener(this);
    }

    public void onStartPosition() {
        if(exoPlayer != null) {
            exoPlayer.seekTo(0);
            pauseMedia();
        }
    }

    private void notifyListenersOnStart() {
        for (QBMediaPlayerListener listener : listeners) {
            listener.onStart(uri);
        }
    }

    private void notifyListenersOnResume() {
        for (QBMediaPlayerListener listener : listeners) {
            listener.onResume(uri);
        }
    }

    private void notifyListenersOnPause() {
        for (QBMediaPlayerListener listener : listeners) {
            listener.onPause(uri);
        }
    }

    private void notifyListenersOnStop() {
        for (QBMediaPlayerListener listener : listeners) {
            listener.onStop(uri);
        }
    }

    private void notifyListenersOnPlayerError(ExoPlaybackException error) {
        for (QBMediaPlayerListener listener : listeners) {
            listener.onPlayerError(error);
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        parsePlayerEvent(playbackState);
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        notifyListenersOnPlayerError(error);
    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    private void parsePlayerEvent(int playbackState) {
        switch (playbackState) {
            case Player.STATE_BUFFERING:
                break;
            case Player.STATE_ENDED:
                notifyListenersOnStop();
                break;
            case Player.STATE_IDLE:
                break;
            case Player.STATE_READY:
                break;
            default:
                break;
        }
    }
}