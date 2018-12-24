package com.omni.syntrendsdk.module;

public enum UserLocationRequestType {

    AUTOMATICALLY(0),
    INDOOR_ONLY(1),
    OUTDOOR_ONLY(2);

    private int mType;

    UserLocationRequestType(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }

}
