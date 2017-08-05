package com.quickblox.ui.kit.chatmessage.adapter.media;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.quickblox.ui.kit.chatmessage.adapter.listeners.QBMediaPlayerListener;
import com.quickblox.ui.kit.chatmessage.adapter.media.utils.SimpleExoPlayerInitializer;
import com.quickblox.ui.kit.chatmessage.adapter.media.view.QBPlaybackControlView;

/**
 * Created by roman on 7/14/17.
 */

public class SingleMediaManager implements MediaManager, ExoPlayer.EventListener {
    private static String TAG = SingleMediaManager.class.getSimpleName();

    private QBPlaybackControlView playerView;
    private SimpleExoPlayer exoPlayer;
    private Context context;

    private Uri uri;
    private boolean shouldAutoPlay;
    private int resumeWindow;
    private long resumePosition;
    private QBMediaPlayerListener mediaPlayerListener;

    public SingleMediaManager(Context context) {
        this.context = context;
    }

    public void setMediaPlayerListener(QBMediaPlayerListener mediaPlayerListener) {
        this.mediaPlayerListener = mediaPlayerListener;
    }

    public void removeMediaPlayerListener(){
        this.mediaPlayerListener = null;
    }

    public SimpleExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    @Override
    public void playMedia(QBPlaybackControlView playerView, Uri uri) {
        if(isPlayerViewCurrent(playerView)){
            if(isPlaying()) {
                Log.v(TAG, "playMedia: already playing");
                return;
            } else {
                Log.v(TAG, "playMedia: continue playing");
                exoPlayer.setPlayWhenReady(true);

                if(mediaPlayerListener != null) {
                    mediaPlayerListener.onResume();
                }
                return;
            }
        }
        this.uri = uri;
        updatePlayerView(playerView);
        stopResetCurrentPlayer();
        initPlayer();
        initViewPlayback(playerView);
        startPlayback();

        if(mediaPlayerListener != null) {
            mediaPlayerListener.onStart();
        }
    }

    @Override
    public void pauseMedia() {
        exoPlayer.setPlayWhenReady(false);

        if(mediaPlayerListener != null) {
            mediaPlayerListener.onPause();
        }
    }

    public void suspendPlay() {
        resetMediaPlayer();
        if(mediaPlayerListener != null) {
            mediaPlayerListener.onPause();
        }
    }

    public void resumePlay() {
        restoreMediaPlayer();
        if(mediaPlayerListener != null) {
            mediaPlayerListener.onResume();
        }
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

    public boolean isPlaying() {
        if(exoPlayer != null) {
            Log.d(TAG, "isPlaying playbackState= " + exoPlayer.getPlaybackState());
            return exoPlayer.getPlayWhenReady();
        } return false;
    }

    private void updatePlayerView(QBPlaybackControlView playerView) {
        if(this.playerView !=null && this.playerView != playerView) {
            this.playerView.setDurationViewOnTop();
        }
    }

    private void stopResetCurrentPlayer() {
        Log.v(TAG, "stopResetCurrentPlayer");
        if(playerView != null) {
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
        MediaSource mediaSource = SimpleExoPlayerInitializer.buildMediaSource(uri);
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
        Log.v(TAG, "startPlayback exoPlayer= " + (exoPlayer == null));
        exoPlayer.prepare(SimpleExoPlayerInitializer.buildMediaSource(uri));
        exoPlayer.setPlayWhenReady(true);
    }

    private void initPlayer() {
        exoPlayer = SimpleExoPlayerInitializer.initializeAudioPlayer(context);
        setEventListener();
    }

    private void setEventListener() {
        exoPlayer.addListener(this);
    }

    private void removeEventListener() {
        exoPlayer.removeListener(this);
    }

    public void setPlayerToStartPosition() {
        exoPlayer.seekTo(0);
        pauseMedia();
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
    public void onPlayerError(ExoPlaybackException error) {
        if(mediaPlayerListener != null) {
            mediaPlayerListener.onPlayerError(error);
        }
    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    private void parsePlayerEvent(int playbackState) {
        switch(playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                break;
            case ExoPlayer.STATE_ENDED:
                if(mediaPlayerListener != null) {
                    mediaPlayerListener.onStop();
                }
                break;
            case ExoPlayer.STATE_IDLE:
                break;
            case ExoPlayer.STATE_READY:
                break;
            default:
                break;
        }
    }
}