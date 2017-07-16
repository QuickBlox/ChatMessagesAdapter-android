package com.quickblox.ui.kit.chatmessage.adapter.media;

import android.net.Uri;
import android.util.Log;
import android.view.View;

/**
 * Created by Roman on 16.07.2017.
 */

public class AudioController implements MediaController {
    private Uri uri;
    private MediaManager mediaManager;

    public AudioController(MediaManager mediaManager, Uri uri) {
        this.mediaManager = mediaManager;
        this.uri = uri;
    }

    @Override
    public void onPlayClicked(View view) {
        Log.d("Tempos", "AudioController playButton clicked uri= " + uri);
        Log.d("Tempos", "AudioController playButton clicked mediaManager= " + mediaManager.hashCode());
        mediaManager.playMedia(null, uri);
    }

    @Override
    public void onPauseClicked(View view) {
        Log.d("Tempos", "AudioController pauseButton clicked uri= " + uri);

    }
}
