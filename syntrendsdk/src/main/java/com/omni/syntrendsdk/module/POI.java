package com.omni.syntrendsdk.module;

import com.google.gson.annotations.SerializedName;
import com.omni.syntrendsdk.R;

import java.io.Serializable;

public class POI implements Serializable {

    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_EMERGENCY_EXIT = 1;
    public static final int TYPE_ELEVATOR = 2;
    public static final int TYPE_STAIRS = 3;
    public static final int TYPE_EVENT_SPACE = 4;
    public static final int TYPE_CLAPPER_THEATER = 5;
    public static final int TYPE_CLAPPER_STUDIO = 6;
    public static final int TYPE_FOOD_COURT = 7;
    public static final int TYPE_ACCESSIBILITY = 8;
    public static final int TYPE_AED = 9;
    public static final int TYPE_PACKING_AREA = 10;
    public static final int TYPE_ATM = 11;
    public static final int TYPE_CAFE = 12;
    public static final int TYPE_GARDEN = 13;
    public static final int TYPE_DIAPER_CHANGING = 14;
    public static final int TYPE_BREAST_FEEDING_ROOM = 15;
    public static final int TYPE_CASHIER = 16;
    public static final int TYPE_FAMILY_RESTROOM = 17;
    public static final int TYPE_ELEVATOR_FOR_DISABLED = 18;
    public static final int TYPE_VENDING_MACHINE = 19;
    public static final int TYPE_PUBLIC_PHONE = 20;
    public static final int TYPE_TRASH_RECYCLE = 21;
    public static final int TYPE_FIRE_HYDRANT = 22;
    public static final int TYPE_ENTRANCE = 23;
    public static final int TYPE_EXTINGUISHER = 24;
    public static final int TYPE_RESTAUrANT = 25;
    public static final int TYPE_MAP_TEXT = 26;
    public static final int TYPE_STORE = 27;
    public static final int TYPE_ESCALATOR_UP = 28;
    public static final int TYPE_ESCALATOR_DOWN = 29;
    public static final int TYPE_RESTROOM = 30;
    public static final int TYPE_SERVICE_CENTER = 31;
    public static final int TYPE_INFORMATION = 32;
    public static final int TYPE_LOCKERS = 33;

    public static final String TYPE_STR_GUIDE_EXHIBIT = "poi_type_guide_exhibit";

    @SerializedName("id")
    private int id;
    @SerializedName("store_id")
    private int store_id;
    @SerializedName("name")
    private String name;
    @SerializedName("desc")
    private String desc;
    @SerializedName("logo")
    private String logo;
    @SerializedName("type")
    private String type;
    @SerializedName("type_zh")
    private String typeZh;
    @SerializedName("color")
    private String color;
    @SerializedName("min_level")
    private int min_level;
    @SerializedName("max_level")
    private int max_level;
    @SerializedName("lat")
    private double latitude;
    @SerializedName("lng")
    private double longitude;
    @SerializedName("is_entrance")
    private String isEntrance;
    @SerializedName("is_door")
    private String isDoor;
    @SerializedName("icon")
    private String icon;

    private int poiType = -1;
    private int poiIconResId = -1;

    /**
     * Only for GuideExhibit method toPOI()
     */
    private int guideOrder;
    private String floorNumber;
    private String exhibitContent;
    private String exhibitImageURL;

    public int getId() {
        return id;
    }

    public int getStore_id() {
        return store_id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getLogo() {
        return logo;
    }

    public String getType() {
        return type;
    }

    public String getTypeZh() {
        return typeZh;
    }

    public String getColor() {
        return color;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getIsEntrance() {
        return isEntrance;
    }

    public String getIsDoor() {
        return isDoor;
    }

    public int getMin_level() {
        return min_level;
    }

    public int getMax_level() {
        return max_level;
    }

    public String getIcon() {
        return icon;
    }

    private void setId(int id) {
        this.id = id;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setDesc(String desc) {
        this.desc = desc;
    }

    private void setType(String type) {
        this.type = type;
    }

    private void setTypeZh(String typeZh) {
        this.typeZh = typeZh;
    }

    private void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    private void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private void setIsEntrance(String isEntrance) {
        this.isEntrance = isEntrance;
    }

    private void setIsDoor(String isDoor) {
        this.isDoor = isDoor;
    }

    public int getGuideOrder() {
        return guideOrder;
    }

    private void setGuideOrder(int guideOrder) {
        this.guideOrder = guideOrder;
    }

    public String getFloorNumber() {
        return floorNumber;
    }

    private void setFloorNumber(String floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getExhibitContent() {
        return exhibitContent;
    }

    private void setExhibitContent(String exhibitContent) {
        this.exhibitContent = exhibitContent;
    }

    public String getExhibitImageURL() {
        return exhibitImageURL;
    }

    private void setExhibitImageURL(String exhibitImageURL) {
        this.exhibitImageURL = exhibitImageURL;
    }

    public int getPOIType() {
        if (type != null) {
            switch (type) {
                case "Elevator":
                    poiType = TYPE_ELEVATOR;
                    poiIconResId = R.mipmap.syn_poi_elevator;
                    break;

                case "STAIRS":
                    poiType = TYPE_STAIRS;
                    poiIconResId = R.mipmap.syn_poi_stairs;
                    break;

                case "store":
                    poiType = TYPE_STORE;
                    poiIconResId = R.mipmap.syn_poi_store;
                    break;

                case "Event space":
                    poiType = TYPE_EVENT_SPACE;
                    poiIconResId = R.mipmap.syn_poi_event_speace;
                    break;

                case "Clapper Theater":
                    poiType = TYPE_CLAPPER_THEATER;
                    poiIconResId = R.mipmap.syn_poi_clapper_theater;
                    break;

                case "Clapper studio":
                    poiType = TYPE_CLAPPER_STUDIO;
                    poiIconResId = R.mipmap.syn_poi_clapper_studio;
                    break;

                case "Food court":
                    poiType = TYPE_FOOD_COURT;
                    poiIconResId = R.mipmap.syn_poi_food_court;
                    break;

                case "Accessibility":
                    poiType = TYPE_ACCESSIBILITY;
                    poiIconResId = R.mipmap.syn_poi_accesibility;
                    break;

                case "AED":
                    poiType = TYPE_AED;
                    poiIconResId = R.mipmap.syn_poi_aed;
                    break;

                case "packing area":
                    poiType = TYPE_PACKING_AREA;
                    poiIconResId = R.mipmap.syn_poi_packingarea;
                    break;

                case "ATM":
                    poiType = TYPE_ATM;
                    poiIconResId = R.mipmap.syn_poi_atm;
                    break;

                case "cafe":
                    poiType = TYPE_CAFE;
                    poiIconResId = R.mipmap.syn_poi_cafe;
                    break;

                case "GARDEN":
                    poiType = TYPE_GARDEN;
                    poiIconResId = R.mipmap.syn_poi_garden;
                    break;

                case "CASHIER":
                    poiType = TYPE_CASHIER;
                    poiIconResId = R.mipmap.syn_poi_cashier;
                    break;

                case "FAMILY RESTROOM":
                    poiType = TYPE_FAMILY_RESTROOM;
                    poiIconResId = R.mipmap.syn_poi_family_restroom;
                    break;

                case "ELEVATOR FOR DISABLED":
                    poiType = TYPE_ELEVATOR_FOR_DISABLED;
                    poiIconResId = R.mipmap.syn_poi_elevator_for_disabled;
                    break;

                case "Diaper Changing":
                    poiType = TYPE_DIAPER_CHANGING;
                    poiIconResId = R.mipmap.syn_poi_diaper_changing;
                    break;

                case "vending machine":
                    poiType = TYPE_VENDING_MACHINE;
                    poiIconResId = R.mipmap.syn_poi_vending_machine;
                    break;

                case "PUBLIC PHONE":
                    poiType = TYPE_PUBLIC_PHONE;
                    poiIconResId = R.mipmap.syn_poi_public_phone;
                    break;

                case "TRASH Recycle":
                    poiType = TYPE_TRASH_RECYCLE;
                    poiIconResId = R.mipmap.syn_poi_trash_and_recycle;
                    break;

                case "Fire Hydrant":
                    poiType = TYPE_FIRE_HYDRANT;
                    poiIconResId = R.mipmap.syn_poi_hydranta_and_alarm;
                    break;

                case "emergency_exit":
                    poiType = TYPE_EMERGENCY_EXIT;
                    poiIconResId = R.mipmap.syn_poi_emergency_exit;
                    break;

                case "Entrance":
                    poiType = TYPE_ENTRANCE;
                    poiIconResId = R.mipmap.syn_poi_entrance;
                    break;

                case "Fire Extinguisher":
                    poiType = TYPE_EXTINGUISHER;
                    poiIconResId = R.mipmap.syn_poi_fire_extinguisher;
                    break;

                case "escalator_up":
                    poiType = TYPE_ESCALATOR_UP;
                    poiIconResId = R.mipmap.syn_poi_escalator_up;
                    break;

                case "escalator_down":
                    poiType = TYPE_ESCALATOR_DOWN;
                    poiIconResId = R.mipmap.syn_poi_escalatord_down;
                    break;

                case "Restroom":
                    poiType = TYPE_RESTROOM;
                    poiIconResId = R.mipmap.syn_poi_restroom;
                    break;

                case "SERVICE CENTER":
                    poiType = TYPE_SERVICE_CENTER;
                    poiIconResId = R.mipmap.syn_poi_service_center;
                    break;

                case "Information":
                    poiType = TYPE_INFORMATION;
                    poiIconResId = R.mipmap.syn_poi_information;
                    break;

                case "Lockers":
                    poiType = TYPE_LOCKERS;
                    poiIconResId = R.mipmap.syn_poi_lockers;
                    break;

                case "Restaurant":
                case "restaurant":
                    poiType = TYPE_RESTAUrANT;
                    poiIconResId = R.mipmap.syn_poi_restaurant;
                    break;

                case "Breastfeeding room":
                    poiType = TYPE_BREAST_FEEDING_ROOM;
                    poiIconResId = R.mipmap.syn_poi_babyfeeding_room;
                    break;

                case "map_text":
                    poiType = TYPE_MAP_TEXT;
                    break;

                default:
                    poiType = TYPE_UNKNOWN;
                    poiIconResId = R.mipmap.syn_poi_information;
                    break;
            }
        }

        return poiType;
    }

    public int getPOIIconRes(boolean isSelected) {
        getPOIType();
        return isSelected ? R.mipmap.icon_terminal_point : poiIconResId;
    }

    public String getMapTextImageUrl() {
        return "http://nlpi.omniguider.com/upload/map_text/" + getId() + ".png";
    }

    public String getUrlToPoisImage() {
        return "";
    }

    public static class Builder {

        private POI mPOI;

        public Builder() {
            mPOI = new POI();
        }

        public Builder setId(int id) {
            mPOI.setId(id);
            return this;
        }

        public Builder setName(String name) {
            mPOI.setName(name);
            return this;
        }

        public Builder setDesc(String desc) {
            mPOI.setDesc(desc);
            return this;
        }

        public Builder setLat(double lat) {
            mPOI.setLatitude(lat);
            return this;
        }

        public Builder setLng(double lng) {
            mPOI.setLongitude(lng);
            return this;
        }

        public Builder setType(String type) {
            mPOI.setType(type);
            return this;
        }

        public Builder setIsEntrance(boolean isEntrance) {
            mPOI.setIsEntrance(isEntrance ? "Y" : "N");
            return this;
        }

        public Builder setIsDoor(boolean isDoor) {
            mPOI.setIsDoor(isDoor ? "Y" : "N");
            return this;
        }

        public Builder setExhibitContent(String content) {
            mPOI.setExhibitContent(content);
            return this;
        }

        public Builder setExhibitImageURL(String imageURL) {
            mPOI.setExhibitImageURL(imageURL);
            return this;
        }

        /**
         * Only for GuideExhibit method toPOI()
         */
        public Builder setGuideOrder(int guideOrder) {
            mPOI.setGuideOrder(guideOrder);
            return this;
        }

        public Builder setFloorNumber(String floorNumber) {
            mPOI.setFloorNumber(floorNumber);
            return this;
        }

        public POI build() {
            return mPOI;
        }
    }
}
