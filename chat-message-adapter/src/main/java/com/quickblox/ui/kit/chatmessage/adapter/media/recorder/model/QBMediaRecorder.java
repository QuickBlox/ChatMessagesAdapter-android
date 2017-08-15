package com.quickblox.ui.kit.chatmessage.adapter.media.recorder.model;

/**
 * Created by roman on 7/28/17.
 */

/**
 * Abstract model of the MediaRecorder, in future can be extended by VideoRecorder,
 * and some logic from AudioRecorder can be replaced here.
 */
public abstract class QBMediaRecorder<T> {

    public abstract void startRecord();

    public abstract void stopRecord();

    public abstract void cancelRecord();
}