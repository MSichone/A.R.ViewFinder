package com.masitano.arviewfinder.models;

import android.hardware.*;
import android.location.Location;

/**
 * Created by Masitano on 6/21/2017.
 */

public class POI extends Location {

    public Sensor sensor;

    public POI(String provider) {
        super(provider);
        sensor = new Sensor();
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }
}
