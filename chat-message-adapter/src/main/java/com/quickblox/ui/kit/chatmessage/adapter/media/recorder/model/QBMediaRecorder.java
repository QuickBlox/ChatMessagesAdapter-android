package com.quickblox.ui.kit.chatmessage.adapter.media.recorder.model;

/**
 * Created by roman on 7/28/17.
 */

public abstract class QBMediaRecorder<T> {
    public static final int MEDIA_RECORDER_INFO_SUCCESS = 0;

    protected abstract void start();

    protected abstract void stop();

    protected abstract void cancel();
}