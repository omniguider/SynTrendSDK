package com.omni.syntrendsdk.module;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuildingFloor implements Serializable {

    @SerializedName("id")
    private String id;
    @SerializedName("number")
    private String number;
    @SerializedName("name")
    private String name;
    @SerializedName("desc")
    private String desc;
    @SerializedName("order")
    private String order;
    @SerializedName("lat")
    private double latitude;
    @SerializedName("lng")
    private double longitude;
    @SerializedName("bl_lat")
    private double blLatitude;
    @SerializedName("bl_lng")
    private double blLongitude;
    @SerializedName("tr_lat")
    private double trLatitude;
    @SerializedName("tr_lng")
    private double trLongitude;
    @SerializedName("is_map")
    private String isMap;
    @SerializedName("plan_id")
    private String floorPlanId;
    @SerializedName("pois")
    private POI[] pois;

    public String getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getOrder() {
        return order;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getBlLatitude() {
        return blLatitude;
    }

    public double getBlLongitude() {
        return blLongitude;
    }

    public double getTrLatitude() {
        return trLatitude;
    }

    public double getTrLongitude() {
        return trLongitude;
    }

    public String getIsMap() {
        return isMap;
    }

    public String getFloorPlanId() {
        return floorPlanId;
    }

    public POI[] getPois() {
        return pois;
    }

    public List<POI> getZPois() {
        List<POI> list = new ArrayList<>();

        POI[] allPOIs = getPois();
        for (POI poi : allPOIs) {
            if (poi.getType().contains("Z") || poi.getPOIType() == POI.TYPE_MAP_TEXT) {
                list.add(poi);
            }
        }
        return list;
    }

    @Override
    public String toString() {
        return "BuildingFloor{" +
                "id='" + id + '\'' +
                ", number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", order='" + order + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", blLatitude=" + blLatitude +
                ", blLongitude=" + blLongitude +
                ", trLatitude=" + trLatitude +
                ", trLongitude=" + trLongitude +
                ", isMap='" + isMap + '\'' +
                ", floorPlanId='" + floorPlanId + '\'' +
                ", pois=" + Arrays.toString(pois) +
                '}';
    }
}
