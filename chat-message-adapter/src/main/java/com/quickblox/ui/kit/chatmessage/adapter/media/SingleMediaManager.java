package com.quickblox.ui.kit.chatmessage.adapter.media;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.quickblox.ui.kit.chatmessage.adapter.media.utils.SimpleExoPlayerInitializer;
import com.quickblox.ui.kit.chatmessage.adapter.media.view.PlayerControllerView;

/**
 * Created by roman on 7/14/17.
 */

public class SingleMediaManager implements MediaManager {
    private static String TAG = SingleMediaManager.class.getSimpleName();

    private PlayerControllerView playerView;
    private SimpleExoPlayer exoPlayer;
    private Context context;

    public SingleMediaManager(Context context) {
        this.context = context;
    }

    @Override
    public void playMedia(PlayerControllerView playerView, Uri uri) {
        if(isPlayerViewCurrent(playerView)){
            Log.v(TAG, "playMedia: already playing");
            return;
        }
        stopResetCurrentPlayer();
        initViewPlayback(playerView);
        startPlayback(playerView, uri);
    }

    @Override
    public void pauseMedia() {
        exoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void fastForward(int windowIndex, long positionMs) {
        exoPlayer.seekTo(windowIndex, positionMs);
    }

    private void seekTo(long positionMs) {
        seekTo(exoPlayer.getCurrentWindowIndex(), positionMs);
    }

    private void seekTo(int windowIndex, long positionMs) {
        exoPlayer.seekTo(windowIndex, positionMs);
    }

    @Override
    public void stopAnyPlayback() {
        stopResetCurrentPlayer();
    }

    @Override
    public void resetMediaPlayer() {
        Log.v(TAG, "resetMediaPlayer: should clear clearPlayerInstance");

    }

    private boolean isPlayerViewCurrent(PlayerControllerView playerView) {
        return this.playerView != null && this.playerView == playerView;
    }

    private void stopResetCurrentPlayer() {
        Log.v(TAG, "stopResetCurrentPlayer");
        if(playerView != null) {
            releasePlayer();
            playerView.release();
        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
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
    }
}