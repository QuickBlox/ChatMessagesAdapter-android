package com.quickblox.ui.kit.chatmessage.adapter.media;

import android.app.Activity;
import android.net.Uri;

import com.quickblox.ui.kit.chatmessage.adapter.ActivityLifeCycleHandler;
import com.quickblox.ui.kit.chatmessage.adapter.media.view.QBPlaybackControlView;

/**
 * Created by Roman on 16.07.2017.
 */

public class AudioController implements MediaController {
    private SingleMediaManager mediaManager;
    private EventMediaController eventMediaController;
    private ActivityLifeCycleHandlerController handlerController;

    public AudioController(SingleMediaManager mediaManager, EventMediaController eventMediaController) {
        this.mediaManager = mediaManager;
        this.eventMediaController = eventMediaController;
        handlerController = new ActivityLifeCycleHandlerController();
    }

    public void registerActivityHandler(Activity context) {
        context.getApplication().registerActivityLifecycleCallbacks(handlerController);
    }

    public void unregisterActivityHandler(Activity context) {
        context.getApplication().unregisterActivityLifecycleCallbacks(handlerController);
    }

    @Override
    public void onPlayClicked(QBPlaybackControlView view, Uri uri) {
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

    private void resumePlaying() {
        if(mediaManager != null && mediaManager.isMediaPlayerReady()) {
            mediaManager.resumePlay();
        }
    }

    private void suspendPlaying() {
        if(mediaManager != null && mediaManager.isMediaPlayerReady()) {
            mediaManager.suspendPlay();
        }
    }

    private final class ActivityLifeCycleHandlerController extends ActivityLifeCycleHandler {

        @Override
        public void onActivityResumed(Activity activity) {
            resumePlaying();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            suspendPlaying();
        }
    }

}