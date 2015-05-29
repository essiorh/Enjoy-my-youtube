package com.example.iliamaltsev.enjoyingamovie;

import android.os.Handler;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
 * A placeholder fragment containing a simple view.
 */
public class VideoListActivityFragment extends Fragment {
    private static final String PLAYER_SAVE_STATE ="playerSaveState" ;
    public static final String VIDEO_ID_TAG="VideoListActivityFragment.videoID";
    public static final String VIDEO_QUERY_TAG="mQueryString";
    public static final String VIDEO_LIST_SAVE_STATE="videoListSaveState";
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private static final String NEXT_TOKEN = "nextToken";
    private static final String PREV_TOKEN = "preevToken";
    private EditText searchInput;
    private Handler handler;
    private ListAdapter mAdapter;
    private ArrayList<VideoItem> searchResults;
    private ListView mListView;
    private YouTube.Videos.List queryPopular;
    DraggablePanel draggablePanel;
    private YouTubePlayer youTubePlayer;
    private YouTubePlayerSupportFragment youTubePlayerFragment;
    private info infoFragment;
    private static String nextPageToken;
    private static String prevPageToken;
    public static synchronized void setNextPageToken(String value)
    {
        nextPageToken=value;
    }
    public static synchronized String getNextPageToken()
    {
        return nextPageToken;
    }
    public static synchronized void setPrevPageToken(String value)
    {
        prevPageToken=value;
    }
    public static synchronized String getPrevPageToken()
    {
        return prevPageToken;
    }
    private View view = null;
    private YouTube youtube;
    private YouTube.Search.List query;
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
        youtube=new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(com.google.api.client.http.HttpRequest request) throws IOException {

            }
        }).setApplicationName(getActivity().getString(R.string.app_name)).build();

        draggablePanel=(DraggablePanel)getView().findViewById(R.id.draggable_panel);
        infoFragment = new info();
        nextPageToken=null;

        prevPageToken=null;
        searchInput = (EditText)getView().findViewById(R.id.search_input);
        handler = new Handler();
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
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
        });

        //changePageToken();
        addClickListener();
        initializeYoutubeFragment();
        hookDraggablePanelListeners();
        mListView.setOnScrollListener(new InfiniteScrollListener(10) {
            @Override
            public void loadMore(int page, int totalItemsCount) {
                if (getNextPageToken()==null && (!(getPrevPageToken()==null)))
                {
                }
                else {
                    changePageToken();
                    if (searchInput.getText().toString().equals("")) {
                        searchOnYoutube(null);

                    } else {
                        searchOnYoutube(searchInput.getText().toString());
                    }
                }
            }
        });
        if (savedInstanceState!=null){
            searchResults =(ArrayList) savedInstanceState.getSerializable(VIDEO_LIST_SAVE_STATE);
            if (searchResults != null && searchResults.size()>0) {
                mAdapter.addNewslist(searchResults);
                mListView.setAdapter(mAdapter);
                /*if (draggablePanel.equals(null))
                    draggablePanel=new DraggablePanel()
                    */
                /*final VideoItem serVideoItem = (VideoItem) savedInstanceState.getSerializable(infoFragment.VIDEO_TAG);
                if (serVideoItem != null) {
                    new Thread() {
                        public void run() {
                            while (true) {
                                if (youTubePlayer != null) {
                                    infoFragment.setVideoItem(serVideoItem);
                                    setVideoId(serVideoItem.getId());
                                    draggablePanel.setVisibility(View.VISIBLE);
                                    draggablePanel.maximize();
                                    break;
                                }
                                try {
                                    Thread.sleep(100l);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                }

                setNextPageToken(savedInstanceState.getString(NEXT_TOKEN));
                setPrevPageToken(savedInstanceState.getString(PREV_TOKEN));*/
            }else{
                searchOnYoutube(null);
            }
        }
        else{
            changePageToken();
            searchOnYoutube(null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // ���������, ������� �� ������������� ���������
        if(view == null) {
            // ���� ������������� ���, ������� ���
            view = inflater.inflate(R.layout.fragment_video_list, container, false);
        } else {
            // ���� ������������� ����, ������� ��� �� ��������,
            // ����� ��������� ������ ��� ��� ����������
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    private void addClickListener(){
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                if (draggablePanel.getVisibility() != View.VISIBLE)
                    draggablePanel.setVisibility(View.VISIBLE);
                VideoItem videoItem = (VideoItem) mListView.getAdapter().getItem(pos);
                setVideoId(videoItem.getId());
                infoFragment.setVideoItem(videoItem);
            }

        });
    }

    private void searchOnYoutube(final String keywords){
        new Thread(){
            public void run(){

                    YouTubeConnector yc = new YouTubeConnector(getActivity());
                if (keywords!=null)
                    searchResults = yc.search(keywords,nextPageToken);
                else
                    searchResults = yc.popularvideo("MostPopular",nextPageToken);

                handler.post(new Runnable(){
                    public void run() {
                            mAdapter.addNewslist(searchResults);
                    }
                });
            }
        }.start();
    }

    private void changePageToken() {
        new Thread() {
            public void run() {
                String poisk = searchInput.getText().toString();
                if (poisk.equals("")) {
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

                } else {
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
            }
        }.start();
    }
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<VideoItem> arrayListSerial=mAdapter.getNewsList();
        outState.putString(NEXT_TOKEN,getNextPageToken());
        outState.putString(PREV_TOKEN,getPrevPageToken());
        outState.putSerializable(infoFragment.VIDEO_TAG,infoFragment.getVideoItem());
        outState.putSerializable(VIDEO_LIST_SAVE_STATE,arrayListSerial);
    }
    public void setVideoId(String videoId) {
        if (videoId != null) {
            if (youTubePlayer != null) {
                draggablePanel.maximize();
                youTubePlayer.loadVideo(videoId);

                youTubePlayer.setShowFullscreenButton(true);


            }
        }

    }
    public abstract class InfiniteScrollListener implements AbsListView.OnScrollListener {
        private int bufferItemCount = 10;
        private int currentPage = 0;
        private int itemCount = 0;
        private boolean isLoading = true;

        public InfiniteScrollListener(int bufferItemCount) {
            this.bufferItemCount = bufferItemCount;
        }

        public abstract void loadMore(int page, int totalItemsCount);

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // Do Nothing
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            if (totalItemCount < itemCount) {
                this.itemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.isLoading = true; }
            }

            if (isLoading && (totalItemCount > itemCount)) {
                isLoading = false;
                itemCount = totalItemCount;
                currentPage++;
            }

            if (!isLoading && (totalItemCount - visibleItemCount)<=(firstVisibleItem + bufferItemCount)) {
                loadMore(currentPage + 1, totalItemCount);
                isLoading = true;
            }
        }
    }
}
