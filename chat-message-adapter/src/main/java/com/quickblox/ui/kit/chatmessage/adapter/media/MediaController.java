package com.quickblox.ui.kit.chatmessage.adapter.media;

import android.view.View;

import com.quickblox.ui.kit.chatmessage.adapter.media.view.PlayerControllerView;

/**
 * Created by Roman on 16.07.2017.
 */

public interface MediaController {

    interface EventMediaController {
        void onPlayerInViewInit(PlayerControllerView view);
    }

    void onPlayClicked(PlayerControllerView view);

    void onPauseClicked(View view);

    void onFastForward(int windowIndex, long positionMs);

    void onRewind(int windowIndex, long positionMs);

    void stopAnyPlayback();
}
