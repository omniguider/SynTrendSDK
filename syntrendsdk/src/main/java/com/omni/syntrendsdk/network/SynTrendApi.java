package com.omni.syntrendsdk.network;

import android.app.Activity;

import com.omni.syntrendsdk.module.CommonArrayResponse;
import com.omni.syntrendsdk.module.SendBeaconBatteryResponse;
import com.omni.syntrendsdk.module.SetBeaconBatteryResponse;
import com.omni.syntrendsdk.tool.DialogTools;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class SynTrendApi {

    private static SynTrendApi mSynTrendApi;

    public static SynTrendApi getInstance() {
        if (mSynTrendApi == null) {
            mSynTrendApi = new SynTrendApi();
        }
        return mSynTrendApi;
    }

    interface SynTrendService {

//        @GET("api/get_building_intro_list")
//        Call<CommonArrayResponse> getBuildingIntroList();
//
//        @GET("api/get_activity_now")
//        Call<CommonArrayResponse> getActivityNow();
//
//        @GET("api/get_activity_old")
//        Call<CommonArrayResponse> getActivityOld();
//
//        @GET("api/get_activity_future")
//        Call<CommonArrayResponse> getActivityFuture();
//
//        @GET("api/get_exh")
//        Call<CommonArrayResponse> getExhibition();
//
//        @FormUrlEncoded
//        @POST("api/get_beacon")
//        Call<CommonArrayResponse> getBeaconsPushInfo(@Field("minor") String minors,
//                                                     @Field("device_id") String deviceId,
//                                                     @Field("timestamp") String timestamp,
//                                                     @Field("mac") String mac);

        @FormUrlEncoded
        @POST("api/set_beacon")
        Call<SetBeaconBatteryResponse> setBeaconBatteryLevel(@Field("beacon_mac") String beacon_mac,
                                                              @Field("voltage") String voltage,
                                                              @Field("timestamp") String timestamp,
                                                              @Field("mac") String mac);

    }

    private SynTrendService getSynTrendService() {
        return NetworkManager.getInstance().getRetrofit().create(SynTrendService.class);
    }

    public void setBeaconBatteryLevel(Activity activity, String beaconMac, String voltage, NetworkManager.NetworkManagerListener<SetBeaconBatteryResponse> listener) {

//        DialogTools.getInstance().showProgress(activity);

        long currentTimestamp = System.currentTimeMillis() / 1000L;
        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);
        Call<SetBeaconBatteryResponse> call = getSynTrendService().setBeaconBatteryLevel(
                beaconMac, voltage, currentTimestamp + "", mac);

        NetworkManager.getInstance().addPostRequest(activity, call, SetBeaconBatteryResponse.class, listener);
    }

//    public void getBuildingIntroList(Activity activity, NetworkManager.NetworkManagerListener<BuildingIntroData[]> listener) {
//
//        DialogTools.getInstance().showProgress(activity);
//        Call<CommonArrayResponse> call = geNTCRIService().getBuildingIntroList();
//        NetworkManager.getInstance().addGetRequestToCommonArrayObj(activity, call, BuildingIntroData[].class, listener);
//    }
//
//    public void getActivityNow(Activity activity, NetworkManager.NetworkManagerListener<ActivityData[]> listener) {
//
//        DialogTools.getInstance().showProgress(activity);
//        Call<CommonArrayResponse> call = geNTCRIService().getActivityNow();
//        NetworkManager.getInstance().addGetRequestToCommonArrayObj(activity, call, ActivityData[].class, listener);
//    }
//
//    public void getActivityOld(Activity activity, NetworkManager.NetworkManagerListener<ActivityData[]> listener) {
//
//        DialogTools.getInstance().showProgress(activity);
//        Call<CommonArrayResponse> call = geNTCRIService().getActivityOld();
//        NetworkManager.getInstance().addGetRequestToCommonArrayObj(activity, call, ActivityData[].class, listener);
//    }
//
//    public void getActivityFuture(Activity activity, NetworkManager.NetworkManagerListener<ActivityData[]> listener) {
//
//        DialogTools.getInstance().showProgress(activity);
//        Call<CommonArrayResponse> call = geNTCRIService().getActivityFuture();
//        NetworkManager.getInstance().addGetRequestToCommonArrayObj(activity, call, ActivityData[].class, listener);
//    }
//
//    public void getExhibition(Activity activity, NetworkManager.NetworkManagerListener<ExhibitionData[]> listener) {
//
//        DialogTools.getInstance().showProgress(activity);
//        Call<CommonArrayResponse> call = geNTCRIService().getExhibition();
//        NetworkManager.getInstance().addGetRequestToCommonArrayObj(activity, call, ExhibitionData[].class, listener);
//    }
//
//    public void getBeaconsPushInfo(Activity activity, String minors,
//                                   NetworkManager.NetworkManagerListener<BeaconResponse[]> listener) {
//
//        long currentTimestamp = System.currentTimeMillis() / 1000L;
//        String mac = NetworkManager.getInstance().getMacStr(currentTimestamp);
//
//        Call<CommonArrayResponse> call = geNTCRIService().getBeaconsPushInfo(
//                minors,
//                NetworkManager.getInstance().getDeviceId(activity),
//                currentTimestamp + "",
//                mac);
//
//        NetworkManager.getInstance().addPostRequestToCommonArrayObj(activity, false, call, BeaconResponse[].class, listener);
//    }
}
