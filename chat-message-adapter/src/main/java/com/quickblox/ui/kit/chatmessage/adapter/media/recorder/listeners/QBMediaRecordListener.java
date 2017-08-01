package com.quickblox.ui.kit.chatmessage.adapter.media.recorder.listeners;

import java.io.File;

/**
 * Created by roman on 7/26/17.
 */

public interface QBMediaRecordListener {

    void onMediaRecorded(File file);

    void onMediaRecordError(Exception e);

    void onMediaRecordClosed();
}
