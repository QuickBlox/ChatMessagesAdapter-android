package com.quickblox.ui.kit.chatmessage.adapter.media;

import android.net.Uri;

import com.quickblox.ui.kit.chatmessage.adapter.media.view.PlayerControllerView;

/**
 * Created by roman on 7/14/17.
 */

public interface MediaManager {

    void playMedia(PlayerControllerView playerView, Uri uri);

    void pauseMedia();

    void fastForward(int windowIndex, long positionMs);

    void rewind(int windowIndex, long positionMs);

    void stopAnyPlayback();

    void resetMediaPlayer();
}