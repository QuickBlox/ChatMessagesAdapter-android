package com.quickblox.ui.kit.chatmessage.adapter.media.recorder.listeners;

import java.io.File;

/**
 * Created by roman on 7/26/17.
 */

public interface AudioRecordListener {

    void onAudioRecorded(int requestCode, File file, int what);

    void onAudioRecordError(int requestCode, Exception e);

    void onAudioRecordClosed(int requestCode);
}
