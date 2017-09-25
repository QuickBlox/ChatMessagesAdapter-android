package com.quickblox.ui.kit.chatmessage.adapter.media.utils;

import android.net.Uri;

import com.quickblox.chat.model.QBAttachment;
import com.quickblox.content.model.QBFile;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by roman on 8/7/17.
 */

public class Utils {

    public static String formatTimeSecondsToMinutes(int totalSecs) {
        long minutes = TimeUnit.SECONDS.toMinutes(totalSecs);
        totalSecs -= TimeUnit.MINUTES.toSeconds(minutes);
        long seconds = TimeUnit.SECONDS.toSeconds(totalSecs);
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    public static Uri getUriFromAttachPublicUrl(QBAttachment attachment) {
        return Uri.parse(QBFile.getPublicUrlForUID(attachment.getId()));
    }
}