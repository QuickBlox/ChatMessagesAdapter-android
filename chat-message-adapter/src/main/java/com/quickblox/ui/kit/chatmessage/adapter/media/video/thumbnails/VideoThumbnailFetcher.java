package com.quickblox.ui.kit.chatmessage.adapter.media.video.thumbnails;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by roman on 7/18/17.
 */

class VideoThumbnailFetcher implements DataFetcher<InputStream> {
    private final VideoThumbnail model;
    private FileInputStream stream;

    public VideoThumbnailFetcher(VideoThumbnail model) {
        this.model = model;
    }

    @Override
    public String getId() {
        return model.path;
    }

    @Override
    public InputStream loadData(Priority priority) throws Exception {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            Log.d("VideoThumbnailFetcher", "model.path= " + model.path);
            retriever.setDataSource(model.path, new HashMap<String, String>());
            Bitmap bitmap = retriever.getFrameAtTime();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] picture = stream.toByteArray();
            if (picture != null) {
                return new ByteArrayInputStream(picture);
            } else {
                return fallback(model.path);
            }
        } finally {
            retriever.release();
        }
    }

    private static final String[] FALLBACKS = {"cover.jpg", "album.jpg", "folder.jpg"};

    private InputStream fallback(String path) throws FileNotFoundException {
        File parent = new File(path).getParentFile();
        for (String fallback : FALLBACKS) {
            // TODO make it smarter by enumerating folder contents and filtering for files
            // example algorithm for that: http://askubuntu.com/questions/123612/how-do-i-set-album-artwork
            File cover = new File(parent, fallback);
            if (cover.exists()) {
                return stream = new FileInputStream(cover);
            }
        }
        return null;
    }

    @Override
    public void cleanup() {
        // already cleaned up in loadData and ByteArrayInputStream will be GC'd
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException ignore) {
                // can't do much about it
            }
        }
    }

    @Override
    public void cancel() {
        // cannot cancel
    }
}