package com.example.iliamaltsev.enjoyingamovie;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;


public class info extends Fragment {
    public static final String VIDEO_TAG = "VideoSaveState";
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

    public VideoItem getVideoItem() {
        return mVideoItem;
    }

    public void setVideoItem(VideoItem videoItem) {
        this.mVideoItem = videoItem;
        mVideoTitleBigTextView.setText(mVideoItem.getTitle());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mVideoItem.getDate());

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        StringBuffer sDate=new StringBuffer();
        sDate.append(Integer.toString(mDay));
        sDate.append(".");
        sDate.append(Integer.toString(mMonth));
        sDate.append(".");
        sDate.append(Integer.toString(mYear));
        mTimeTextView.setText(sDate);
        mViewsCountTextView.setText(mVideoItem.getDescription());
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
