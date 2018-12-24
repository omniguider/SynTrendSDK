package com.omni.syntrendsdk.module.google_navigation;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GoogleDistance implements Serializable {

    @SerializedName("text")
    private String text;
    @SerializedName("value")
    private int value;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
