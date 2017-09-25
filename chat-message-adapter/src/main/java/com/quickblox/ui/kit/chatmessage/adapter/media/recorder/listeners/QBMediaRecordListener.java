package com.quickblox.ui.kit.chatmessage.adapter.media.recorder.listeners;

import com.quickblox.ui.kit.chatmessage.adapter.media.recorder.exceptions.MediaRecorderException;

import java.io.File;

/**
 * Created by roman on 7/26/17.
 */

public interface QBMediaRecordListener {

    void onMediaRecorded(File file);

    void onMediaRecordError(MediaRecorderException e);

    void onMediaRecordClosed();
}
