package com.example.iliamaltsev.enjoyingamovie;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggablePanel;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class VideoListActivityFragment extends Fragment {

    private EditText searchInput;
    private Handler handler;
    private ListAdapter mAdapter;
    private ArrayList<VideoItem> searchResults;
    private ListView mListView;
    private YouTube.Videos.List queryPopular;
    private YouTube youtube;
    DraggablePanel draggablePanel;
    private YouTubePlayer youTubePlayer;
    private YouTubePlayerSupportFragment youTubePlayerFragment;
    private info infoFragment;
    public VideoListActivityFragment() {
    }



    public static VideoListActivityFragment newInstance() {
        return new VideoListActivityFragment();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter =  new ListAdapter(getActivity());
        mListView = (ListView) getView().findViewById(R.id.videos_found);
        mListView.setAdapter(mAdapter);
        draggablePanel=(DraggablePanel)getView().findViewById(R.id.draggable_panel);

        searchInput = (EditText)getView().findViewById(R.id.search_input);
        handler = new Handler();
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    searchOnYoutube(v.getText().toString());
                    return false;
                }
                return true;
            }
        });
        searchOnYoutube(null);
        addClickListener();
        initializeYoutubeFragment();
        hookDraggablePanelListeners();
    }



    final String LOG_TAG = "myLogs";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_list, container, false);
    }

    private void addClickListener(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                if (draggablePanel.getVisibility()!=View.VISIBLE)
                    draggablePanel.setVisibility(View.VISIBLE);
                VideoItem videoItem = searchResults.get(pos);
                setVideoId(videoItem.getId());


                //Intent intent = new Intent(getActivity().getApplicationContext(), PlayerActivity.class);
                //intent.putExtra("VIDEO_ID", searchResults.get(pos).getId());
                //startActivity(intent);
            }

        });
    }

    private void searchOnYoutube(final String keywords){
        new Thread(){
            public void run(){
                YouTubeConnector yc = new YouTubeConnector(getActivity());
                if (keywords!=null)
                    searchResults = yc.search(keywords);
                else
                    searchResults = yc.popularvideo("MostPopular");
                handler.post(new Runnable(){
                    public void run() {
                        if (keywords != null) {

                            if (!searchResults.isEmpty()) {
                                mListView.setAdapter(null);
                                mAdapter.clear();
                                mAdapter.addNewslist(searchResults);
                                mListView.setAdapter(mAdapter);
                            } else {
                                mAdapter.addNewslist(searchResults);
                            }
                        } else {
                            mAdapter.addNewslist(searchResults);
                        }

                    }
                });
            }
        }.start();
    }

    private void hookDraggablePanelListeners() {
        draggablePanel.setFragmentManager(getActivity().getSupportFragmentManager());
        draggablePanel.setTopFragment(youTubePlayerFragment);
        infoFragment = new info();
        draggablePanel.setBottomFragment(infoFragment);
        draggablePanel.setDraggableListener(new DraggableListener() {
            @Override
            public void onMaximized() {
                playVideo();
            }

            @Override
            public void onMinimized() {
                //Empty
            }

            @Override
            public void onClosedToLeft() {
                pauseVideo();
            }

            @Override
            public void onClosedToRight() {
                pauseVideo();
            }
        });
        draggablePanel.initializeView();
        draggablePanel.setVisibility(View.GONE);
    }
    private void pauseVideo() {
        if (youTubePlayer.isPlaying()) {
            youTubePlayer.pause();
        }
    }
    private void playVideo() {
        if (!youTubePlayer.isPlaying()) {
            youTubePlayer.play();
        }
    }
    private void initializeYoutubeFragment() {
        youTubePlayerFragment = new YouTubePlayerSupportFragment();
        youTubePlayerFragment.initialize(DeveloperKey.DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    youTubePlayer = player;

                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult error) {
            }
        });
    }
    public void setVideoId(String videoId) {
        if (videoId != null && !videoId.equals(videoId)) {
            if (youTubePlayer != null) {
                youTubePlayer.cueVideo(videoId);
                youTubePlayer.setShowFullscreenButton(true);


            }
        }
    }
}
