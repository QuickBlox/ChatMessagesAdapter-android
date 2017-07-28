package com.quickblox.ui.kit.chatmessage.adapter.media.recorder.controller;

import com.quickblox.ui.kit.chatmessage.adapter.media.recorder.model.QBMediaRecorder;

/**
 * Created by roman on 7/27/17.
 */

public interface QBMediaRecordController<T extends QBMediaRecorder> {

    void record();

    void stopRecord();

    void cancel();
}
