package com.quickblox.ui.kit.chatmessage.adapter.media;

import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.quickblox.ui.kit.chatmessage.adapter.media.view.PlayerControllerView;

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
    public void onPlayClicked(PlayerControllerView view) {
        Log.d("Tempos", "AudioController playButton clicked uri= " + uri);
        Log.d("Tempos", "AudioController playButton clicked mediaManager= " + mediaManager.hashCode());
        eventMediaController.onPlayerInViewInit(view);
        mediaManager.playMedia(view, uri);
    }

    @Override
    public void onPauseClicked(View view) {
        Log.d("Tempos", "AudioController pauseButton clicked uri= " + uri);
        mediaManager.pauseMedia();
    }

    @Override
    public void onFastForward(int windowIndex, long positionMs) {
        mediaManager.fastForward(windowIndex, positionMs);
    }

    @Override
    public void onRewind(int windowIndex, long positionMs) {
        mediaManager.rewind(windowIndex, positionMs);
    }

    @Override
    public void stopAnyPlayback() {
        mediaManager.stopAnyPlayback();
    }
}
