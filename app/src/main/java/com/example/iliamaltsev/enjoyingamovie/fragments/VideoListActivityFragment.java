package com.example.iliamaltsev.enjoyingamovie.fragments;

import android.os.Handler;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.iliamaltsev.enjoyingamovie.R;
import com.example.iliamaltsev.enjoyingamovie.adapters.VideoItem;
import com.example.iliamaltsev.enjoyingamovie.keys.DeveloperKey;
import com.example.iliamaltsev.enjoyingamovie.adapters.InfiniteScrollListener;
import com.example.iliamaltsev.enjoyingamovie.adapters.ListAdapter;
import com.example.iliamaltsev.enjoyingamovie.adapters.YouTubeConnector;
import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggablePanel;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.util.ArrayList;



/**
 * This is the main fragment for show YouTube video list
 * and show selection video in another fragment
 * @see android.support.v4.app.Fragment
 * @author ilia
 */
public class VideoListActivityFragment extends Fragment implements HttpRequestInitializer,TextView.OnEditorActionListener{
    private EditText searchInput;
    private Handler handler;
    private ListAdapter mAdapter;
    private ArrayList<VideoItem> searchResults;
    private ListView mListView;
    private YouTube.Videos.List queryPopular;
    private DraggablePanel draggablePanel;
    private YouTubePlayer youTubePlayer;
    private YouTubePlayerSupportFragment youTubePlayerFragment;
    private VideoInfoFragment infoFragment;
    private static String nextPageToken;
    private static String prevPageToken;
    private View view = null;
    private YouTube youtube;
    private YouTube.Search.List query;
    public static synchronized void setNextPageToken(String value) {
        nextPageToken=value;
    }
    public static synchronized String getNextPageToken()
    {
        return nextPageToken;
    }
    public static synchronized void setPrevPageToken(String value) {
        prevPageToken = value;
    }
    public static synchronized String getPrevPageToken() { return prevPageToken; }

    /**
     * Default constructor
     */
    public VideoListActivityFragment() {
    }

    @Override
    public void initialize(com.google.api.client.http.HttpRequest request) throws IOException {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter =  new ListAdapter(getActivity());
        mListView = (ListView) getView().findViewById(R.id.videos_found);
        mListView.setAdapter(mAdapter);
        youtube=new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(), this).setApplicationName(getActivity().getString(R.string.app_name)).build();

        draggablePanel=(DraggablePanel)getView().findViewById(R.id.draggable_panel);
        infoFragment = new VideoInfoFragment();
        nextPageToken=null;

        prevPageToken=null;
        searchInput = (EditText)getView().findViewById(R.id.search_input);
        handler = new Handler();
        searchInput.setOnEditorActionListener(this);

        addClickListener();
        initializeYoutubeFragment();
        hookDraggablePanelListeners();

        mListView.setOnScrollListener(new InfiniteScrollListener(10) {
            @Override
            public void loadMore(int page, int totalItemsCount) {
                loadMoreVideo();
            }
        });
        changePageToken();
        searchOnYoutube(null);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_list, container, false);
    }
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return beginToFindNewVideoList(v, actionId, event);
    }
    /**
     * Please call this method when you need to load more video to your video list
     */
    private void loadMoreVideo() {
        if (!(getNextPageToken() == null && ((getPrevPageToken() != null)))) {
            changePageToken();
            String textSearchInput=searchInput.getText().toString();
            searchOnYoutube(textSearchInput.equals("")?
                    null:
                    textSearchInput);
        }
    }
    /**
     * Call this method for add click listener
     * to selected video item in your video list
     */
    private void addClickListener(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                showSelectedVideo(pos);
            }

        });
    }

    /**
     * This method needs to show selected video into {@link #youTubePlayer}
     * @param pos Position of selected video to show this one
     */
    private void showSelectedVideo(int pos) {
        if (draggablePanel.getVisibility() != View.VISIBLE)
            draggablePanel.setVisibility(View.VISIBLE);
        VideoItem videoItem = (VideoItem) mListView.getAdapter().getItem(pos);
        setVideoId(videoItem.getId());
        infoFragment.setVideoItem(videoItem);
    }

    /**
     * Please call this method when you need to search
     * new video list
     * @param keywords Needs to search string.
     *                 If keyvords is null then will show
     *                 most popular list
     */
    private void searchOnYoutube(final String keywords){
        new Thread(){
            public void run(){
                YouTubeConnector yc = new YouTubeConnector(getActivity());
                searchResults = (keywords!=null) ?
                        yc.search(keywords,nextPageToken):
                        yc.popularvideo("MostPopular",nextPageToken);
                handler.post(new Runnable(){
                    public void run() {
                            mAdapter.addNewslist(searchResults);
                    }
                });
            }
        }.start();
    }

    /**
     * This method sets {@link #nextPageToken} when we scroll our video list
     * and loads more videos
     * This method calls new thread so be careful please
     */
    private void changePageToken() {
        new Thread() {
            public void run() {
                String poisk = searchInput.getText().toString();
                if (poisk.equals("")) {
                    findNewPageTokenInMostPopular();

                } else {
                    findNewPageTokenInUserCall();
                }
            }
        }.start();
    }

    /**
     * This method finds new page token when we need to show
     * most popular videos to our video list
     */
    private void findNewPageTokenInUserCall() {
        try {
            query = youtube.search().list("id,snippet");
            query.setKey(DeveloperKey.DEVELOPER_KEY);
            query.setType("video");
            query.setMaxResults(10l);
            query.setFields("nextPageToken,prevPageToken,items(id/videoId,snippet/publishedAt,snippet/title,snippet/description,snippet/thumbnails/default/url)");
        } catch (IOException e) {
            Log.d("YC", "Could not initialize: " + e);
        }
        query.setQ(searchInput.getText().toString());
        try {
            if (getNextPageToken() != null)
                query.setPageToken(getNextPageToken());
            SearchListResponse response = query.execute();
            String sToken=response.getNextPageToken();
            setNextPageToken(sToken);
            String sPrevToken=response.getPrevPageToken();
            setPrevPageToken(sPrevToken);
        } catch (IOException e) {
            Log.d("YC", "Could not initialize: " + e);
        }
    }

    /**
     * This method finds new page token when we need to show
     * most popular videos to our video list
     */
    private void findNewPageTokenInMostPopular() {
        try {
            queryPopular = youtube.videos().list("id,snippet");
            queryPopular.setKey(DeveloperKey.DEVELOPER_KEY);
            queryPopular.setMaxResults(10l);
            queryPopular.setFields("nextPageToken,prevPageToken,items(id,snippet/publishedAt,snippet/title,snippet/description,snippet/thumbnails/default/url)");
        } catch (IOException e) {
            Log.d("YC", "Could not initialize: " + e);
        }
        queryPopular.setChart("MostPopular");
        try {
            if (getNextPageToken() != null)
                queryPopular.setPageToken(getNextPageToken());

            VideoListResponse response = queryPopular.execute();
            String sNextToken=response.getNextPageToken();
            setNextPageToken(sNextToken);
            String sPrevToken=response.getPrevPageToken();
            setPrevPageToken(sPrevToken);
        }
        catch (IOException e) {
            Log.d("YC", "Could not initialize: " + e);
        }
    }

    /**
     * Call this method when you need to initialize
     * {@link #draggablePanel} and set for it
     * {@link #youTubePlayerFragment} and {@link #infoFragment}
     */
    private void hookDraggablePanelListeners() {
        draggablePanel.setFragmentManager(getActivity().getSupportFragmentManager());
        draggablePanel.setTopFragment(youTubePlayerFragment);
        draggablePanel.setBottomFragment(infoFragment);
        draggablePanel.setDraggableListener(new DraggableListener() {
            @Override
            public void onMaximized() {
                playVideo();
            }
            @Override
            public void onMinimized() { }
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

    /**
     * This method calls pause to {@link #youTubePlayer}
     */
    private void pauseVideo() {
        if (youTubePlayer.isPlaying()) {
            youTubePlayer.pause();
        }
    }

    /**
     * This method calls play to {@link #youTubePlayer}
     */
    private void playVideo() {
        if (!youTubePlayer.isPlaying()) {
            youTubePlayer.play();
        }
    }

    /**
     * Use this method when you need to
     * initialize your {@link #youTubePlayerFragment}
     */
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

    /**
     * This method loads video to {@link #youTubePlayer}
     * and makes {@link #draggablePanel} maximize
     * @param videoId needs to add video accordingly param videoId
     */
    public void setVideoId(String videoId) {
        if (videoId != null) {
            if (youTubePlayer != null) {
                draggablePanel.maximize();
                youTubePlayer.loadVideo(videoId);
                youTubePlayer.setShowFullscreenButton(true);
            }
        }

    }

    /**
     * Use this method when you need to
     * start {@link #searchOnYoutube(String)}
     * if ENTER has been pressed
     * @param v this is textView that needs to be processed
     * @param actionId it's id of our action
     * @param event it's key of our event type
     * @return true if everything ok or false if anything bad
     */
    private boolean beginToFindNewVideoList(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            mAdapter.clear();
            setNextPageToken(null);
            setPrevPageToken(null);
            searchOnYoutube(v.getText().toString());
            changePageToken();
            return false;
        }
        return true;
    }
}
