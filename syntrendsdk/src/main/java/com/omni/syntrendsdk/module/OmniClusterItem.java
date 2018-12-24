package com.omni.syntrendsdk.module;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class OmniClusterItem<T> implements ClusterItem {

    private LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private int mIconRes = -1;
    private POI mPOI;

    public OmniClusterItem(POI poi) {
        mPOI = poi;
        mPosition = new LatLng(poi.getLatitude(), poi.getLongitude());
        mTitle = poi.getName();
        mSnippet = poi.getDesc();
        mIconRes = poi.getPOIIconRes(false);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OmniClusterItem)) {
            return false;
        }

        return super.equals(obj);
    }

    public String getTitle() { return mTitle; }

    public String getSnippet() { return mSnippet; }

    public int getIconRes() {
        return mIconRes;
    }

    public POI getPOI() {
        return mPOI;
    }

}
