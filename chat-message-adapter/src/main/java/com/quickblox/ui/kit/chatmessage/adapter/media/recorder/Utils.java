package com.quickblox.ui.kit.chatmessage.adapter.media.recorder;

import android.media.MediaRecorder;

import com.quickblox.core.helper.FileHelper;

import java.io.File;

/**
 * Created by roman on 7/27/17.
 */

public class Utils {

    public static String getAudioFilePathTemp(String folderName, String fileName) {
        File folder = FileHelper.getDirectory(folderName);
        return folder + File.separator + fileName;
    }

    public static String parseCode(int what) {
        String msg = null;
        switch (what) {
            case MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN:
                msg = "MEDIA_RECORDER_ERROR_INFO_UNKNOWN";
                break;
            case MediaRecorder.MEDIA_ERROR_SERVER_DIED:
                msg = "MEDIA_ERROR_SERVER_DIED";
                break;
            case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
                msg = "MEDIA_RECORDER_INFO_MAX_DURATION_REACHED";
                break;
            case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
                msg = "MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED";
                break;
            default:
                msg = "UNKNOWN";
                break;
        }
        return msg;
    }
}
