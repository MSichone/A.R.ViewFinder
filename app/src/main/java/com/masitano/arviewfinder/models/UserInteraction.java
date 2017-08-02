package com.masitano.arviewfinder.models;

import com.google.firebase.database.Exclude;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Masitano on 8/1/2017.
 */

public class UserInteraction {
    private String userId;
    private String time;
    private String action;
    private double latitude;
    private double longitude;
    private double accuracy;
    private String poiName;

    public UserInteraction() {
        time = new Timestamp(System.currentTimeMillis()).toString();
        poiName = "";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public String getPoiName() {
        return poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId",userId);
        result.put("time",time);
        result.put("action",action);
        result.put("latitude",latitude);
        result.put("longitude",longitude);
        result.put("accuracy",accuracy);
        result.put("poiName",poiName);
        return result;
    }


}
