package com.example.iliamaltsev.enjoyingamovie;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class info extends Fragment {
    private static final String VIDEO_TAG = "VideoSaveState";
    private VideoItem mVideoItem;

    private TextView mVideoTitleBigTextView;
    private TextView mTimeTextView         ;
    private TextView mViewsCountTextView   ;

    public info() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mVideoItem =(VideoItem) savedInstanceState.getSerializable(VIDEO_TAG);
            if (mVideoItem == null ) {
                mVideoItem=new VideoItem();
            }
        }else{
            mVideoItem=new VideoItem();
        }

    }

    public VideoItem getmVideoItem() {
        return mVideoItem;
    }

    public void setVideoItem(VideoItem videoItem) {
        this.mVideoItem = videoItem;
        mVideoTitleBigTextView.setText(mVideoItem.getTitle());
        mTimeTextView.setText(mVideoItem.getId());
        mViewsCountTextView.setText("Число просмотров"+mVideoItem.getDescription());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        mVideoTitleBigTextView =(TextView) view.findViewById(R.id.videoTitleBig);
        mTimeTextView          =(TextView) view.findViewById(R.id.Time);
        mViewsCountTextView    =(TextView) view.findViewById(R.id.viewsCount);


        return view;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }
}
