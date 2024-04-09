package com.example.myapplication;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@Keep
public class NotificationModel implements Serializable {

    @SerializedName("latitude")
    public  String latitude="";

    @SerializedName("longitude")
    public  String longitude="";

    @SerializedName("isCustomLocation")
    public  Boolean isCustomLocation= false;

    @SerializedName("isNewTrip")
    public  Boolean isNewTrip= false;

    @SerializedName("restart")
    public  Boolean restart= false;

    @Override
    public String toString() {
        return "NotificationModel{" +
                "longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", isCustomLocation='" + isCustomLocation + '\'' +
                ", isNewTrip='" + isNewTrip + '\'' +
                ", restart='" + restart + '\'' +
                '}';
    }

    public Boolean getCustomLocation() {
        return isCustomLocation;
    }

    public void setCustomLocation(Boolean customLocation) {
        isCustomLocation = customLocation;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }


}
