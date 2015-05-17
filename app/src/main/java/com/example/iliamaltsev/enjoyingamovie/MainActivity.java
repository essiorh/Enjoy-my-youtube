package com.example.iliamaltsev.enjoyingamovie;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;


import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {
    static private final String DEVELOPER_KEY = "AIzaSyC6pyliWX1fGdlTjk8pBxKCkbX1iNcBPjk";

    static private final String VIDEO = "8N1_wbOCtcU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.player_view);
        youTubeView.initialize(DEVELOPER_KEY, this);


    }

    @Override
    public void onInitializationFailure(Provider provider,
                                        YouTubeInitializationResult error) {
        Toast.makeText(this, "Блин!" + error.toString(), Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onInitializationSuccess(Provider provider,
                                        YouTubePlayer player, boolean wasRestored) {
        player.loadVideo(VIDEO);
    }

}
