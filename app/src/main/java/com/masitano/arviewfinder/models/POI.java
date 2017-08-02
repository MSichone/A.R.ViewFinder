package com.masitano.arviewfinder.models;

import android.hardware.*;
import android.location.Location;

/**
 * Created by Masitano on 6/21/2017.
 */

public class POI extends Location {

    public int placeId;
    public String placeName;
    public String openingHours;
    public String address;
    public String placeType;
    public String placeDescription;
    public String placeWebsite;
    public String placeSource;
    public String phoneNumber;
    public Sensor sensor;
    public boolean proximityAlert;

    public POI(String provider) {
        super(provider);
        sensor = new Sensor();
        placeId = 0;
        placeName = "";
        openingHours = "";
        placeType = "";
        placeDescription = "";
        placeWebsite = "";
        phoneNumber ="";
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPlaceSource() {
        return placeSource;
    }

    public void setPlaceSource(String placeSource) {
        this.placeSource = placeSource;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

    public String getPlaceDescription() {
        return placeDescription;
    }

    public void setPlaceDescription(String placeDescription) {
        this.placeDescription = placeDescription;
    }

    public String getPlaceWebsite() {
        return placeWebsite;
    }

    public void setPlaceWebsite(String placeWebsite) {
        this.placeWebsite = placeWebsite;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public boolean isProximityAlert() {
        return proximityAlert;
    }

    public void setProximityAlert(boolean proximityAlert) {
        this.proximityAlert = proximityAlert;
    }
}
