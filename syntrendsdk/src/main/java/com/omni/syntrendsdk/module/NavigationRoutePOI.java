package com.omni.syntrendsdk.module;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NavigationRoutePOI implements Serializable {

    @SerializedName("buid")
    private String bUID;
    @SerializedName("floor_number")
    private String floorNumber;
    @SerializedName("lat")
    private String latitude;
    @SerializedName("lon")
    private String longitude;
    @SerializedName("pois_type")
    private String poisType;
    @SerializedName("puid")
    private String pUID;
    @SerializedName("id")
    private String id;
    @SerializedName("store_id")
    private String store_id;
    @SerializedName("selected")
    private String selected;
    @SerializedName("name")
    private String name;

    public String getID() {
        return id;
    }

    public String getStoreID() {
        return store_id;
    }

    public String getSelected() {
        return selected;
    }

    public String getName() {
        return name;
    }

    public String getbUID() {
        return bUID;
    }

    public String getFloorNumber() {
        return floorNumber;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getPoisType() {
        return poisType;
    }

    public String getpUID() {
        return pUID;
    }

    public void setFloorNumber(String floorNumber) {
        this.floorNumber = floorNumber;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setPoisType(String poisType) {
        this.poisType = poisType;
    }

    public LatLng getLocation() {
        return new LatLng(Double.valueOf(getLatitude()), Double.valueOf(getLongitude()));
    }

    @Override
    public String toString() {
        return "NavigationRoutePOI{" +
                "bUID='" + bUID + '\'' +
                ", floorNumber='" + floorNumber + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", poisType='" + poisType + '\'' +
                ", pUID='" + pUID + '\'' +
                '}';
    }
}
