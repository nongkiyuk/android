package com.nongkiyuk.nongkiyuk.activities.Favorite.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class Place implements Serializable {
    String id;
    String name;
    String description;
    String longitude;
    String latitude;
    String coverUrl;

    String imageUrls[];

    public Place(String id, String name, String description, String latitude, String longitude, String coverUrl, String[] imageUrls) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.coverUrl = coverUrl;
        this.imageUrls = imageUrls;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }
}
