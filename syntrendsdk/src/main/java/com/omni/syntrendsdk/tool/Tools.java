package com.omni.syntrendsdk.tool;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.omni.syntrendsdk.R;


public class Tools {

    private static Tools mTools;

    public static Tools getInstance() {
        if (mTools == null) {
            mTools = new Tools();
        }
        return mTools;
    }

    public static final float beaconTrigger05 = 0.7f;
    public static final float beaconTrigger2 = 2f;
    public static final float beaconTrigger10 = 10f;

//    private static float beaconTrigger = beaconTrigger10;
    public float getBeaconTrigger() {
        return beaconTrigger10;
    }

    public int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public int dpToIntPx(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
//        int dpAsPixels = (int) (dp * scale + 0.5f);
        int dpAsPixels = (int) (dp * scale);
        return dpAsPixels;
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public void hideKeyboard(Context context, View rootView) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
    }

    public int getColor(Context context, int colorId) {
        return ContextCompat.getColor(context, colorId);
    }

    public float getDistance(double p1Lat, double p1Lon, double p2Lat, double p2Lon) {
        Location l1 = new Location("One");
        l1.setLatitude(p1Lat);
        l1.setLongitude(p1Lon);

        Location l2 = new Location("Two");
        l2.setLatitude(p2Lat);
        l2.setLongitude(p2Lon);

        float distance = l1.distanceTo(l2);

        return distance;
    }

    public String getDistanceStr(double p1Lat, double p1Lon, double p2Lat, double p2Lon) {
        float distance = getDistance(p1Lat, p1Lon, p2Lat, p2Lon);

        String dist = distance + " M";
        if (distance > 1000.0f) {
            distance = distance / 1000000.0f;
            dist = distance + "M";
        }

        return dist;
    }

    public Drawable getDrawable(Context context, int drawableId) {
        return ContextCompat.getDrawable(context, drawableId);
    }

    public int getAndroidVersion() {
        return Build.VERSION.SDK_INT;
    }

    public int getNotificationSmallIcon() {
        if (getAndroidVersion() >= Build.VERSION_CODES.LOLLIPOP) {
            return R.mipmap.nlpi_logo;
        } else {
            return R.mipmap.nlpi_logo;
        }
    }

    public void changeDrawableBGWithColor(Context context, View view, @ColorRes int strokeColorRes, @ColorRes int fillColorRes) {
        GradientDrawable evacuationRouteShape = (GradientDrawable) view.getBackground();
        evacuationRouteShape.setColor(ContextCompat.getColor(context, fillColorRes));
        evacuationRouteShape.setStroke(dpToIntPx(context, 1), getColor(context, strokeColorRes));
    }

}
