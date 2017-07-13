package com.quickblox.ui.kit.chatmessage.adapter.utils;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

/**
 * Created by roman on 7/13/17.
 */

public class MediaManager {

    private Context context;
    private static MediaManager instance;

    SimpleExoPlayer player;

    boolean playWhenReady;
    int currentWindow;
    long playbackPosition;

    public MediaManager(Context context) {
        this.context = context;
        initializePlayer();
    }

    public static synchronized MediaManager getInstance(Context context) {
        if (instance == null) {
            instance = new MediaManager(context);
        }
        return instance;
    }

    public void setMediaContent(SimpleExoPlayerView playerView, Uri uri) {
        playerView.setPlayer(player);

        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true, false);
    }

    public void initializePlayer() {
         player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(context),
                new DefaultTrackSelector(), new DefaultLoadControl());

//        playerView.setPlayer(player);
//        player.setPlayWhenReady(playWhenReady);
//        player.seekTo(currentWindow, playbackPosition);


//        MediaSource mediaSource = buildMediaSource(uri);
//        player.prepare(mediaSource, true, false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("ua"),
                new DefaultExtractorsFactory(), null, null);
    }

    public void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }
}
