package com.example.iliamaltsev.enjoyingamovie;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;


public class ListAdapter extends ArrayAdapter<VideoItem> {

    private ArrayList<VideoItem> mNewslist;
    private final Context mContext;

    public ListAdapter(Context context) {
        super(context, R.layout.list_element);
        mNewslist = new ArrayList<>();
        mContext = context;
    }

    public void addNewslist(ArrayList<VideoItem> parsedNewsList) {
        parsedNewsList.removeAll(mNewslist);
        mNewslist.addAll(parsedNewsList);
        notifyDataSetChanged();
    }

    public ArrayList<VideoItem> getNewsList() {
        return mNewslist;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_element, parent, false);
        }

        ImageView thumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.video_title);
        TextView description = (TextView) convertView.findViewById(R.id.video_description);

        VideoItem searchResult = mNewslist.get(position);

        Picasso.with(getContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
        title.setText(searchResult.getTitle());
        description.setText(searchResult.getDescription());
        return convertView;
    }

    @Override
    public void clear() {
        super.clear();
        mNewslist.clear();
    }


    @Override
    public int getCount() {
        return mNewslist.size();
    }

    // getItem(int) in Adapter returns Object but we can override
    // it to BananaPhone thanks to Java return type covariance
    @Override
    public VideoItem getItem(int position) {
        return mNewslist.get(position);
    }

    // getItemId() is often useless, I think this should be the default
    // implementation in BaseAdapter
    @Override
    public long getItemId(int position) {
        return position;
    }

    public void prependNewsList(ArrayList<VideoItem> newsList) {
        for (VideoItem boardNews : newsList) {
            mNewslist.add(0, boardNews);
        }
        notifyDataSetChanged();
    }
}
