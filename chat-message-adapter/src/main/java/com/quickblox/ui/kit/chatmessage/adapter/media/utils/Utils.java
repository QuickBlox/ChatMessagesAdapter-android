package com.quickblox.ui.kit.chatmessage.adapter.media.utils;

import android.net.Uri;

import com.quickblox.chat.model.QBAttachment;
import com.quickblox.content.model.QBFile;

import java.util.Locale;

/**
 * Created by roman on 8/7/17.
 */

public class Utils {
    private static final int SECONDS_IN_HOUR = 3600;
    private static final int SECONDS_IN_MINUTES = 60;

    public static String formatTimeSecondsToMinutes(int totalSecs) {
        int minutes = (totalSecs % SECONDS_IN_HOUR) / SECONDS_IN_MINUTES;
        int seconds = totalSecs % SECONDS_IN_MINUTES;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    public static Uri getUriFromAttachPublicUrl(QBAttachment attachment) {
        return Uri.parse(QBFile.getPublicUrlForUID(attachment.getId()));
    }
}