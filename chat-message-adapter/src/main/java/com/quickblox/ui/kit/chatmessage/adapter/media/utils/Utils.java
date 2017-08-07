package com.quickblox.ui.kit.chatmessage.adapter.media.utils;

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
}