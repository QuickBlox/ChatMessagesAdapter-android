package com.quickblox.ui.kit.chatmessage.adapter.media.video.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.util.Util;
import com.quickblox.ui.kit.chatmessage.adapter.R;
import com.quickblox.ui.kit.chatmessage.adapter.media.utils.SimpleExoPlayerInitializer;
import com.quickblox.ui.kit.chatmessage.adapter.media.video.ExoPlayerEventListenerImpl;

/**
 * Created by roman on 8/16/17.
 */

public class VideoPlayerActivity extends Activity {
    private static final String EXTRA_VIDEO_URI = "video_uri";
    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;

    private Uri videoUri;
    private boolean shouldAutoPlay;
    private int resumeWindow;
    private long resumePosition;

    public static void start(Context context, Uri videoUri) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra(EXTRA_VIDEO_URI, videoUri);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_player_activity);
        initViews();
        initFields();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void initViews() {
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
    }

    private void initFields() {
        videoUri = getIntent().getParcelableExtra(EXTRA_VIDEO_URI);
        shouldAutoPlay = true;
    }

    private void initializePlayer() {
        player = SimpleExoPlayerInitializer.initializeExoPlayer(this);
        player.addListener(new PlayerStateListener());
        simpleExoPlayerView.setPlayer(player);
        player.setPlayWhenReady(shouldAutoPlay);
        boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
        if (haveResumePosition) {
            player.seekTo(resumeWindow, resumePosition);
        }
        player.prepare(SimpleExoPlayerInitializer.buildMediaSource(videoUri, this));
    }

    private void releasePlayer() {
        if (player != null) {
            shouldAutoPlay = player.getPlayWhenReady();
            updateResumePosition();
            player.release();
            player = null;
        }
    }

    private void updateResumePosition() {
        resumeWindow = player.getCurrentWindowIndex();
        resumePosition = player.isCurrentWindowSeekable() ? Math.max(0, player.getCurrentPosition())
                : C.TIME_UNSET;
    }

    private void setPlayerToStartPosition() {
        player.seekTo(0);
        player.setPlayWhenReady(false);
    }

    private class PlayerStateListener extends ExoPlayerEventListenerImpl {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == ExoPlayer.STATE_ENDED) {
                setPlayerToStartPosition();
            }
        }
    }
}