package com.example.iliamaltsev.enjoyingamovie.adapters;

import android.content.Context;
import android.util.Log;

import com.example.iliamaltsev.enjoyingamovie.R;
import com.example.iliamaltsev.enjoyingamovie.keys.DeveloperKey;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This method sets connect your application with YouTube
 * @author ilia
 */
public class YouTubeConnector {
    public static final String ID_SNIPPET = "id,snippet";
    public static final String FIELDS_USER_REQUEST = "items(id/videoId,snippet/publishedAt,snippet/title,snippet/description,snippet/thumbnails/default/url)";
    public static final String FIELDS_MOST_POPULAR = "items(id,snippet/publishedAt,snippet/title,snippet/description,snippet/thumbnails/default/url)";
    public static final String VIDEO_TYPE = "video";
    public static final String TAG_YC = "YC";
    public static final String COULD_NOT_INITIALIZE = "Could not initialize: ";
    public YouTube youtube;
    private YouTube.Search.List query;
    private YouTube.Videos.List queryPopular;

    /**
     * Use it when you need to initialize your YouTube @param context Context for YouTubeConnector
     */
    public YouTubeConnector(Context context) {
        youtube = new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(com.google.api.client.http.HttpRequest request) throws IOException {
            }
        }).setApplicationName(context.getString(R.string.app_name)).build();

    }

    /**
     * This method sends request to YouTube and returns Video list
     * @param keywords Type of your request
     * @param pageToken Token for page that you need
     * @return Video list from YouTube that you want to receive
     */
    public ArrayList<VideoItem> searchOfWhatUserUserWants(String keywords, String pageToken){
        try{
            List<SearchResult> results = receiveUserRequestVideos(keywords, pageToken);

            return getVideoItemsSuchAsUserRequest(results);
        }catch(IOException e){
            Log.d(TAG_YC, COULD_NOT_INITIALIZE + e);
            return null;
        }
    }

    /**
     * This method sends request to YouTube and returns Video list
     * @param mostPopular Type of your request
     * @param pageToken Token for page that you need
     * @return Video list from YouTube that you want to receive
     */
    public ArrayList<VideoItem> searchMostPopularVideos(String mostPopular, String pageToken) {
        try {
            List<Video> results = receiveMostPopularVideos(mostPopular, pageToken);

            return getVideoItemsSuchAsMostPopular(results);
        }catch(IOException e){
            Log.d(TAG_YC, COULD_NOT_INITIALIZE + e);
            return null;
        }
    }

    /**
     * Use this method that receive playlist from YouTube
     * @param keywords Key to receive playlist from YouTube
     * @param pageToken Key to receive definite page in playlist
     * @return Return list with videos from YouTube
     * @throws IOException throw it if anything bad
     */
    private List<SearchResult> receiveUserRequestVideos(String keywords, String pageToken) throws IOException {
        query = youtube.search().list(ID_SNIPPET);
        query.setKey(DeveloperKey.DEVELOPER_KEY);
        query.setType(VIDEO_TYPE);
        query.setMaxResults(10l);
        query.setFields(FIELDS_USER_REQUEST);
        query.setQ(keywords);
        if (pageToken!=null)
            query.setPageToken(pageToken);
        SearchListResponse response = query.execute();

        return response.getItems();
    }

    /**
     * This method receive VideoItems from YouTube video list
     * @param results This is YouTube video list
     * @return Return array list with VideoItems
     */
    @NotNull
    private ArrayList<VideoItem> getVideoItemsSuchAsUserRequest(List<SearchResult> results) {
        ArrayList<VideoItem> items = new ArrayList<>();
        for(SearchResult result:results){
            VideoItem item = new VideoItem();
            item.setTitle(result.getSnippet().getTitle());
            item.setDate(result.getSnippet().getPublishedAt().getValue());
            item.setDescription(result.getSnippet().getDescription());
            item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
            item.setId(result.getId().getVideoId());
            items.add(item);
        }
        return items;
    }

    /**
     * Use this method that receive playlist from YouTube
     * @param mostPopular Key to receive playlist from YouTube
     * @param pageToken Key to receive definite page in playlist
     * @return Return list with videos from YouTube
     * @throws IOException throw it if anything bad
     */
    private List<Video> receiveMostPopularVideos(String mostPopular, String pageToken) throws IOException {
        queryPopular = youtube.videos().list(ID_SNIPPET);
        queryPopular.setKey(DeveloperKey.DEVELOPER_KEY);
        queryPopular.setMaxResults(10l);
        queryPopular.setFields(FIELDS_MOST_POPULAR);
        queryPopular.setChart(mostPopular);
        if (pageToken!=null)
            queryPopular.setPageToken(pageToken);
        VideoListResponse response = queryPopular.execute();
        return response.getItems();
    }

    /**
     * This method receive VideoItems from YouTube video list
     * @param results This is YouTube video list
     * @return Return array list with VideoItems
     */
    @NotNull
    private ArrayList<VideoItem> getVideoItemsSuchAsMostPopular(List<Video> results) {
        ArrayList<VideoItem> items = new ArrayList<>();
        for(Video result:results){
            VideoItem item = new VideoItem();
            item.setTitle(result.getSnippet().getTitle());
            item.setDate(result.getSnippet().getPublishedAt().getValue());
            item.setDescription(result.getSnippet().getDescription());
            item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
            item.setId(result.getId());
            items.add(item);
        }
        return items;
    }
}
