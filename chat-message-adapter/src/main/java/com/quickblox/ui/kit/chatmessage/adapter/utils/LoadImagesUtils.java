package com.quickblox.ui.kit.chatmessage.adapter.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;

public class LoadImagesUtils {

    /**
     * Checks possibility loading image via Glide.
     * Glide can't load image for destroyed activity. If Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1
     * Glide will take application context.
     *
     * @param context the context for checking
     * @return true if loading possible and false otherwise
     */
    public static boolean isPossibleToDisplayImage(Context context) {
        if (context == null) {
            return false;
        }

        if (!(context instanceof Application)) {
            if (context instanceof Activity) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && ((Activity) context).isDestroyed()) {
                    return false;
                }
            }
        }

        return true;
    }
}
