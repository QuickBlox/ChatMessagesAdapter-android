package com.quickblox.ui.kit.chatmessage.adapter.media.video.thumbnails;

import android.content.Context;

import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.StreamModelLoader;

import java.io.InputStream;

/**
 * Created by roman on 7/18/17.
 */

class VideoCoverLoader implements StreamModelLoader<VideoCover> {
    @Override public DataFetcher<InputStream> getResourceFetcher(VideoCover model, int width, int height) {
        return new VideoCoverFetcher(model);
    }

    static class Factory implements ModelLoaderFactory<VideoCover, InputStream> {
        @Override public ModelLoader<VideoCover, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new VideoCoverLoader();
        }
        @Override public void teardown() {
        }
    }
}