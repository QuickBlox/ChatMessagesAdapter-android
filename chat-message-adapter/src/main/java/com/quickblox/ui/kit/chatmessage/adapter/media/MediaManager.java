package com.quickblox.ui.kit.chatmessage.adapter.media;

import android.net.Uri;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.quickblox.ui.kit.chatmessage.adapter.media.view.PlayerViewController;

/**
 * Created by roman on 7/14/17.
 */

public interface MediaManager {

    void playMedia(PlayerViewController playerView, Uri uri);

    void stopAnyPlayback();

    void resetMediaPlayer();
}