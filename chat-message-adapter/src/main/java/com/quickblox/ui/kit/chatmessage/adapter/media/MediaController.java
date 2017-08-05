package com.quickblox.ui.kit.chatmessage.adapter.media;

import android.view.View;

import com.quickblox.ui.kit.chatmessage.adapter.media.view.QBPlaybackControlView;

/**
 * Created by Roman on 16.07.2017.
 */

public interface MediaController {

    interface EventMediaController {
        void onPlayerInViewInit(QBPlaybackControlView view);
    }

    void onPlayClicked(QBPlaybackControlView view);

    void onPauseClicked(View view);

    void stopAnyPlayback();
}