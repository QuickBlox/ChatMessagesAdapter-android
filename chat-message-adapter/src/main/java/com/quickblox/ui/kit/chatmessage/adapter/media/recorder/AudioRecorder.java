package com.quickblox.ui.kit.chatmessage.adapter.media.recorder;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.support.v4.app.Fragment;

import com.quickblox.ui.kit.chatmessage.adapter.media.recorder.listeners.AudioRecordListener;

/**
 * Created by roman on 7/26/17.
 */

public class AudioRecorder {
    private AudioRecordListener listener;
    private Fragment fragment;
    private Activity activity;
    private String filePath;
    private int requestCode;
    private AudioSource source;
    private AudioFormat channel;

    private AudioRecorder(Activity activity) {
        this.activity = activity;
    }

    private AudioRecorder(Fragment fragment) {
        this.fragment = fragment;
    }

    private AudioRecorder(AudioRecordListener listener) {
        this.listener = listener;
    }

//    public static AudioRecorder with(Activity activity) {
//        return new AudioRecorder(activity);
//    }

    public static AudioRecorder with(Fragment fragment) {
        return new AudioRecorder(fragment);
    }

    public static AudioRecorder with(AudioRecordListener listener) {
        return new AudioRecorder(listener);
    }

    public AudioRecorder setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public AudioRecorder setRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public AudioRecorder setSource(AudioSource source) {
        this.source = source;
        return this;
    }

    public AudioRecorder setChannel(AudioFormat channel) {
        this.channel = channel;
        return this;
    }

    public void record() {

    }

}
