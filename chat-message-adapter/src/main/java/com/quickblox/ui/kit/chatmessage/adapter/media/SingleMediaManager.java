package com.quickblox.ui.kit.chatmessage.adapter.media;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.quickblox.ui.kit.chatmessage.adapter.media.utils.SimpleExoPlayerInitializer;
import com.quickblox.ui.kit.chatmessage.adapter.media.view.PlayerViewController;

/**
 * Created by roman on 7/14/17.
 */

public class SingleMediaManager implements MediaManager {
    private static String TAG = SingleMediaManager.class.getSimpleName();

    private PlayerViewController playerView;
    private Context context;

    public SingleMediaManager(Context context) {
        this.context = context;
    }

    @Override
    public void playMedia(PlayerViewController playerView, Uri uri) {
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

    private boolean isPlayerViewCurrent(PlayerViewController playerView) {
        return this.playerView == playerView;
    }

    private void stopResetCurrentPlayer() {
        Log.v(TAG, "stopResetCurrentPlayer");
        if(playerView != null) {
            player().release();
        }
    }

    private void setViewPlayback(PlayerViewController playerView) {
        this.playerView = playerView;
        Log.v(TAG, "setViewPlayback");
    }

    private void startPlayback(PlayerViewController playerView, Uri uri) {
        Log.v(TAG, "startPlayback");
//        SimpleExoPlayerInitializer.play(context, uri);
    }
}