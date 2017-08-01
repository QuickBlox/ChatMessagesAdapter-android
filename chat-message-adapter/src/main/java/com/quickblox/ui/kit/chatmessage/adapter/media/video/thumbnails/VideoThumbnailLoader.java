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

class VideoThumbnailLoader implements StreamModelLoader<VideoThumbnail> {
    @Override public DataFetcher<InputStream> getResourceFetcher(VideoThumbnail model, int width, int height) {
        return new VideoThumbnailFetcher(model);
    }

    static class Factory implements ModelLoaderFactory<VideoThumbnail, InputStream> {
        @Override public ModelLoader<VideoThumbnail, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new VideoThumbnailLoader();
        }
        @Override public void teardown() {
        }
    }
}