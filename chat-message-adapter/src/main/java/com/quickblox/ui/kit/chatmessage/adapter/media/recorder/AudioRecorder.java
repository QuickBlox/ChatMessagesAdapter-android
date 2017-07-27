package com.quickblox.ui.kit.chatmessage.adapter.media.recorder;

import android.media.MediaRecorder;
import android.util.Log;

import com.quickblox.ui.kit.chatmessage.adapter.media.recorder.listeners.AudioRecordListener;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * Created by roman on 7/26/17.
 */

public class AudioRecorder {
    private static final String TAG = AudioRecorder.class.getSimpleName();
    public static final int CHANNEL_MONO = 1;
    public static final int CHANNEL_STEREO = 2;

    private String folderName = TAG;
    private String fileName = "recorded_audio_chat.wav";
    private String filePath;
    private int requestCode;

    private int audioSource = MediaRecorder.AudioSource.MIC;
    private int outputFormat = MediaRecorder.OutputFormat.THREE_GPP;
    private int audioEncoder = MediaRecorder.AudioEncoder.AAC;
    private int bitRate = 96000;
    private int samplingRate = 44100;
    private int duration = (int) TimeUnit.SECONDS.toMillis(30);

    private AudioRecordListener recordListener;
    private MediaRecorder recorder = null;
    private RecordState state;
    private int channels = CHANNEL_STEREO;


    private AudioRecorder(AudioRecordListener recordListener) {
        this.recordListener = recordListener;
    }


    public static AudioRecorder with(AudioRecordListener recordListener) {
        return new AudioRecorder(recordListener);
    }

    public AudioRecorder useInBuildFilePathGenerator() {
        filePath = Utils.getAudioFilePathTemp(folderName, fileName);
        return this;
    }

    public AudioRecorder setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public AudioRecorder setRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public AudioRecorder setAudioSource(int source) {
        this.audioSource = source;
        return this;
    }

    public AudioRecorder setOutputFormat(int outputFormat) {
        this.outputFormat = outputFormat;
        return this;
    }

    public AudioRecorder setAudioEncoder(int audioEncoder) {
        this.audioEncoder = audioEncoder;
        return this;
    }

    public AudioRecorder setAudioEncodingBitRate(int bitRate) {
        this.bitRate = bitRate;
        return this;
    }

    public AudioRecorder setDuration(int durationSeconds) {
        this.duration = (int) TimeUnit.SECONDS.toMillis(durationSeconds);
        return this;
    }

    public AudioRecorder setAudioChannels(int channels) {
        this.channels = channels;
        return this;
    }

    public AudioRecorder setAudioSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
        return this;
    }

    public void cancel() {
//        release all data and maybe delete temp file
        release();

        if(recordListener != null) {
            recordListener.onAudioRecordClosed(requestCode);
        }
    }

    public void startRecording() {
        setState(RecordState.RECORD_STATE_BEGIN);
        recorder = new MediaRecorder();
        recorder.setAudioSource(audioSource);
        recorder.setOutputFormat(outputFormat);
        recorder.setOutputFile(filePath);
        recorder.setAudioChannels(channels);
        recorder.setAudioEncoder(audioEncoder);
        recorder.setAudioEncodingBitRate(bitRate);
        recorder.setAudioSamplingRate(samplingRate);
        recorder.setMaxDuration(duration);
        recorder.setOnInfoListener(new OnInfoListenerImpl());
        recorder.setOnErrorListener(new OnErrorListenerImpl());
        try {
            recorder.prepare();
        } catch (IOException e) {
            recordListener.onAudioRecordError(requestCode, e);
        }

        recorder.start();
    }

    private File getFile() {
        return new File(filePath);
    }

    public void stopRecording() {
        release();
        sendResult(0);
    }

    public void release() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    private class OnErrorListenerImpl implements MediaRecorder.OnErrorListener {

        @Override
        public void onError(MediaRecorder mediaRecorder, int what, int extra) {
            setState(RecordState.RECORD_STATE_ERROR);
            String error = Utils.parseCode(what);
            recordListener.onAudioRecordError(requestCode, new Exception(error));
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
            setState(RecordState.RECORD_STATE_COMPLETED);
            recordListener.onAudioRecorded(requestCode, getFile(), what);
        }
    }

    private void setState(RecordState state) {
        this.state = state;
    }

    public enum RecordState {
        RECORD_STATE_UNKNOWN,
        RECORD_STATE_BEGIN,
        RECORD_STATE_COMPLETED,
        RECORD_STATE_ERROR;
    }
}
