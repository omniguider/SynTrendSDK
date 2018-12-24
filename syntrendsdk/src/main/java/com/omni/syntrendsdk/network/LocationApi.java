package com.omni.syntrendsdk.network;

import android.app.Activity;
import android.util.Log;

import com.omni.syntrendsdk.module.Building;
import com.omni.syntrendsdk.module.BuildingFloor;
import com.omni.syntrendsdk.module.CommonArrayResponse;
import com.omni.syntrendsdk.module.CommonResponse;
import com.omni.syntrendsdk.module.NavigationRoutePOI;
import com.omni.syntrendsdk.module.google_navigation.GoogleNavigationRoute;
import com.omni.syntrendsdk.tool.DialogTools;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class LocationApi {

    private static LocationApi sLOCATION_API;
    private static String platform = "android";

    public static LocationApi getInstance() {
        if (sLOCATION_API == null) {
            sLOCATION_API = new LocationApi();
        }
        return sLOCATION_API;
    }

    interface LocationService {

        @GET("locapi/get_building")
        Call<CommonArrayResponse> getBuildings();

        @GET("locapi/get_floor")
        Call<CommonArrayResponse> getFloors(@Query("b") String buildingId);

        @GET("locapi/get_floor")
        Call<CommonArrayResponse> getFloors(@Query("b") String buildingId,
                                            @Query("keyword") String keyword);

        @GET("locapi/get_navi_route_xy")
        Call<CommonArrayResponse> getUserLocationToIndoorPRoute(@Query("p") String poiId,
                                                                @Query("lat") double lat,
                                                                @Query("lng") double lng,
                                                                @Query("priority") String priority,
                                                                @Query("f") String userCurrentFloorLevel,
                                                                @Query("platform") String platform);

        @GET("locapi/get_navi_xytopoi")
        Call<CommonArrayResponse> getUserLocationToExit(@Query("b") String userCurrentBuildingId,
                                                        @Query("f") String userCurrentFloorLevel,
                                                        @Query("a_lat") double userLat,
                                                        @Query("a_lng") double userLng,
                                                        @Query("b_lat") String exitLat,
                                                        @Query("b_lng") String exitLng,
                                                        @Query("priority") String priority,
                                                        @Query("platform") String platform);

        @GET("locapi/get_navi_nearby_type")
        Call<CommonArrayResponse> getEmergencyRoute(@Query("b") String userCurrentBuildingId,
                                                    @Query("f") String userCurrentFloorLevel,
                                                    @Query("lat") double lat,
                                                    @Query("lng") double lng,
                                                    @Query("type") String type,
                                                    @Query("platform") String platform);

        @FormUrlEncoded
        @POST("locapi/guide_to_parkinglot")
        Call<CommonArrayResponse> getRouteToCar(@Field("user_lat") double userLat,
                                                @Field("user_lng") double userLng,
                                                @Field("user_floor") String userFloorLevel,
                                                @Field("parking_floor") String parkingSpaceFloorLevel,
                                                @Field("parking_number") int parkingSpaceNumber,
                                                @Field("platform") String platform,
                                                @Field("timestamp") String timestamp,
                                                @Field("mac") String mac);

        @FormUrlEncoded
        @POST("locapi/recommend_route")
        Call<CommonArrayResponse> getRecommendRoute(@Field("route_a") String route_a,
                                                    @Field("route_b") String route_b,
                                                    @Field("f") String userCurrentFloorLevel,
                                                    @Field("lat") double lat,
                                                    @Field("lng") double lng);

    }

    interface GoogleApiService {
        @GET("maps/api/directions/json")
        Call<CommonResponse> getGoogleRoute(@Query("origin") String origin,
                                            @Query("destination") String destination,
                                            @Query("sensor") String sensor);
    }

    private LocationService getLocationService() {
        return NetworkManager.getInstance().getRetrofit().create(LocationService.class);
    }

    private GoogleApiService getGoogleApiService() {
        return NetworkManager.getInstance().getGoogleApiRetrofit().create(GoogleApiService.class);
    }

    public void getBuildings(Activity activity, NetworkManager.NetworkManagerListener<Building[]> listener) {
        Log.e("OKOK", "getBuildings");
        DialogTools.getInstance().showProgress(activity);

        Call<CommonArrayResponse> call = getLocationService().getBuildings();

        NetworkManager.getInstance().addPostRequestToCommonArrayObj(activity, call, Building[].class, listener);
    }

    public void getFloors(Activity activity, String buildingId, NetworkManager.NetworkManagerListener<BuildingFloor[]> listener) {
        Log.e("OKOK", "getFloors");
        DialogTools.getInstance().showProgress(activity);

        Call<CommonArrayResponse> call = getLocationService().getFloors(buildingId);

        NetworkManager.getInstance().addGetRequestToCommonArrayObj(activity, call, BuildingFloor[].class, listener);
    }

    public void doSearch(Activity activity, String buildingId, String keyword, NetworkManager.NetworkManagerListener<BuildingFloor[]> listener) {
        Log.e("OKOK", "doSearch");
        DialogTools.getInstance().showProgress(activity);

        Call<CommonArrayResponse> call = getLocationService().getFloors(buildingId, keyword);

        NetworkManager.getInstance().addGetRequestToCommonArrayObj(activity, call, BuildingFloor[].class, listener);
    }

    public void getUserIndoorLocationToOutdoorPRoute(final Activity activity,
                                                     final String currentBuildingId, final String userCurrentFloorNumber,
                                                     final double userLat, final double userLng,
                                                     double outdoorPOILat, double outdoorPOILng,
                                                     final String priority,
                                                     final NetworkManager.NetworkManagerListener<NavigationRoutePOI[]> listener) {
        DialogTools.getInstance().showProgress(activity);

        Call<CommonResponse> call = getGoogleApiService().getGoogleRoute(userLat + "," + userLng,
                outdoorPOILat + "," + outdoorPOILng,
                "false");

        NetworkManager.getInstance().addGetRequestToCommonObj(activity, call, GoogleNavigationRoute.class,
                new NetworkManager.NetworkManagerListener<GoogleNavigationRoute>() {
                    @Override
                    public void onSucceed(GoogleNavigationRoute response) {
                        if (response.getStatus().equals(GoogleNavigationRoute.GET_ROUTE_STATUS_OK)) {

                            final List<NavigationRoutePOI> poiList = response.getNavigationRoute();
                            if (poiList != null && !poiList.isEmpty()) {
                                NavigationRoutePOI poi = poiList.get(0);

                                getUserLocationToExit(activity, currentBuildingId, userCurrentFloorNumber, userLat, userLng,
                                        poi.getLatitude(), poi.getLongitude(), priority, listener);
                            }
                        } else {

                        }
                    }

                    @Override
                    public void onFail(String errorMsg, boolean shouldRetry) {

                    }
                });
    }

    public void getUserLocationToIndoorPRoute(Activity activity, String poiId, double lat, double lng, String userCurrentFloorLevel,
                                              String priority,
                                              NetworkManager.NetworkManagerListener<NavigationRoutePOI[]> listener) {
        DialogTools.getInstance().showProgress(activity);

        Call<CommonArrayResponse> call = getLocationService().getUserLocationToIndoorPRoute(poiId, lat, lng, priority, userCurrentFloorLevel, platform);
        Log.e("@W@", "call" + call.request().url().toString());
        NetworkManager.getInstance().addGetRequestToCommonArrayObj(activity, call, NavigationRoutePOI[].class, listener);
    }

    public void getRecommendRoute(Activity activity, String route_a, String route_b, String userCurrentFloorLevel, double lat, double lng,
                                  NetworkManager.NetworkManagerListener<NavigationRoutePOI[]> listener) {
        DialogTools.getInstance().showProgress(activity);

//        String jsonStr_route_a = NetworkManager.getInstance().getGson().toJson(route_a);
//        String jsonStr_route_b = NetworkManager.getInstance().getGson().toJson(route_b);

        Call<CommonArrayResponse> call = getLocationService().getRecommendRoute(route_a, route_b, userCurrentFloorLevel, lat, lng);
        Log.e("@W@", "call" + call.request().url().toString());
        NetworkManager.getInstance().addPostRequestToCommonArrayObj(activity, call, NavigationRoutePOI[].class, listener);
    }


    public void getUserLocationToExit(Activity activity, String userCurrentBuildingId, String userCurrentFloorLevel,
                                      double userLat, double userLng, String exitLat, String exitLng,
                                      String priority,
                                      NetworkManager.NetworkManagerListener<NavigationRoutePOI[]> listener) {
        DialogTools.getInstance().showProgress(activity);

        Call<CommonArrayResponse> call = getLocationService().getUserLocationToExit(userCurrentBuildingId, userCurrentFloorLevel,
                userLat, userLng, exitLat, exitLng, priority, platform);

        NetworkManager.getInstance().addGetRequestToCommonArrayObj(activity, call, NavigationRoutePOI[].class, listener);
    }

    public void getEmergencyRoute(Activity activity, String userCurrentBuildingId, String userCurrentFloorLevel,
                                  double userLat, double userLng, String type,
                                  NetworkManager.NetworkManagerListener<NavigationRoutePOI[]> listener) {
        DialogTools.getInstance().showProgress(activity);

        Call<CommonArrayResponse> call = getLocationService().getEmergencyRoute(userCurrentBuildingId,
                userCurrentFloorLevel,
                userLat,
                userLng,
                type,
                platform);

        NetworkManager.getInstance().addGetRequestToCommonArrayObj(activity, call, NavigationRoutePOI[].class, listener);
    }

    public void getRouteToCar(Activity activity,
                              double userLat, double userLng, String userFloorLevel,
                              String parkingSpaceFloorLevel, int parkingSpaceNumber,
                              NetworkManager.NetworkManagerListener<NavigationRoutePOI[]> listener) {

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);

        Call<CommonArrayResponse> call = getLocationService().getRouteToCar(userLat, userLng,
                userFloorLevel, parkingSpaceFloorLevel, parkingSpaceNumber, platform, currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequestToCommonArrayObj(activity, false, call, NavigationRoutePOI[].class, listener);
    }

}
