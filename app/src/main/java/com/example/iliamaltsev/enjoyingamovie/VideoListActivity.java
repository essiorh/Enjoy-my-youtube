package com.example.iliamaltsev.enjoyingamovie;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubePlayerFragment;

public class VideoListActivity extends AppCompatActivity {
    private VideoFragment videoFragment;
    private View videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, VideoListActivityFragment.newInstance()).commit();


    }
}
