package com.example.iliamaltsev.enjoyingamovie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.iliamaltsev.enjoyingamovie.R;

import java.util.ArrayList;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Adapter for video list from YouTube
 * @see android.widget.Adapter
 * @see android.widget.ArrayAdapter
 * @author ilia
 */
public class ListAdapter extends ArrayAdapter<VideoItem> {

    private ArrayList<VideoItem> mNewslist;
    private final Context mContext;
    private         ImageView thumbnail;
    private         TextView title;
    private         TextView description;
    private         VideoItem searchResult;

    public ListAdapter(Context context) {
        super(context, R.layout.list_element);
        mNewslist = new ArrayList<>();
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_element, parent, false);
        }
        thumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
        title = (TextView) convertView.findViewById(R.id.video_title);
        description = (TextView) convertView.findViewById(R.id.video_description);
        searchResult = mNewslist.get(position);
        Picasso.with(getContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
        title.setText(searchResult.getTitle());
        description.setText(getStringBuffer(searchResult.getDescription()));
        return convertView;
    }


    /**
     * Clear your adapter
     */
    @Override
    public void clear() {
        super.clear();
        mNewslist.clear();
    }

    @Override
    public int getCount() {
        return mNewslist.size();
    }

    /**
     * getItem(int) in Adapter returns Object but we can override
     * it to BananaPhone thanks to Java return type covariance
     * @param position this param needs to receive true item in Video list
     * @return Return true video item such as position
     */
    @Override
    public VideoItem getItem(int position) {
        return mNewslist.get(position);
    }

    /**
     *  getItemId() is often useless, I think this should be the default
     * implementation in BaseAdapter
     * @param position this param needs to receive position in this adapter
     * @return Return true position in adapter
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * This method adds parsedNewsList to this adapter
     * @param parsedNewsList New list which needs to add
     */
    public void addNewsTolist(ArrayList<VideoItem> parsedNewsList) {
        parsedNewsList.removeAll(mNewslist);
        mNewslist.addAll(parsedNewsList);
        notifyDataSetChanged();
    }

    /**
     * Thia method needs for transform Description video to stringBuffer with
     * short length
     * @param string You description
     * @return Short string buffer with description
     */
    private StringBuffer getStringBuffer(String string) {
        StringBuffer stringBuffer;
        stringBuffer=new StringBuffer();
        stringBuffer.setLength(0);
        stringBuffer.append(string);
        if(stringBuffer.length()>50){
            stringBuffer.setLength(50);
            stringBuffer.append("...");
        }
        return  stringBuffer;
    }

}
