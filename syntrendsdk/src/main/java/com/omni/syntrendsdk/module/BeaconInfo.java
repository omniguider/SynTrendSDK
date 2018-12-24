package com.omni.syntrendsdk.module;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BeaconInfo implements Serializable {

    /**
     * should use CD time from server
     */
    private final int beaconPushCD = 3;

    transient SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH");

    @SerializedName("id")
    private int id;
    @SerializedName("major")
    private String major;
    @SerializedName("minor")
    private String minor;
    @SerializedName("desc")
    private String desc;
    @SerializedName("lat")
    private double lat;
    @SerializedName("lng")
    private double lng;
    @SerializedName("push")
    private BeaconPushContent[] beaconPushContents;

    private boolean isUnread = false;
    private long lastPushTimeInMillis;
    private String lastPushDate;

    public static String getBeaconKeyWithMajorMinor(String major, String minor) {
        return major + "," + minor;
    }

    public int getId() {
        return id;
    }

    public String getMajor() {
        return major;
    }

    public String getMinor() {
        return minor;
    }

    public String getDesc() {
        return desc;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public BeaconPushContent[] getBeaconPushContents() {
        return beaconPushContents;
    }

    public String getBeaconKey() {
        return getBeaconKeyWithMajorMinor(getMajor(), getMinor());
    }

    public boolean isUnread() {
        return isUnread;
    }

    public void setUnread(boolean unread) {
        isUnread = unread;
    }

    public boolean shouldPushToday() {
        Date today = Calendar.getInstance().getTime();
        String currentHourAsString = dateFormat.format(today);

        String previousPushDateStr = getLastPushDate();
        if (TextUtils.isEmpty(previousPushDateStr)) {
            setLastPushDate(currentHourAsString);
            return true;
        } else {
            String[] lastPushDateArr = lastPushDate.split("-");
            String[] todayDateArr = currentHourAsString.split("-");

            if (lastPushDateArr[0].equals(todayDateArr[0])) {
                int lastPushHour = Integer.valueOf(lastPushDateArr[1]);
                int currentHour = Integer.valueOf(todayDateArr[1]);
                if (currentHour - lastPushHour >= beaconPushCD) {
                    setLastPushDate(currentHourAsString);
                    return true;
                } else {
                    return false;
                }

            } else {
                setLastPushDate(currentHourAsString);
                return true;
            }
        }
    }

    public String getLastPushDate() {
        return lastPushDate;
    }

    public void setLastPushDate(String lastPushDate) {
        this.lastPushDate = lastPushDate;
    }

    public long getLastPushTimeInMillis() {
        return lastPushTimeInMillis;
    }

    public void initPushDate() {
        if (!TextUtils.isEmpty(lastPushDate)) {
            return;
        }

        Date now = Calendar.getInstance().getTime();
        lastPushTimeInMillis = now.getTime();

        lastPushDate = dateFormat.format(now);
    }
}
