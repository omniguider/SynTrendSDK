package com.omni.syntrendsdk.service;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;
import com.indooratlas.android.sdk.resources.IAResourceManager;
import com.omni.syntrendsdk.manager.DataCacheManager;
import com.omni.syntrendsdk.module.OmniEvent;
import com.omni.syntrendsdk.module.UserCurrentLocation;
import com.omni.syntrendsdk.module.UserLocationRequestType;
import com.omni.syntrendsdk.tool.SynTrendText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OGService implements IARegion.Listener,
        IALocationListener,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public interface GoogleApiClientConnectCallBack {
        void onGoogleApiClientConnected();
    }

    private Activity mActivity;
    private IALocationManager mIALocationManager;
    private IAResourceManager mIAResourceManager;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIsIndoor = false;
    private GoogleApiClientConnectCallBack mGoogleApiClientConnectCallBack;
    private String mCurrentVenueId;
    private String mCurrentFloorId;
    private String mPreviousFloorId;
    private Location mLocation;
    private List<String[]> mTraceIdRecordList = new ArrayList<>();

    private UserLocationRequestType mRequestUserIndoorLocation = UserLocationRequestType.AUTOMATICALLY;

    private Handler mTimeHandler;
    private HandlerThread mTimeHandlerThread;
    private int mCountTime = 0;
    private final Runnable timerRun = new Runnable() {
        public void run() {
            ++mCountTime; // 經過的秒數 + 1

            addUserLocationToList(mLocation, mIsIndoor ? DataCacheManager.getInstance().getUserCurrentFloorPlanId() : SynTrendText.USER_OUTDOOR);

            if (mCountTime == 6) {
                mCountTime = 0;
//                sendUserLocationToServer();
            }

            mTimeHandler.removeCallbacks(this);
            mTimeHandler.postDelayed(this, 10000);
        }
    };
    private List<UserCurrentLocation> mUserLocationList;

    private EventBus mEventBus;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OmniEvent event) {
        switch (event.getType()) {
            case OmniEvent.TYPE_REQUEST_LAST_LOCATION:
                if (DataCacheManager.getInstance().getUserCurrentFloorPlanId().equals(SynTrendText.USER_OUTDOOR)) {
                    mEventBus.post(new OmniEvent(OmniEvent.TYPE_USER_OUTDOOR_LOCATION, mLocation));
                } else {
                    mEventBus.post(new OmniEvent(OmniEvent.TYPE_USER_INDOOR_LOCATION, mLocation));
                }
                break;

            case OmniEvent.TYPE_REQUEST_USER_LOCATION_INDOOR_ONLY:
                mRequestUserIndoorLocation = UserLocationRequestType.INDOOR_ONLY;
                break;

            case OmniEvent.TYPE_REQUEST_USER_LOCATION_OUTDOOR_ONLY:
                mRequestUserIndoorLocation = UserLocationRequestType.OUTDOOR_ONLY;
                break;

            case OmniEvent.TYPE_REQUEST_USER_LOCATION_AUTOMATICALLY:
                mRequestUserIndoorLocation = UserLocationRequestType.AUTOMATICALLY;
                break;

            case OmniEvent.TYPE_CHECK_USER_LOCATION_REQUEST_TYPE:
                mEventBus.post(new OmniEvent(OmniEvent.TYPE_USER_LOCATION_REQUEST_TYPE, mRequestUserIndoorLocation));
                break;
        }
    }

    public OGService(Activity activity) {
        mActivity = activity;

        if (mEventBus == null) {
            mEventBus = EventBus.getDefault();
        }
        mEventBus.register(this);
    }

    public void startService(GoogleApiClientConnectCallBack callBack) {

//        mOGLocationListener = listener;
        mGoogleApiClientConnectCallBack = callBack;

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mActivity).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        initLocationService();
        IALocationRequest request = IALocationRequest.create();
        request.setFastestInterval(1000);
        request.setSmallestDisplacement(0.6f);

        mIALocationManager.removeLocationUpdates(this);
        mIALocationManager.requestLocationUpdates(request, this);
    }

    private void initLocationService() {
        if (mIALocationManager == null) {
            Log.e("OKOK", "mIALocationManager");
            mIALocationManager = IALocationManager.create(mActivity);
        } else {
            mIALocationManager.unregisterRegionListener(this);
        }
        mIALocationManager.registerRegionListener(this);
        if (mIAResourceManager == null) {
            mIAResourceManager = IAResourceManager.create(mActivity);
        }
    }

    public void stopService() {
        Log.e("OKOK","stopService");
        if (mIALocationManager != null) {
            mIALocationManager.removeLocationUpdates(this);
            mIALocationManager.unregisterRegionListener(this);
        }
    }

    public void destroy() {
        if (mEventBus != null) {
            mEventBus.unregister(this);
        }

        if (mTimeHandler != null && timerRun != null) {
            mTimeHandler.removeCallbacks(timerRun);
        }
        if (mTimeHandlerThread != null) {
            mTimeHandlerThread.interrupt();
            mTimeHandlerThread.quit();
        }

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        if (mIALocationManager != null) {
            mIALocationManager.destroy();
            Log.e("OKOK", "mIALocationManager.destroy()");
            mIALocationManager = null;
        }
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else {
            if (mLocationRequest == null) {
                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(1000);
                mLocationRequest.setFastestInterval(1000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            if (mGoogleApiClientConnectCallBack != null) {
                mGoogleApiClientConnectCallBack.onGoogleApiClientConnected();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        switch (mRequestUserIndoorLocation) {
            case INDOOR_ONLY:
                return;

            case OUTDOOR_ONLY:
                mLocation = location;

                EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_USER_OUTDOOR_LOCATION, mLocation));

                startSendUserLocation();

                addUserLocationToList(mLocation, SynTrendText.USER_OUTDOOR);
                break;

            case AUTOMATICALLY:
//                Log.e("@W@", "normal location is indoor : " + mIsIndoor);
                if (!mIsIndoor) {
                    mLocation = location;

                    EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_USER_OUTDOOR_LOCATION, mLocation));

                    startSendUserLocation();

                    addUserLocationToList(mLocation, SynTrendText.USER_OUTDOOR);
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(IALocation iaLocation) {
//        Log.e("@W@", "floor certainty : " + iaLocation.getFloorCertainty() + ", accuracy : " + iaLocation.getAccuracy());
        mTraceIdRecordList.add(new String[]{mIALocationManager.getExtraInfo().traceId});

        if (iaLocation != null && iaLocation.getFloorCertainty() > 0.8) {

            switch (mRequestUserIndoorLocation) {
                case INDOOR_ONLY:
                    mLocation = iaLocation.toLocation();

                    if (iaLocation.getRegion() != null) {
                        addUserLocationToList(mLocation, iaLocation.getRegion().getId());

                        EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_USER_INDOOR_LOCATION, mLocation));

                        startSendUserLocation();
                    }
                    break;

                case OUTDOOR_ONLY:
                    return;

                case AUTOMATICALLY:
//                    Log.e("@W@", "iaLocation is indoor : " + mIsIndoor);
                    if (mIsIndoor) {
                        mLocation = iaLocation.toLocation();

                        boolean haveToFetch = false;
                        if (TextUtils.isEmpty(mPreviousFloorId) ||
                                (!TextUtils.isEmpty(mPreviousFloorId)) && !mCurrentFloorId.equals(mPreviousFloorId)) {
                            haveToFetch = true;

                            mPreviousFloorId = mCurrentFloorId;
                        }

                        if (iaLocation.getRegion() != null) {
                            addUserLocationToList(mLocation, iaLocation.getRegion().getId());

                            EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_USER_INDOOR_LOCATION, mLocation));

                            startSendUserLocation();
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onEnterRegion(IARegion iaRegion) {
        if (iaRegion.getType() == IARegion.TYPE_UNKNOWN) {
            Log.e("@W@", "onEnterRegion unknown : ");
            mIsIndoor = false;
        } else if (iaRegion.getType() == IARegion.TYPE_VENUE) {
            Log.e("@W@", "onEnterRegion venue : " + iaRegion.getId());
            mIsIndoor = false;

        } else if (iaRegion.getType() == IARegion.TYPE_FLOOR_PLAN) {
            Log.e("@W@", "onEnterRegion floor plan : " + iaRegion.getId());
            mIsIndoor = true;

            DataCacheManager.getInstance().setUserCurrentFloorPlanId(iaRegion.getId());
            EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_FLOOR_PLAN_CHANGED, iaRegion.getId()));
        }
    }

    @Override
    public void onExitRegion(IARegion iaRegion) {
        if (iaRegion.getType() == IARegion.TYPE_UNKNOWN) {
            Log.e("@W@", "onExitRegion : unknown");
            mIsIndoor = false;
        } else if (iaRegion.getType() == IARegion.TYPE_VENUE) {
            Log.e("@W@", "onExitRegion venue : " + iaRegion.getId());
            mCurrentVenueId = "";
            mIsIndoor = false;
        } else if (iaRegion.getType() == IARegion.TYPE_FLOOR_PLAN) {
            Log.e("@W@", "onExitRegion floor plan : " + iaRegion.getId());
            mCurrentFloorId = "";
            mIsIndoor = false;

            DataCacheManager.getInstance().setUserCurrentFloorPlanId("");
        }
    }

    private void addUserLocationToList(Location location, String floorPlanId) {
        if (location == null) {
            return;
        }
        UserCurrentLocation userCurrentLocation = new UserCurrentLocation.Builder()
                .setFloorPlanId(floorPlanId)
                .setLat(String.valueOf(location.getLatitude()))
                .setLng(String.valueOf(location.getLongitude()))
                .setDate(new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date()))
                .build();

        if (mUserLocationList == null) {
            mUserLocationList = new ArrayList<>();
        }
        mUserLocationList.add(userCurrentLocation);
    }

//    public void sendUserLocationToServer() {
//        Log.w("@W@", "sendUserLocationToServer");
//        UserLoginInfo loginInfo = UserInfoManager.getInstance().getUserInfo(mActivity);
//        String loginToken = loginInfo == null ? "" : loginInfo.getServerLoginToken();
//
//        List<UserCurrentLocation> userLocationList = new ArrayList<>(getUserLocationList());
//        getUserLocationList().removeAll(userLocationList);
//        if (userLocationList.size() != 0) {
//            LocationApi.getInstance().sendUserLocation(mActivity,
//                    userLocationList,
//                    loginToken,
//                    new NetworkManager.NetworkManagerListener<SendUserLocationResponse>() {
//                        @Override
//                        public void onSucceed(SendUserLocationResponse response) {
//                            if (response.getResult().equals(NetworkManager.API_RESULT_TRUE)) {
//                                Log.e("@W@", "SendUserLocationResponse success msg : " + response.getMessage());
//                    List<GroupData> groupDatas = response.getData();
//                    if (groupDatas != null) {
//                        EventBus.getDefault().post(new OmniEvent(OmniEvent.TYPE_GROUP_RESPONSE, groupDatas));
//                    }
//                            } else {
//                                if (response != null) {
//                                    Log.e("@W@", "sendUserLocationToServer result false errorMsg : " + response.getErrorMessage() +
//                                            ", msg : " + response.getMessage());
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFail(String errorMsg, boolean shouldRetry) {
//                            Log.e("@W@", "sendUserLocationToServer onFail errorMsg : " + errorMsg);
//                        }
//                    });
//        }
//    }

    private List<UserCurrentLocation> getUserLocationList() {
        if (mUserLocationList == null) {
            return new ArrayList<>();
        } else {
            return mUserLocationList;
        }
    }

    private void startSendUserLocation() {

        if (mTimeHandlerThread == null) {
            mTimeHandlerThread = new HandlerThread("send_user_location_time_handler_thread");
            mTimeHandlerThread.start();
        }

        if (mTimeHandler == null) {
            mTimeHandler = new Handler(mTimeHandlerThread.getLooper());
            mTimeHandler.postDelayed(timerRun, 100);
        }
    }

    public List<String[]> getTraceIdRecordList() {
        return mTraceIdRecordList;
    }
}
