package com.omni.syntrendsdk.module.google_navigation;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GoogleRoute implements Serializable {

//    {
//        "bounds":{
//        "northeast":{
//            "lat":25.0810055,
//                    "lng":121.5650804
//        },
//        "southwest":{
//            "lat":25.0807009,
//                    "lng":121.5649898
//        }
//    },
//        "copyrights":"Map data ©2017 Google",
//            "legs":[
//        {
//            "distance":{
//            "text":"35 m",
//                    "value":35
//        },
//            "duration":{
//            "text":"1 min",
//                    "value":8
//        },
//            "end_address":"No. 7-17, Jihu Road, Neihu District, Taipei City, Taiwan 114",
//                "end_location":{
//            "lat":25.0810055,
//                    "lng":121.5650804
//        },
//            "start_address":"No. 28, Jihu Road, Neihu District, Taipei City, Taiwan 114",
//                "start_location":{
//            "lat":25.0807009,
//                    "lng":121.5649898
//        },
//            "steps":[
//            {
//                "distance":{
//                "text":"35 m",
//                        "value":35
//            },
//                "duration":{
//                "text":"1 min",
//                        "value":8
//            },
//                "end_location":{
//                "lat":25.0810055,
//                        "lng":121.5650804
//            },
//                "html_instructions":"Head \u003cb\u003enorth\u003c/b\u003e on \u003cb\u003e基湖路\u003c/b\u003e toward \u003cb\u003e瑞光路\u003c/b\u003e\u003cdiv style=\"font-size:0.9em\"\u003eDestination will be on the left\u003c/div\u003e",
//                    "polyline":{
//                "points":"kqaxCed~dV[Ga@I"
//            },
//                "start_location":{
//                "lat":25.0807009,
//                        "lng":121.5649898
//            },
//                "travel_mode":"DRIVING"
//            }
//            ],
//            "traffic_speed_entry":[
//            ],
//            "via_waypoint":[
//            ]
//        }
//        ],
//        "overview_polyline":{
//        "points":"kqaxCed~dV}@Q"
//    },
//        "summary":"基湖路",
//            "warnings":[
//        ],
//        "waypoint_order":[
//        ]
//    }

    @SerializedName("bounds")
    private GoogleBounds bounds;
    @SerializedName("copyrights")
    private String copyRights;
    @SerializedName("legs")
    private GoogleLeg[] legs;
    @SerializedName("overview_polyline")
    private GooglePolyline overviewPolyline;
    @SerializedName("summary")
    private String summary;
    @SerializedName("warnings")
    private Object[] warnings;
    @SerializedName("waypoint_order")
    private Object[] waypointOrders;

    public GoogleBounds getBounds() {
        return bounds;
    }

    public void setBounds(GoogleBounds bounds) {
        this.bounds = bounds;
    }

    public String getCopyRights() {
        return copyRights;
    }

    public void setCopyRights(String copyRights) {
        this.copyRights = copyRights;
    }

    public GoogleLeg[] getLegs() {
        return legs;
    }

    public void setLegs(GoogleLeg[] legs) {
        this.legs = legs;
    }

    public GooglePolyline getOverviewPolyline() {
        return overviewPolyline;
    }

    public void setOverviewPolyline(GooglePolyline overviewPolyline) {
        this.overviewPolyline = overviewPolyline;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Object[] getWarnings() {
        return warnings;
    }

    public void setWarnings(Object[] warnings) {
        this.warnings = warnings;
    }

    public Object[] getWaypointOrders() {
        return waypointOrders;
    }

    public void setWaypointOrders(Object[] waypointOrders) {
        this.waypointOrders = waypointOrders;
    }
}
