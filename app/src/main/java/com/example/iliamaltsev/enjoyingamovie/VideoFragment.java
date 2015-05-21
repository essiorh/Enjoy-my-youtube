package com.example.iliamaltsev.enjoyingamovie;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;


public class VideoFragment extends YouTubePlayerFragment implements YouTubePlayer.OnInitializedListener {
    private YouTubePlayer player;
    private String videoId;
    private View videoBox;
    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //videoBox =getActivity().findViewById(R.id.video_fragment_container);

        initialize(DeveloperKey.DEVELOPER_KEY, this);
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.release();
        }
        super.onDestroy();
    }

    public void setVideoId(String videoId) {
        if (videoId != null && !videoId.equals(this.videoId)) {
            this.videoId = videoId;
            if (player != null) {
                videoBox.setVisibility(View.VISIBLE);
                player.cueVideo(videoId);
            }
        }
    }

    public void pause() {
        if (player != null) {
            player.pause();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean restored) {
        this.player = player;
        player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
        if (!restored && videoId != null) {
            player.cueVideo(videoId);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        this.player = null;
    }

}

