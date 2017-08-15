package com.quickblox.ui.kit.chatmessage.adapter.media.recorder;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;


import com.quickblox.ui.kit.chatmessage.adapter.media.recorder.exceptions.MediaRecorderException;
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
    private RecordState state = RecordState.RECORD_STATE_UNKNOWN;
    private ConfigurationBuilder configurationBuilder;

    private AudioRecorder(QBMediaRecordListener recordListener) {
        this.recordListener = recordListener;
    }

    public static ConfigurationBuilder newBuilder(QBMediaRecordListener recordListener) {
        return new AudioRecorder(recordListener).new ConfigurationBuilder();
    }

    @Override
    public void startRecord() {
        initMediaRecorder();
        prepareMediaRecorder();
        setState(RecordState.RECORD_STATE_BEGIN);
        recorder.start();
    }

    @Override
    public void cancelRecord() {
//        releaseMediaRecorder all data and maybe delete temp file
        releaseMediaRecorder();

        if(recordListener != null) {
            recordListener.onMediaRecordClosed();
        }
    }

    @Override
    public void stopRecord() {
        releaseMediaRecorder();
        sendResult();
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
            notifyListenerError(new MediaRecorderException(e.getMessage()));
        }
    }

    private void notifyListenerError(MediaRecorderException e) {
        setState(RecordState.RECORD_STATE_ERROR);
        if(recordListener != null) {
            recordListener.onMediaRecordError(e);
        }
    }

    private void notifyListenerSuccess() {
        setState(RecordState.RECORD_STATE_COMPLETED);
        if(recordListener != null) {
            recordListener.onMediaRecorded(getFile());
        }
    }

    private File getFile() {
        return new File(configurationBuilder.filePath);
    }

    private void releaseMediaRecorder() {
        if (recorder != null) {
            if (state.ordinal() >= RecordState.RECORD_STATE_BEGIN.ordinal()) {
                recorder.stop();
            }
            recorder.release();
            recorder = null;
        }
    }

    private void sendResult() {
        if(state.ordinal() > RecordState.RECORD_STATE_UNKNOWN.ordinal() && state.ordinal() < RecordState.RECORD_STATE_COMPLETED.ordinal()) {
            notifyListenerSuccess();
        }
    }

    private class OnErrorListenerImpl implements MediaRecorder.OnErrorListener {

        @Override
        public void onError(MediaRecorder mediaRecorder, int what, int extra) {
            String error = Utils.parseCode(what);
            notifyListenerError(new MediaRecorderException(error));
        }
    }

    private class OnInfoListenerImpl implements MediaRecorder.OnInfoListener {

        @Override
        public void onInfo(MediaRecorder mediaRecorder, int what, int extra) {
            String event = Utils.parseCode(what);
            sendResult();
            Log.d(TAG, "onInfo event= " + event + ", extra= " + extra);
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

        public ConfigurationBuilder useInBuildFilePathGenerator(Context context) {
            filePath = Utils.getAudioPathPrivate(context, fileName);
            return this;
        }

        public ConfigurationBuilder setFilePath(String filePath) {
            this.filePath = filePath;
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
