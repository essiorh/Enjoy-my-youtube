package com.example.iliamaltsev.enjoyingamovie;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
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
    public VideoListActivityFragment() {
    }
    onSomeEventListener someEventListener;
    public interface onSomeEventListener {
        void someEvent(VideoItem s);
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter =  new ListAdapter(getActivity());
        mListView = (ListView) getView().findViewById(R.id.videos_found);
        mListView.setAdapter(mAdapter);

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
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            someEventListener = (onSomeEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
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
                VideoItem videoItem = searchResults.get(pos);
                someEventListener.someEvent(videoItem);
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
}
