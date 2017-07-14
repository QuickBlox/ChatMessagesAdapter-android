package com.quickblox.ui.kit.chatmessage.adapter.media;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.quickblox.ui.kit.chatmessage.adapter.media.utils.SimpleExoPlayerInitializer;

/**
 * Created by roman on 7/14/17.
 */

public class SingleMediaManager implements MediaManager {
    private static String TAG = SingleMediaManager.class.getSimpleName();

    private SimpleExoPlayerView playerView;
    private Context context;

    public SingleMediaManager(Context context) {
        this.context = context;
    }

    @Override
    public void playMedia(SimpleExoPlayerView playerView, Uri uri) {
        if(isPlayerViewCurrent(playerView)){
            Log.v(TAG, "playMedia: already playing");
            return;
        }
        stopResetCurrentPlayer();
        setViewPlayback(playerView);
        startPlayback(playerView, uri);
    }

    @Override
    public void stopAnyPlayback() {
        stopResetCurrentPlayer();
    }

    @Override
    public void resetMediaPlayer() {
        Log.v(TAG, "resetMediaPlayer: should clear clearPlayerInstance");

    }

    private boolean isPlayerViewCurrent(SimpleExoPlayerView playerView) {
        return this.playerView == playerView;
    }

    private void stopResetCurrentPlayer() {
        Log.v(TAG, "stopResetCurrentPlayer");
        if(playerView != null) {
            playerView.getPlayer().release();
        }
    }

    private void setViewPlayback(SimpleExoPlayerView playerView) {
        this.playerView = playerView;
        Log.v(TAG, "setViewPlayback");
    }

    private void startPlayback(SimpleExoPlayerView playerView, Uri uri) {
        Log.v(TAG, "startPlayback");
        SimpleExoPlayerInitializer.play(context, uri);
    }
}