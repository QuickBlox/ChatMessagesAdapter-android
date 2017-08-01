package com.quickblox.ui.kit.chatmessage.adapter.media;

import android.net.Uri;

import com.quickblox.ui.kit.chatmessage.adapter.media.view.QBPlaybackControlView;

/**
 * Created by roman on 7/14/17.
 */

public interface MediaManager {

    void playMedia(QBPlaybackControlView playerView, Uri uri);

    void pauseMedia();

    void stopAnyPlayback();

    void resetMediaPlayer();
}