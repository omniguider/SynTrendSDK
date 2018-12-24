package com.omni.syntrendsdk.module.google_navigation;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GoogleWaypoint implements Serializable {

    @SerializedName("geocoder_status")
    private String geoCoderStatus;
    @SerializedName("place_id")
    private String placeId;
    @SerializedName("types")
    private String[] types;

    public String getGeoCoderStatus() {
        return geoCoderStatus;
    }

    public void setGeoCoderStatus(String geoCoderStatus) {
        this.geoCoderStatus = geoCoderStatus;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }
}
