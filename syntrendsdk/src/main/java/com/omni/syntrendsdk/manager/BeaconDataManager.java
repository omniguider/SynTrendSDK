package com.omni.syntrendsdk.manager;

import android.app.Activity;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.omni.syntrendsdk.module.BeaconInfo;
import com.omni.syntrendsdk.tool.PreferencesTools;

import java.lang.reflect.Type;
import java.util.Map;

public class BeaconDataManager {

    private static BeaconDataManager sBeaconDataManager;

    private Gson mGson = new Gson();
    Type type = new TypeToken<Map<String, BeaconInfo>>() {
    }.getType();

    public static BeaconDataManager getInstance() {
        if (sBeaconDataManager == null) {
            sBeaconDataManager = new BeaconDataManager();
        }

        return sBeaconDataManager;
    }

    private BeaconDataManager() {

    }

    public Map<String, BeaconInfo> getAllBeaconData(Activity activity) {
        return PreferencesTools.getInstance().getProperties(activity,
                PreferencesTools.KEY_BEACON_NOTIFICATION_HISTORY,
                type, String.class, BeaconInfo.class);
    }

    public Map<String, BeaconInfo> getUnreadBeaconData(Activity activity) {
        return PreferencesTools.getInstance().getProperties(activity,
                PreferencesTools.KEY_BEACON_NOTIFICATION_HISTORY_UNREAD,
                type, String.class, BeaconInfo.class);
    }

    public void addReceivedBeaconData(Activity activity, Map<String, BeaconInfo> newMap) {
        String valueStr = PreferencesTools.getInstance().getProperty(activity, PreferencesTools.KEY_BEACON_NOTIFICATION_HISTORY);
        Map<String, BeaconInfo> allBeaconMap;
        if (TextUtils.isEmpty(valueStr)) {
            allBeaconMap = new ArrayMap<>();
        } else {
            allBeaconMap = mGson.fromJson(valueStr, type);
        }

        /**
         * Filter out unread beacon info.
         * */
        Map<String, BeaconInfo> unreadMap = null;

        for (String key : newMap.keySet()) {
            if (!allBeaconMap.containsKey(key)) {

                BeaconInfo data = newMap.get(key);
                data.setUnread(true);
                data.initPushDate();

                if (unreadMap == null) {
                    unreadMap = new ArrayMap<>();
                    unreadMap.put(key, data);
                }
            }
        }

        if (unreadMap != null) {
            addUnreadBeaconData(activity, unreadMap);
        }

        newMap.keySet().removeAll(allBeaconMap.keySet());
        allBeaconMap.putAll(newMap);

        PreferencesTools.getInstance().saveProperty(activity,
                PreferencesTools.KEY_BEACON_NOTIFICATION_HISTORY,
                allBeaconMap);
    }

    private void addUnreadBeaconData(Activity activity, Map<String, BeaconInfo> newMap) {
        String valueStr = PreferencesTools.getInstance().getProperty(activity, PreferencesTools.KEY_BEACON_NOTIFICATION_HISTORY_UNREAD);
        Map<String, BeaconInfo> unreadMap;
        if (TextUtils.isEmpty(valueStr)) {
            unreadMap = new ArrayMap<>();
        } else {
            unreadMap = mGson.fromJson(valueStr, type);
        }

        newMap.keySet().removeAll(unreadMap.keySet());
        unreadMap.putAll(newMap);

        PreferencesTools.getInstance().saveProperty(activity,
                PreferencesTools.KEY_BEACON_NOTIFICATION_HISTORY_UNREAD,
                unreadMap);
    }

    public void readBeaconData(Activity activity, String major, String minor, int beaconInfoId) {
        String unreadBeaconMapStr = PreferencesTools.getInstance().getProperty(activity, PreferencesTools.KEY_BEACON_NOTIFICATION_HISTORY_UNREAD);
        if (TextUtils.isEmpty(unreadBeaconMapStr)) {
            return;
        }

        String beaconKey = BeaconInfo.getBeaconKeyWithMajorMinor(major, minor);

        Map<String, BeaconInfo> unreadBeaconMap = mGson.fromJson(unreadBeaconMapStr, type);
        if (unreadBeaconMap.containsKey(beaconKey)) {
            BeaconInfo unreadBeaconData = unreadBeaconMap.get(beaconKey);
            boolean isAllPushContentRead = true;

            if (unreadBeaconData.getId() == beaconInfoId) {
                unreadBeaconData.setUnread(false);
            }

            for (BeaconInfo info : unreadBeaconMap.values()) {
                if (info.isUnread()) {
                    isAllPushContentRead = false;
                }
            }

            if (isAllPushContentRead) {
                unreadBeaconMap.remove(beaconKey);
            }

            PreferencesTools.getInstance().saveProperty(activity,
                    PreferencesTools.KEY_BEACON_NOTIFICATION_HISTORY_UNREAD,
                    unreadBeaconMap);
        }

        String allBeaconMapStr = PreferencesTools.getInstance().getProperty(activity, PreferencesTools.KEY_BEACON_NOTIFICATION_HISTORY);
        Map<String, BeaconInfo> allBeaconMap = mGson.fromJson(allBeaconMapStr, type);
        BeaconInfo data = allBeaconMap.get(beaconKey);
        data.setUnread(false);

        PreferencesTools.getInstance().saveProperty(activity,
                PreferencesTools.KEY_BEACON_NOTIFICATION_HISTORY,
                allBeaconMap);
    }

    public void removeBeaconData(Activity activity, String major, String minor) {

        String beaconKey = BeaconInfo.getBeaconKeyWithMajorMinor(major, minor);

        Map<String, BeaconInfo> allBeaconDataMap = getAllBeaconData(activity);
        if (allBeaconDataMap.containsKey(beaconKey)) {
            allBeaconDataMap.remove(beaconKey);

            PreferencesTools.getInstance().saveProperty(activity,
                    PreferencesTools.KEY_BEACON_NOTIFICATION_HISTORY,
                    allBeaconDataMap);
        }

        Map<String, BeaconInfo> unreadBeaconDataMap = getUnreadBeaconData(activity);
        if (unreadBeaconDataMap.containsKey(beaconKey)) {
            unreadBeaconDataMap.remove(beaconKey);

            PreferencesTools.getInstance().saveProperty(activity,
                    PreferencesTools.KEY_BEACON_NOTIFICATION_HISTORY_UNREAD,
                    unreadBeaconDataMap);
        }
    }

    public boolean haveNewBeaconData(Activity activity) {
        String unreadMapStr = PreferencesTools.getInstance().getProperty(activity, PreferencesTools.KEY_BEACON_NOTIFICATION_HISTORY_UNREAD);
        return TextUtils.isEmpty(unreadMapStr) || unreadMapStr.equals("{}") ? false : true;
    }

    public boolean shouldPush(Activity activity, String major, String minor) {
        String beaconKey = BeaconInfo.getBeaconKeyWithMajorMinor(major, minor);

        /** <BeaconInfo.getIdStr(), BeaconInfo> */
        Map<String, BeaconInfo> allBeaconDataMap = getAllBeaconData(activity);


        if (allBeaconDataMap.containsKey(beaconKey)) {
            BeaconInfo beaconData = allBeaconDataMap.get(beaconKey);

            boolean shouldPush = beaconData.shouldPushToday();
            if (shouldPush) {
                PreferencesTools.getInstance().saveProperty(activity,
                        PreferencesTools.KEY_BEACON_NOTIFICATION_HISTORY,
                        allBeaconDataMap);
            }

            return shouldPush;
//            return true;
        } else {
            return true;
        }
    }

}
