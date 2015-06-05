package com.example.iliamaltsev.enjoyingamovie.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.iliamaltsev.enjoyingamovie.R;
import com.example.iliamaltsev.enjoyingamovie.adapters.VideoItem;
import java.util.Calendar;

/**
 * This class extends from fragment and initialize Video info
 * @see android.support.v4.app.Fragment
 * @author ilia
 */
public class VideoInfoFragment extends Fragment {
    public static final String VIDEO_TAG = "VideoSaveState";
    private VideoItem mVideoItem;
    private TextView mVideoTitleBigTextView;
    private TextView mTimeTextView         ;
    private TextView mViewsCountTextView   ;

    public VideoInfoFragment() {
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

    /**
     * Use it for add info video
     * @param videoItem Video item for set selected info video to textView
     */
    public void setVideoItem(VideoItem videoItem) {
        this.mVideoItem = videoItem;
        mVideoTitleBigTextView.setText(mVideoItem.getTitle());
        mTimeTextView.setText(convertDateToGoodView(mVideoItem.getDate()));
        mViewsCountTextView.setText(mVideoItem.getDescription());
    }

    /** Convert Long Date to StringBuffer for Good View
     * @param cDate Long date to convert to StringBuffer
     * @return Return String Buffer with Good date
     */
    private StringBuffer convertDateToGoodView(Long cDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(cDate);
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        StringBuffer sDate=new StringBuffer();
        sDate.append(Integer.toString(mDay));
        sDate.append(".");
        sDate.append(Integer.toString(mMonth));
        sDate.append(".");
        sDate.append(Integer.toString(mYear));
        return sDate;
    }

}
