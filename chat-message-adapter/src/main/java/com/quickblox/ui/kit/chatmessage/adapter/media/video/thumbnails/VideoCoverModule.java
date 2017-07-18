package com.quickblox.ui.kit.chatmessage.adapter.media.video.thumbnails;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

/**
 * Created by roman on 7/18/17.
 */

public class VideoCoverModule implements GlideModule {
    @Override public void applyOptions(Context context, GlideBuilder builder) {

    }
    @Override public void registerComponents(Context context, Glide glide) {
        glide.register(VideoCover.class, InputStream.class, new VideoCoverLoader.Factory());
    }
}