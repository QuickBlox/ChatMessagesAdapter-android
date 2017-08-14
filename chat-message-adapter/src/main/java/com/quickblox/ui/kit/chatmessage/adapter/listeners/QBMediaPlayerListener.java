package com.quickblox.ui.kit.chatmessage.adapter.listeners;

import android.net.Uri;

import com.google.android.exoplayer2.ExoPlaybackException;

/**
 * Created by Roman on 25.07.2017.
 */

public interface QBMediaPlayerListener {

    void onStart(Uri uri);

    void onResume(Uri uri);

    void onPause(Uri uri);

    void onStop(Uri uri);

    void onPlayerError(ExoPlaybackException error);
}
