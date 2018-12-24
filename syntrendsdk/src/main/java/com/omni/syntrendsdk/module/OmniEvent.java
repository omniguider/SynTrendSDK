package com.omni.syntrendsdk.module;

import android.os.Bundle;

public class OmniEvent {

    public static final int TYPE_CLICK_HOME = 19;
    public static final int TYPE_BOOK_DETAIL_INFO_PAGE_DESTROYED = 20;
    public static final int TYPE_REQUEST_LAST_LOCATION = 21;
    public static final int TYPE_USER_OUTDOOR_LOCATION = 22;
    public static final int TYPE_USER_INDOOR_LOCATION = 23;
    public static final int TYPE_FLOOR_PLAN_CHANGED = 24;
    public static final int TYPE_POIS_ADDED = 25;
    public static final int TYPE_NAVIGATION_MODE_CHANGED = 26;
    public static final int TYPE_NOTIFICATION_HISTORY_STATUS_CHANGED = 27;
    public static final int TYPE_REQUEST_CURRENT_FLOOR = 28;
    public static final int TYPE_FIREBASE_TOKEN_CHANGED = 29;
    public static final int TYPE_RECEIVED_FIREBASE_MESSAGE = 30;
    public static final int TYPE_REQUEST_USER_LOCATION_INDOOR_ONLY = 31;
    public static final int TYPE_REQUEST_USER_LOCATION_OUTDOOR_ONLY = 32;
    public static final int TYPE_REQUEST_USER_LOCATION_AUTOMATICALLY = 33;
    public static final int TYPE_USER_LOCATION_REQUEST_TYPE = 34;
    public static final int TYPE_CHECK_USER_LOCATION_REQUEST_TYPE = 35;
    public static final int TYPE_PARKING_SPACE_CHANGED = 36;
    public static final int TYPE_SEND_TRACE_ID = 37;
    public static final int TYPE_SELECT_OTHER_LEVEL_MISSION_ENTER = 38;
    public static final int TYPE_SELECT_OTHER_LEVEL_MISSION_COMPLETE = 39;
    public static final int TYPE_LOGIN_STATUS_CHANGED = 40;
    public static final int TYPE_MISSION_COMPLETE = 41;
    public static final int TYPE_REWARD_COMPLETE = 42;
    public static final int TYPE_CHECKOUT_SCAN = 43;
    public static final int TYPE_CHECKOUT_HOLD = 44;
    public static final int TYPE_CLICK_BORROWED_LIST = 45;
    public static final int TYPE_SELECT_OTHER_LEVEL_MISSION = 46;

    public static final int TYPE_BEACON_LEAVE_NOTICE = 99;

    public static final String EVENT_CONTENT_USER_IN_NAVIGATION = "event_content_user_in_navigation";
    public static final String EVENT_CONTENT_NOT_NAVIGATION = "event_content_not_navigation";

    private int mType;
    private String mContent;
    private Object mObj;
    private Bundle mArgs;

    public OmniEvent(int type, String content) {
        mType = type;
        mContent = content;
    }

    public OmniEvent(int type, Object obj) {
        mType = type;
        mObj = obj;
    }

    public OmniEvent(int type, Bundle args) {
        mType = type;
        mArgs = args;
    }

    public int getType() {
        return mType;
    }

    public String getContent() {
        return mContent;
    }

    public Object getObj() {
        return mObj;
    }

    public Bundle getArguments() {
        return mArgs;
    }
}
