package com.quickblox.ui.kit.chatmessage.adapter.media.recorder;

import com.quickblox.ui.kit.chatmessage.adapter.media.recorder.controller.QBMediaRecordController;

/**
 * Created by roman on 7/28/17.
 */

public class AudioRecordController implements QBMediaRecordController<AudioRecorder> {

    private AudioRecorder audioRecorder;

    public AudioRecordController(AudioRecorder mediaRecorder) {
        audioRecorder = mediaRecorder;
    }

    @Override
    public void record() {
        audioRecorder.start();
    }

    @Override
    public void stopRecord() {
        audioRecorder.stop();
    }

    @Override
    public void cancel() {
        audioRecorder.cancel();
    }
}
