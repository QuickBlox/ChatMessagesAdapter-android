package com.quickblox.ui.kit.chatmessage.adapter.media;

import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

/**
 * Created by roman on 7/14/17.
 */

public class SingleMediaManager implements MediaManager {
    private static String TAG = SingleMediaManager.class.getSimpleName();

    private SimpleExoPlayerView playerView;

    public SingleMediaManager() {

    }

    @Override
    public void playMedia(MediaSource metaData, SimpleExoPlayerView playerView, Uri uri) {
        stopResetCurrentPlayer();
        setViewPlayback(playerView);
        startPlayback(playerView);
    }

    @Override
    public void stopAnyPlayback() {
        stopResetCurrentPlayer();
    }

    @Override
    public void resetMediaPlayer() {
        Log.v(TAG, "resetMediaPlayer: should clear clearPlayerInstance");

    }

    private void stopResetCurrentPlayer() {
        Log.v(TAG, "stopResetCurrentPlayer");
    }

    private void setViewPlayback(SimpleExoPlayerView playerView) {
        this.playerView = playerView;
        Log.v(TAG, "setViewPlayback");
    }

    private void startPlayback(SimpleExoPlayerView playerView) {
        Log.v(TAG, "startPlayback");
    }
}