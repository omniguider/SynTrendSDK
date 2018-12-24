package com.omni.syntrendsdk.module;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class BeaconPushContent implements Serializable {

    @SerializedName("title")
    private String title;
    @SerializedName("desc")
    private String desc;
    @SerializedName("image")
    private String imageUrl;
    @SerializedName("url")
    private String url;
    @SerializedName("is_poi")
    private String isPoi;
    @SerializedName("poi_id")
    private String poiId;

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUrl() {
        return url;
    }

    public String getIsPoi() {
        return isPoi;
    }

    public String getPoiId() {
        return poiId;
    }
}
