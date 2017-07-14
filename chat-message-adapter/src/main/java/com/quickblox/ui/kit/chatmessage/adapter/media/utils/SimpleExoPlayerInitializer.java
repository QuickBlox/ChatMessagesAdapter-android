package com.quickblox.ui.kit.chatmessage.adapter.media.utils;

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
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

/**
 * Created by roman on 7/14/17.
 */

public class SimpleExoPlayerInitializer {

    public static void play(Context context, Uri uri) {
        initializePlayer(context, uri);
    }

    public static void initializePlayer(Context context, Uri uri) {
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(context),
                new DefaultTrackSelector(), new DefaultLoadControl());

//        playerView.setPlayer(player);
//        player.setPlayWhenReady(playWhenReady);
//        player.seekTo(currentWindow, playbackPosition);


        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true, false);
    }

    private static MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("ua"),
                new DefaultExtractorsFactory(), null, null);
    }
}
