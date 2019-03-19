package com.omni.syntrendsdk.manager;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.omni.syntrendsdk.module.Building;
import com.omni.syntrendsdk.module.BuildingFloor;
import com.omni.syntrendsdk.module.OmniClusterItem;
import com.omni.syntrendsdk.module.OmniFloor;
import com.omni.syntrendsdk.module.POI;
import com.omni.syntrendsdk.network.LocationApi;
import com.omni.syntrendsdk.network.NetworkManager;
import com.omni.syntrendsdk.tool.DialogTools;
import com.omni.syntrendsdk.tool.PreferencesTools;
import com.omni.syntrendsdk.tool.SynTrendText;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataCacheManager {

    private static DataCacheManager mDataCacheManager;

    private Gson mGson;

    private OmniFloor mCurrentShowFloor;
    private String mUserCurrentFloorLevel;
    private String mUserCurrentFloorPlanId;

    // key : floorNumber, value : route point list on this floor
    private Map<String, List<LatLng>> mFloorRoutePointsMap;
    // key : buildingId, value : clusters in this building
    private Map<String, List<OmniClusterItem>> mBuildingClusterItemsMap;
    // key : POI id, value : cluster item
    private Map<String, OmniClusterItem> mPOIClusterItemMap;
    // key : floorNumber, value : the polyline of this floor
    private Map<String, Polyline> mFloorPolylineMap;
    // key : floorNumber, value : arrow markers on this floor
    private Map<String, List<Marker>> mFloorArrowMarkersMap;
    private List<LatLng> mRoutePointList;
    //    private int mEnabledBuildingCount = 0;
//    private int mAlreadyGetBuildingFloorsCount = 0;
    // key : buildingId, value : zMarker in this building
    private Map<String, List<Marker>> mZMarkerMap;
    private List<Marker> mZMarkerList;

    public static DataCacheManager getInstance() {
        if (mDataCacheManager == null) {
            mDataCacheManager = new DataCacheManager();
        }
        return mDataCacheManager;
    }

    private Gson getGson() {
        if (mGson == null) {
            mGson = new Gson();
        }
        return mGson;
    }

    public void initAllBuildingsData(final Activity activity) {
        Log.e("OKOK", "initAllBuildingsData");
        LocationApi.getInstance().getBuildings(activity, new NetworkManager.NetworkManagerListener<Building[]>() {
            @Override
            public void onSucceed(Building[] buildings) {
                Log.e("OKOK", "onSucceed");
                DataCacheManager.this.setAllBuildings(activity, buildings);
                initBuildingList(activity);
            }

            @Override
            public void onFail(String errorMsg, boolean shouldRetry) {
                Log.e("OKOK", "errorMsg" + errorMsg);
                DialogTools.getInstance().showNoNetworkMessage(activity);
            }
        });
    }

    private void initBuildingList(final Activity activity) {
        Log.e("OKOK", "initBuildingList");
//        String floorNotExistBuildingId = checkFloorDataExist(activity);
//        if (!TextUtils.isEmpty(floorNotExistBuildingId)) {
//            getBuildingFloorsByBuildingId(activity, floorNotExistBuildingId);
//        }

//        for (final Building building : buildings) {
        Building[] buildings = DataCacheManager.this.getAllBuildings(activity);
        /**mEnabledBuildingCount = 0;
         for (Building building : buildings) {
         if (building.getEnabled().equals("Y")) {
         mEnabledBuildingCount++;
         Log.e("@W@", "mEnabledBuildingCount : " + mEnabledBuildingCount);
         }
         }**/

        for (final Building building : buildings) {
//        if (buildings != null && buildings.length != 0) {
//            final Building building = buildings[0];

            if (building.getEnabled().equals("Y")) {

                LocationApi.getInstance().getFloors(activity, building.getId(), new NetworkManager.NetworkManagerListener<BuildingFloor[]>() {
                    @Override
                    public void onSucceed(BuildingFloor[] floors) {
                        Log.e("OKOK", "onSucceed" + floors.length);
                        DataCacheManager.this.setBuildingFloors(activity, building.getId(), floors);

                        /**mAlreadyGetBuildingFloorsCount++;
                         Log.e("@W@", "mAlreadyGetBuildingFloorsCount : " + mAlreadyGetBuildingFloorsCount);
                         if (mAlreadyGetBuildingFloorsCount == mEnabledBuildingCount) {
                         DialogTools.getInstance().dismissProgress(activity);
                         }**/
                    }

                    @Override
                    public void onFail(String errorMsg, boolean shouldRetry) {
//                        DialogTools.getInstance().showErrorMessage(activity, "API Error", "get building floors error");
                        DialogTools.getInstance().showNoNetworkMessage(activity);
                    }
                });
            }
        }
//        }
    }


    public void setAllBuildings(Context context, Building[] allBuildings) {
        Log.e("OKOK", "setAllBuildings");
        PreferencesTools.getInstance().saveProperty(context, PreferencesTools.KEY_ALL_BUILDINGS, allBuildings);
    }

    public Building[] getAllBuildings(Context context) {
        Log.e("OKOK", "getAllBuildings");
        String allBuildingsStr = PreferencesTools.getInstance().getProperty(context, PreferencesTools.KEY_ALL_BUILDINGS);
        Log.e("OKOK", "allBuildingsStr" + allBuildingsStr);
        if (allBuildingsStr == null) {
            return null;
        } else {
            return getGson().fromJson(allBuildingsStr, Building[].class);
        }
    }

    public BuildingFloor getMainGroundFloorPlanId(Context context) {
        Building[] buildings = getAllBuildings(context);
        if (buildings != null) {
            Log.e("OKOK", "getMainGroundFloorPlanId");
            for (Building building : buildings) {
                if (building.getDesc().contains("三創生活園區")) {
                    BuildingFloor[] floors = getFloorsByBuildingId(context, building.getId());
                    if (floors != null) {
                        for (BuildingFloor floor : floors) {
                            if (floor.getNumber().equals("1")) {
                                return floor;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public BuildingFloor getSearchFloorPlanId(Context context, int selectedPoiId) {
        Building[] buildings = getAllBuildings(context);
        if (buildings != null) {
            Log.e("OKOK", "getSearchFloorPlanId");
            for (Building building : buildings) {
                if (building.getDesc().contains("三創生活園區")) {
                    BuildingFloor[] floors = getFloorsByBuildingId(context, building.getId());
                    if (floors != null) {
                        for (BuildingFloor floor : floors) {
                            for (POI poi : floor.getPois()) {
                                if (poi.getId() == selectedPoiId) {
                                    return floor;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public POI getEntrancePOI(BuildingFloor floor) {
        if (floor == null) {
            return null;
        } else {
            for (POI poi : floor.getPois()) {
                if (poi.getName().equals("中心廣場入口")) {
//                if (poi.getIsEntrance().equals("Y")) {
                    Log.e("OKOK", "poi.getName" + poi.getName());
                    return poi;
                }
            }
            return null;
        }
    }

    public POI getMainLibraryEntrancePOI(Context context) {
        BuildingFloor floor = getMainGroundFloorPlanId(context);
        if (floor != null) {
            return getEntrancePOI(floor);
        }
        return null;
    }

    public void setBuildingFloors(Context context, String buildingId, BuildingFloor[] floors) {
        Log.e("OKOK", "setBuildingFloors");
        String floorsStr = PreferencesTools.getInstance().getProperty(context, PreferencesTools.KEY_FLOORS);
        Map<String, BuildingFloor[]> buildingFloorsMap;
        if (floorsStr == null) {
            buildingFloorsMap = new HashMap<>();
        } else {
            Type type = new TypeToken<HashMap<String, BuildingFloor[]>>() {
            }.getType();
            buildingFloorsMap = getGson().fromJson(floorsStr, type);
        }
        /** Don't save floor and poi data. 23/10/2017 17:52
         if ((!buildingFloorsMap.containsKey(buildingId)) || buildingFloorsMap.get(buildingId) == null) {*/
        buildingFloorsMap.put(buildingId, floors);

        PreferencesTools.getInstance().saveProperty(context, PreferencesTools.KEY_FLOORS, getGson().toJson(buildingFloorsMap));
    }

    public Map<String, BuildingFloor[]> getAllBuildingFloorsMap(Activity activity) {
        String floorsStr = PreferencesTools.getInstance().getProperty(activity, PreferencesTools.KEY_FLOORS);
        if (floorsStr == null) {
            return null;
        } else {
            Type type = new TypeToken<HashMap<String, BuildingFloor[]>>() {
            }.getType();
            Map<String, BuildingFloor[]> buildingFloorsMap = getGson().fromJson(floorsStr, type);
            return buildingFloorsMap;
        }
    }

    public String getFloorNumberByPlanId(Activity activity, String floorPlanId) {
        Map<String, BuildingFloor[]> allBuildingFloorsMap = getAllBuildingFloorsMap(activity);
        if (allBuildingFloorsMap != null) {
            for (String buildingId : allBuildingFloorsMap.keySet()) {
                BuildingFloor[] floors = allBuildingFloorsMap.get(buildingId);
                for (BuildingFloor floor : floors) {
                    if (floor.getFloorPlanId().equals(floorPlanId)) {
                        return floor.getNumber();
                    }
                }
            }
        }
        return "";
    }

    public boolean containsFloor(Activity activity, String floorPlanId) {
        Map<String, BuildingFloor[]> allBuildingFloorsMap = getAllBuildingFloorsMap(activity);
        if (allBuildingFloorsMap != null) {
            for (BuildingFloor[] floors : allBuildingFloorsMap.values()) {
                for (BuildingFloor floor : floors) {
                    if (floor.getFloorPlanId().equals(floorPlanId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String getBuildingIdByFloorPlanId(Activity activity, String floorPlanId) {
        String buildingId = null;

        Map<String, BuildingFloor[]> buildingFloorsMap = getAllBuildingFloorsMap(activity);

        if (buildingFloorsMap != null) {
            for (String blId : buildingFloorsMap.keySet()) {
                if (!TextUtils.isEmpty(buildingId)) {
                    break;
                }

                BuildingFloor[] floors = buildingFloorsMap.get(blId);
                for (BuildingFloor floor : floors) {
                    if (floor.getFloorPlanId().equals(floorPlanId)) {
                        return blId;
                    }
                }
            }
        }
        return buildingId;
    }

    public Building getBuildingByFloorPlanId(Activity activity, String floorPlanId) {
        String buildingId = getBuildingIdByFloorPlanId(activity, floorPlanId);

        if (buildingId != null) {
            Building[] buildings = getAllBuildings(activity);
            if (buildings != null) {
                for (Building building : buildings) {
                    if (building.getId().equals(buildingId)) {
                        return building;
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    public BuildingFloor[] getFloorsByBuildingId(Context activity, String buildingId) {
        String floorsStr = PreferencesTools.getInstance().getProperty(activity, PreferencesTools.KEY_FLOORS);
        Log.e("OKOK", "floorsStr" + floorsStr);
        if (floorsStr == null) {
            return null;
        } else {
            Type type = new TypeToken<HashMap<String, BuildingFloor[]>>() {
            }.getType();
            Map<String, BuildingFloor[]> buildingFloorsMap = getGson().fromJson(floorsStr, type);

            return buildingFloorsMap.get(buildingId);
        }
    }

    @Nullable
    public BuildingFloor getBuildingFloor(Activity activity, String buildingId, String floorPlanId) {
        BuildingFloor[] floors = getFloorsByBuildingId(activity, buildingId);
        BuildingFloor floor = null;
        if (floors != null) {
            for (BuildingFloor f : floors) {
                if (f.getFloorPlanId().equals(floorPlanId)) {
                    floor = f;
                    break;
                }
            }
        }
        return floor;
    }

    @NonNull
    public OmniFloor getCurrentShowFloor() {
        if (mCurrentShowFloor == null) {
            mCurrentShowFloor = new OmniFloor(SynTrendText.USER_OUTDOOR, SynTrendText.USER_OUTDOOR);
        }
        return mCurrentShowFloor;
    }

    public void setCurrentShowFloor(OmniFloor floor) {
        mCurrentShowFloor = floor;
    }

    public String getUserCurrentBuildingId(Activity activity) {
        OmniFloor omniFloor = getCurrentShowFloor();
        return omniFloor == null ? null :
                getBuildingIdByFloorPlanId(activity, omniFloor.getFloorPlanId());
    }

    @NonNull
    public String getUserCurrentFloorLevel(Activity activity) {
//        return (mUserCurrentFloorLevel == null) ? "1" : mUserCurrentFloorLevel;
        String userCurrentFloorPlanId = getUserCurrentFloorPlanId();
        String buildingId = getBuildingIdByFloorPlanId(activity, userCurrentFloorPlanId);
        if (!TextUtils.isEmpty(buildingId)) {
            BuildingFloor floor = getBuildingFloor(activity, buildingId, userCurrentFloorPlanId);

            return floor.getNumber();
        }
        return "1";
    }

    public void setUserCurrentFloorLevel(String userCurrentFloorLevel) {
        Log.e("OKOK", "setUserCurrentFloorLevel");
        mUserCurrentFloorLevel = userCurrentFloorLevel;
    }

    public void setUserCurrentFloorPlanId(String planId) {
        mUserCurrentFloorPlanId = planId;
    }

    @NonNull
    public String getUserCurrentFloorPlanId() {
        if (TextUtils.isEmpty(mUserCurrentFloorPlanId)) {
            mUserCurrentFloorPlanId = SynTrendText.USER_OUTDOOR;
        }
        Log.e("OKOK", "getUserCurrentFloorPlanId" + mUserCurrentFloorPlanId);
        return mUserCurrentFloorPlanId;
    }

    public void setFloorRoutePointsMap(String floorNumber, List<LatLng> routePointList) {
        if (mFloorRoutePointsMap == null) {
            mFloorRoutePointsMap = new HashMap<>();
        }
        List<LatLng> list = new ArrayList<>(routePointList);
        mFloorRoutePointsMap.put(floorNumber, list);
    }

    @Nullable
    public List<LatLng> getFloorRoutePointList(String floorNumber) {
        return mFloorRoutePointsMap == null ? null : mFloorRoutePointsMap.get(floorNumber);
    }

    @Nullable
    public Map<String, List<LatLng>> getFloorRoutePointsMap() {
        return mFloorRoutePointsMap;
    }

    @Nullable
    public List<LatLng> getUserCurrentFloorRoutePointList(Activity activity) {
        return mFloorRoutePointsMap == null ? null : mFloorRoutePointsMap.get(getUserCurrentFloorLevel(activity));
//        return mFloorRoutePointsMap == null ? null : mFloorRoutePointsMap.get(mUserCurrentFloorLevel);
    }

    public void setBuildingClusterItems(String buildingId, List<OmniClusterItem> itemList) {
        Log.e("OKOK","setBuildingClusterItems");
        if (mBuildingClusterItemsMap == null) {
            mBuildingClusterItemsMap = new HashMap<>();
        }
        mBuildingClusterItemsMap.put(buildingId, itemList);
    }

    @Nullable
    public List<OmniClusterItem> getClusterListByBuildingId(String buildingId) {
        Log.e("OKOK","getClusterListByBuildingId");
        return mBuildingClusterItemsMap == null ? null : mBuildingClusterItemsMap.get(buildingId);
    }

    public void setPOIClusterItemMap(String poiId, OmniClusterItem item) {
        if (mPOIClusterItemMap == null) {
            mPOIClusterItemMap = new HashMap<>();
        }
        mPOIClusterItemMap.put(poiId, item);
    }

    @Nullable
    public OmniClusterItem getClusterItemByPOIId(String poiId) {
        return mPOIClusterItemMap == null ? null : mPOIClusterItemMap.get(poiId);
    }

    @NonNull
    public Map<String, Polyline> getFloorPolylineMap() {
        if (mFloorPolylineMap == null) {
            mFloorPolylineMap = new HashMap<>();
        }
        return mFloorPolylineMap;
    }

    @Nullable
    public List<LatLng> getCurrentRoutePointList() {
        if (mRoutePointList == null) {
            mRoutePointList = new ArrayList<>();
        }
        return mRoutePointList;
    }

    public void setCurrentRoutePointList(List<LatLng> currentRoutePointList) {
        mRoutePointList = currentRoutePointList;
    }

    public void clearAllPolyline() {
        Log.e("@W@", "clearAllPolyline mFloorPolylineMap == null ? " + (mFloorPolylineMap == null));
        if (mFloorPolylineMap != null) {
            for (Polyline polyline : mFloorPolylineMap.values()) {
                polyline.remove();
            }
            mFloorPolylineMap.clear();
        }
    }

    public void clearAllArrowMarkers() {
        if (mRoutePointList != null) {
            mRoutePointList.clear();
        }
        if (mFloorArrowMarkersMap != null) {
            for (List<Marker> list : mFloorArrowMarkersMap.values()) {
                for (Marker marker : list) {
                    marker.remove();
                }
            }
            mFloorArrowMarkersMap.clear();
        }
        if (mFloorRoutePointsMap != null) {
            mFloorRoutePointsMap.clear();
        }
    }

    @Nullable
    public List<Marker> getFloorArrowMarkerList(String floorNumber) {
        return mFloorArrowMarkersMap == null ? null : mFloorArrowMarkersMap.get(floorNumber);
    }

    @Nullable
    public List<Marker> getCurrentFloorArrowMarkerList() {
        return mFloorArrowMarkersMap == null ? null : mFloorArrowMarkersMap.get(mUserCurrentFloorLevel);
    }

    public Map<String, List<Marker>> getFloorArrowMarkersMap() {
        if (mFloorArrowMarkersMap == null) {
            mFloorArrowMarkersMap = new HashMap<>();
        }
        return mFloorArrowMarkersMap;
    }

    public void setFloorArrowMarkersMap(String floorNumber, List<Marker> markerList) {
        if (mFloorArrowMarkersMap == null) {
            mFloorArrowMarkersMap = new HashMap<>();
        }
        mFloorArrowMarkersMap.put(floorNumber, markerList);
    }


    /**
     * Guide part
     */
    private OmniFloor mGuideCurrentShowFloor;

    public OmniFloor getGuideCurrentShowFloor() {
//        if (mGuideCurrentShowFloor == null) {
//            mGuideCurrentShowFloor = new OmniFloor("1", PrehistoryText.NLPI_1F_FLOOR_PLAN_ID);
//        }

        return mGuideCurrentShowFloor;
    }

    public void setGuideCurrentShowFloor(OmniFloor guideCurrentShowFloor) {
        this.mGuideCurrentShowFloor = guideCurrentShowFloor;
    }

    public boolean isInBuilding(Activity activity) {

        String planId = getUserCurrentFloorPlanId();
        if (TextUtils.isEmpty(planId)) {
            return false;
        } else {

            String buildingId = getBuildingIdByFloorPlanId(activity, planId);
            return !TextUtils.isEmpty(buildingId);

////            Log.e("@W@", " planId : " + planId);
//            Map<String, BuildingFloor[]> allBuildingFloorsMap = getAllBuildingFloorsMap(activity);
//            if (allBuildingFloorsMap == null) {
////                Log.e("@W@", "isInBuilding getAllBuildingFloorsMap == null");
//                return false;
//            } else {
////                for (String id : allBuildingFloorsMap.keySet()) {
////                    Log.e("@W@", "building id : " + id);
////                }
//                for (BuildingFloor[] floors : allBuildingFloorsMap.values()) {
//                    for (BuildingFloor floor : floors) {
//                        if (floor.getFloorPlanId().equals(planId)) {
//                            return true;
//                        }
//                    }
//                }
//                return false;
//            }
        }
    }

    public boolean isInSameBuilding(Activity activity, String compareBuildingId) {
        String planId = getUserCurrentFloorPlanId();
        if (TextUtils.isEmpty(planId)) {
            return false;
        } else {
            String userCurrentBuildingId = getBuildingIdByFloorPlanId(activity, planId);

            return !TextUtils.isEmpty(userCurrentBuildingId) && userCurrentBuildingId.equals(compareBuildingId);
        }
    }

    public List<Marker> getZMarkerListByBuildingId(String buildingId) {
        return mZMarkerMap == null ? null : mZMarkerMap.get(buildingId);
    }

    public void setZMarkerByBuildingId(String buildingId, Marker marker) {
        List<Marker> list = getZMarkerListByBuildingId(buildingId);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(marker);

        if (mZMarkerMap == null) {
            mZMarkerMap = new ArrayMap<>();
        }
        mZMarkerMap.put(buildingId, list);
    }

    public void setZMarkerListByBuildingId(String buildingId, List<Marker> list) {
        if (mZMarkerMap == null) {
            mZMarkerMap = new ArrayMap<>();
        }
        mZMarkerMap.put(buildingId, list);
    }
}