package com.example.iliamaltsev.enjoyingamovie;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubePlayerFragment;
import com.example.iliamaltsev.enjoyingamovie.VideoListActivityFragment.onSomeEventListener;

public class VideoListActivity extends Activity implements onSomeEventListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        getFragmentManager().beginTransaction().replace(R.id.video_fragment_container, VideoFragment.newInstance()).commit();
        getFragmentManager().beginTransaction().replace(R.id.videos_found, VideoListActivityFragment.newInstance()).commit();

    }


    @Override
    public void someEvent(VideoItem s) {

        VideoFragment videoFragment = (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);
        videoFragment.setVideoId(s.getId());
    }


}
