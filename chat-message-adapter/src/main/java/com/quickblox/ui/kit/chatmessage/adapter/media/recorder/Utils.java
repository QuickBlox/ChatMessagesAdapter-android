package com.quickblox.ui.kit.chatmessage.adapter.media.recorder;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;

import com.quickblox.core.helper.FileHelper;

import java.io.File;

/**
 * Created by roman on 7/27/17.
 */

public class Utils {
    final static int MEDIA_RECORDING_IS_IN_PROGRESS = 895;

    public static String getAudioFilePathPublic(String folderName, String fileName) {
        File folder = FileHelper.getDirectory(folderName);
        return folder + File.separator + fileName;
    }

    public static String getAudioPathPrivate(Context context, String fileName) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName);
        return file.getPath();
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
            case MEDIA_RECORDING_IS_IN_PROGRESS:
                msg = "MEDIA_RECORDING_IS_IN_PROGRESS";
                break;
            default:
                msg = "UNKNOWN";
                break;
        }
        return msg;
    }
}
