package com.example.iliamaltsev.enjoyingamovie.adapters;

import java.io.Serializable;

/**
 * This class needs to store your VideoItems in your video list
 * @see com.example.iliamaltsev.enjoyingamovie.adapters.VideoItem
 * @author ilia
 */
public class VideoItem implements Serializable {
    private String title;
    private String description;
    private String thumbnailURL;
    private String id;
    private Long date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnail) {
        this.thumbnailURL = thumbnail;
    }

    public Long getDate() {
        return date;
    }



    public void setDate(Long date) {
        this.date = date;
    }
}
