package com.omni.syntrendsdk.module.google_navigation;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.omni.syntrendsdk.module.NavigationRoutePOI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GoogleNavigationRoute implements Serializable {

    public static String GET_ROUTE_STATUS_OK = "OK";

    @SerializedName("status")
    private String status;
    @SerializedName("geocoded_waypoints")
    private GoogleWaypoint[] waypoints;
    @SerializedName("routes")
    private GoogleRoute[] routes;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public GoogleWaypoint[] getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(GoogleWaypoint[] waypoints) {
        this.waypoints = waypoints;
    }

//    public GoogleRoute[] getRoutes() {
//        return routes;
//    }

    public void setRoutes(GoogleRoute[] routes) {
        this.routes = routes;
    }

    public List<NavigationRoutePOI> getNavigationRoute() {
//        NavigationalRoute navigationalRoute = new NavigationalRoute();
//        navigationalRoute.setResult(status.equals(GET_ROUTE_STATUS_OK) ? "true" : "false");
//        navigationalRoute.setErrorMessage(status.equals(GET_ROUTE_STATUS_OK) ?
//                "No error." :
//                "Parse by GoogleNavigationRoute");

        List<NavigationRoutePOI> navigationalRoutePOIList = new ArrayList<>();
        for (GoogleRoute route : routes) {

            for (GoogleLeg leg : route.getLegs()) {

                for (GoogleStep step : leg.getSteps()) {

                    List<LatLng> polyPointList = step.getPolyline().getDecodedPolyPoints();
                    for (LatLng latLng : polyPointList) {
                        NavigationRoutePOI poi = new NavigationRoutePOI();
                        poi.setFloorNumber("1");
                        poi.setPoisType("normal_road");
                        poi.setLatitude(String.valueOf(latLng.latitude));
                        poi.setLongitude(String.valueOf(latLng.longitude));

                        navigationalRoutePOIList.add(poi);
                    }
                }
            }
        }

//        navigationalRoute.setNavigationalRoutePOIs(navigationalRoutePOIList);

        return navigationalRoutePOIList;
    }
}
