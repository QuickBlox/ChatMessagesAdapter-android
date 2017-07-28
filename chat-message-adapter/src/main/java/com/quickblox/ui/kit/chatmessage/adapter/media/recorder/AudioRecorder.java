package com.quickblox.ui.kit.chatmessage.adapter.media.recorder;

import android.media.MediaRecorder;
import android.util.Log;


import com.quickblox.ui.kit.chatmessage.adapter.media.recorder.listeners.QBMediaRecordListener;
import com.quickblox.ui.kit.chatmessage.adapter.media.recorder.model.QBMediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * Created by roman on 7/26/17.
 */

public class AudioRecorder extends QBMediaRecorder<AudioRecorder> {
    private static final String TAG = AudioRecorder.class.getSimpleName();

    private QBMediaRecordListener recordListener;
    private MediaRecorder recorder = null;
    private int requestCode;
    private RecordState state;
    private ConfigurationBuilder configurationBuilder;

    private AudioRecorder(QBMediaRecordListener recordListener) {
        this.recordListener = recordListener;
    }

    public static ConfigurationBuilder newBuilder(QBMediaRecordListener recordListener) {
        return new AudioRecorder(recordListener).new ConfigurationBuilder();
    }

    @Override
    protected void cancel() {
//        releaseMediaRecorder all data and maybe delete temp file
        releaseMediaRecorder();

        if(recordListener != null) {
            recordListener.onMediaRecordClosed(requestCode);
        }
    }

    protected void start() {
        initMediaRecorder();
        prepareMediaRecorder();
        setState(RecordState.RECORD_STATE_BEGIN);
        recorder.start();
    }

    private void initMediaRecorder() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(configurationBuilder.audioSource);
        recorder.setOutputFormat(configurationBuilder.outputFormat);
        recorder.setOutputFile(configurationBuilder.filePath);
        recorder.setAudioChannels(configurationBuilder.channels);
        recorder.setAudioEncoder(configurationBuilder.audioEncoder);
        recorder.setAudioEncodingBitRate(configurationBuilder.bitRate);
        recorder.setAudioSamplingRate(configurationBuilder.samplingRate);
        recorder.setMaxDuration(configurationBuilder.duration);
        recorder.setOnInfoListener(new OnInfoListenerImpl());
        recorder.setOnErrorListener(new OnErrorListenerImpl());
    }

    private void prepareMediaRecorder() {
        try {
            recorder.prepare();
        } catch (IOException e) {
            notifyListenerError(e);
        }
    }

    private void notifyListenerError(Exception e) {
        setState(RecordState.RECORD_STATE_ERROR);
        if(recordListener != null) {
            recordListener.onMediaRecordError(requestCode, e);
        }
    }

    private void notifyListenerSuccess(int what) {
        if(recordListener != null) {
            setState(RecordState.RECORD_STATE_COMPLETED);
            recordListener.onMediaRecorded(requestCode, getFile(), what);
        }
    }

    private File getFile() {
        return new File(configurationBuilder.filePath);
    }

    protected void stop() {
        releaseMediaRecorder();
        sendResult(MEDIA_RECORDER_INFO_SUCCESS);
    }

    private void releaseMediaRecorder() {
        if(state.ordinal() > RecordState.RECORD_STATE_BEGIN.ordinal()) {
            recorder.stop();
        }
        recorder.release();
        recorder = null;
    }

    private class OnErrorListenerImpl implements MediaRecorder.OnErrorListener {

        @Override
        public void onError(MediaRecorder mediaRecorder, int what, int extra) {
            String error = Utils.parseCode(what);
            notifyListenerError(new Exception(error));
        }
    }

    private class OnInfoListenerImpl implements MediaRecorder.OnInfoListener {

        @Override
        public void onInfo(MediaRecorder mediaRecorder, int what, int extra) {
            String event = Utils.parseCode(what);
            sendResult(what);
            Log.d(TAG, "onInfo event= " + event + ", extra= " + extra);
        }
    }

    private void sendResult(int what) {
        if(state.ordinal() < RecordState.RECORD_STATE_COMPLETED.ordinal()) {
            notifyListenerSuccess(what);
        }
    }

    private void setState(RecordState state) {
        this.state = state;
    }

    private enum RecordState {
        RECORD_STATE_UNKNOWN,
        RECORD_STATE_BEGIN,
        RECORD_STATE_COMPLETED,
        RECORD_STATE_ERROR;
    }

    public class ConfigurationBuilder {
        public static final int CHANNEL_MONO = 1;
        public static final int CHANNEL_STEREO = 2;

        private int channels = CHANNEL_STEREO;

        private String folderName = TAG;
        private String fileName = "recorded_audio_chat.wav";
        private String filePath;

        private int audioSource = MediaRecorder.AudioSource.MIC;
        private int outputFormat = MediaRecorder.OutputFormat.THREE_GPP;
        private int audioEncoder = MediaRecorder.AudioEncoder.AAC;
        private int bitRate = 96000;
        private int samplingRate = 44100;
        private int duration = (int) TimeUnit.SECONDS.toMillis(30);

        private ConfigurationBuilder() {
        }

        public ConfigurationBuilder useInBuildFilePathGenerator() {
            filePath = Utils.getAudioFilePathTemp(folderName, fileName);
            return this;
        }

        public ConfigurationBuilder setFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public ConfigurationBuilder setRequestCode(int requestCode) {
            AudioRecorder.this.requestCode = requestCode;
            return this;
        }

        public ConfigurationBuilder setAudioSource(int source) {
            this.audioSource = source;
            return this;
        }

        public ConfigurationBuilder setOutputFormat(int outputFormat) {
            this.outputFormat = outputFormat;
            return this;
        }

        public ConfigurationBuilder setAudioEncoder(int audioEncoder) {
            this.audioEncoder = audioEncoder;
            return this;
        }

        public ConfigurationBuilder setAudioEncodingBitRate(int bitRate) {
            this.bitRate = bitRate;
            return this;
        }

        public ConfigurationBuilder setDuration(int durationSeconds) {
            this.duration = (int) TimeUnit.SECONDS.toMillis(durationSeconds);
            return this;
        }

        public ConfigurationBuilder setAudioChannels(int channels) {
            this.channels = channels;
            return this;
        }

        public ConfigurationBuilder setAudioSamplingRate(int samplingRate) {
            this.samplingRate = samplingRate;
            return this;
        }

        public AudioRecorder build() {
            AudioRecorder.this.configurationBuilder = this;
            return AudioRecorder.this;
        }

    }
}
