package com.omni.syntrendsdk.module;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Building implements Serializable {

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("desc")
    private String desc;
    @SerializedName("locid")
    private String locId;
    @SerializedName("enabled")
    private String enabled;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getLocId() {
        return locId;
    }

    public String getEnabled() {
        return enabled;
    }
}
