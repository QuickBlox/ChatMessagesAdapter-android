package com.quickblox.ui.kit.chatmessage.adapter.listeners;

import com.google.android.exoplayer2.ExoPlaybackException;

/**
 * Created by Roman on 25.07.2017.
 */

public interface QBMediaPlayerListener {

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onPlayerError(ExoPlaybackException error);
}
