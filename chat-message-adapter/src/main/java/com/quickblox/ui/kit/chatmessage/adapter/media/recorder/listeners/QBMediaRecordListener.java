package com.quickblox.ui.kit.chatmessage.adapter.media.recorder.listeners;

import java.io.File;

/**
 * Created by roman on 7/26/17.
 */

public interface QBMediaRecordListener {

    void onMediaRecorded(int requestCode, File file, int what);

    void onMediaRecordError(int requestCode, Exception e);

    void onMediaRecordClosed(int requestCode);
}
