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

    private AudioRecorder() {
    }

    public static ConfigurationBuilder newBuilder() {
        return new AudioRecorder().new ConfigurationBuilder();
    }

    @Override
    public void startRecord() {
        setState(RecordState.RECORD_STATE_BEGIN);
        initMediaRecorder();
        prepareStartMediaRecorder();
    }

    @Override
    public void cancelRecord() {
        stopAndReleaseMediaRecorder();
        setState(RecordState.RECORD_STATE_UNKNOWN);
        if (recordListener != null) {
            recordListener.onMediaRecordClosed();
        }
    }

    @Override
    public void stopRecord() {
        stopAndReleaseMediaRecorder();
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

    private void prepareStartMediaRecorder() {
        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            notifyListenerError(new MediaRecorderException(e.getMessage()));
        }
    }

    private void notifyListenerError(MediaRecorderException e) {
        setState(RecordState.RECORD_STATE_ERROR);
        if (recordListener != null) {
            recordListener.onMediaRecordError(e);
        }
    }

    private void notifyListenerSuccess() {
        setState(RecordState.RECORD_STATE_COMPLETED);
        if (recordListener != null) {
            recordListener.onMediaRecorded(getFile());
        }
    }

    private File getFile() {
        return new File(configurationBuilder.filePath);
    }

    // From MediaRecorder: RuntimeException is intentionally thrown to the application,
    // if no valid audio/video data has been received when stop() is called.
    // This happens if stop() is called immediately after start()
    private void stopAndReleaseMediaRecorder() {
        if (recorder != null) {
            if (state.ordinal() >= RecordState.RECORD_STATE_BEGIN.ordinal()) {
                try {
                    recorder.stop();
                } catch (RuntimeException stopException) {
                    notifyListenerError(new MediaRecorderException(stopException.getMessage()));
                }
            }
            releaseMediaRecorder();
        }
    }

    public void releaseMediaRecorder() {
        if (recorder != null) {
            recorder.release();
            recorder = null;
            Log.d(TAG, "mediaRecorder released");
        }
    }

    public void setMediaRecordListener(QBMediaRecordListener recordListener) {
        this.recordListener = recordListener;
    }

    public void removeMediaRecordListener() {
        this.recordListener = null;
    }

    public boolean isRecording() {
        return state.equals(RecordState.RECORD_STATE_BEGIN);
    }

    private void sendResult() {
        if (state.ordinal() > RecordState.RECORD_STATE_UNKNOWN.ordinal() && state.ordinal() < RecordState.RECORD_STATE_COMPLETED.ordinal()) {
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
            Log.d(TAG, "onInfo event= " + event + ", extra= " + extra);
            if (!canProceed(what)) {
                stopAndReleaseMediaRecorder();
                sendResult();
            }
        }

        private boolean canProceed(int what) {
            return what == Utils.MEDIA_RECORDING_IS_IN_PROGRESS;
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

        private static final String DEFAULT_FILE_NAME = "recorded_audio_chat.mp3";

        private int channels = CHANNEL_STEREO;
        private String filePath;

        private int audioSource = MediaRecorder.AudioSource.MIC;
        private int outputFormat = MediaRecorder.OutputFormat.MPEG_4;
        private int audioEncoder = MediaRecorder.AudioEncoder.AAC;
        private int bitRate = 96000;
        private int samplingRate = 44100;
        private int duration = (int) TimeUnit.SECONDS.toMillis(30);

        private ConfigurationBuilder() {
        }

        public ConfigurationBuilder useInBuildFilePathGenerator(Context context) {
            filePath = Utils.getAudioPathPrivate(context, DEFAULT_FILE_NAME);
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