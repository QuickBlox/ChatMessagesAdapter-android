package com.quickblox.ui.kit.chatmessage.adapter.media;

import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.quickblox.ui.kit.chatmessage.adapter.media.view.QBPlaybackControlView;

/**
 * Created by Roman on 16.07.2017.
 */

public class AudioController implements MediaController {
    private Uri uri;
    private SingleMediaManager mediaManager;
    private EventMediaController eventMediaController;

    public AudioController(SingleMediaManager mediaManager, Uri uri) {
        this.mediaManager = mediaManager;
        this.uri = uri;
    }

    public void setEventMediaController(EventMediaController eventMediaController) {
        this.eventMediaController = eventMediaController;
    }

    @Override
    public void onPlayClicked(QBPlaybackControlView view) {
        eventMediaController.onPlayerInViewInit(view);
        mediaManager.playMedia(view, uri);
    }

    @Override
    public void onPauseClicked() {
        mediaManager.pauseMedia();
    }

    @Override
    public void onStopAnyPlayback() {
        mediaManager.stopAnyPlayback();
    }

    @Override
    public void onStartPosition() {
        mediaManager.onStartPosition();
    }
}
