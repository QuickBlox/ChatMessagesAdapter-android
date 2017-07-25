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
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.quickblox.ui.kit.chatmessage.adapter.media.utils.SimpleExoPlayerInitializer;
import com.quickblox.ui.kit.chatmessage.adapter.media.view.PlayerControllerView;

/**
 * Created by roman on 7/14/17.
 */

public class SingleMediaManager implements MediaManager, ExoPlayer.EventListener {
    private static String TAG = SingleMediaManager.class.getSimpleName();

    private PlayerControllerView playerView;
    private SimpleExoPlayer exoPlayer;
    private Context context;

    private Uri uri;
    private boolean shouldAutoPlay;
    private int resumeWindow;
    private long resumePosition;

    public SingleMediaManager(Context context) {
        this.context = context;
    }

    public SimpleExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    @Override
    public void playMedia(PlayerControllerView playerView, Uri uri) {
        if(isPlayerViewCurrent(playerView)){
            if(isPlaying()) {
                Log.v(TAG, "playMedia: already playing");
                return;
            } else {
                Log.v(TAG, "playMedia: continue playing");
                exoPlayer.setPlayWhenReady(true);
                return;
            }
        }
        this.uri = uri;
        stopResetCurrentPlayer();
        initViewPlayback(playerView);
        startPlayback(playerView, uri);
    }

    @Override
    public void pauseMedia() {
        exoPlayer.setPlayWhenReady(false);
    }

    public void suspendPlay() {
        resetMediaPlayer();
    }

    public void resumePlay() {
        initializePlayer();
    }

    @Override
    public void fastForward(int windowIndex, long positionMs) {
        exoPlayer.seekTo(windowIndex, positionMs);
    }

    @Override
    public void rewind(int windowIndex, long positionMs) {
        exoPlayer.seekTo(windowIndex, positionMs);
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

    private boolean isPlayerViewCurrent(PlayerControllerView playerView) {
        return  exoPlayer != null && this.playerView != null && this.playerView == playerView && playerView.isCurrentViewPlaying();
    }

    public boolean isPlaying() {
        if(exoPlayer != null) {
            Log.d(TAG, "isPlaying playbackState= " + exoPlayer.getPlaybackState());
            return exoPlayer.getPlayWhenReady();
        } return false;
    }

    private void stopResetCurrentPlayer() {
        Log.v(TAG, "stopResetCurrentPlayer");
        if(playerView != null) {
            releasePlayer();
            playerView.releaseView();
        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            shouldAutoPlay = exoPlayer.getPlayWhenReady();
            updateResumePosition();
            exoPlayer.release();
            playerView.releaseView();
            removeEventListener();
            exoPlayer = null;
        }
    }

    private void initializePlayer() {
        initPlayer();
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

    private void initViewPlayback(PlayerControllerView playerView) {
        Log.v(TAG, "initViewPlayback");
        this.playerView = playerView;
        initPlayer();
        playerView.initPlayer(exoPlayer);
    }

    private void startPlayback(PlayerControllerView playerView, Uri uri) {
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
Log.d("MORADIN", "onPlayerStateChanged playbackState= " + playbackState);
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }
}