package com.masitano.arviewfinder.models;

import java.util.List;

/**
 * Created by Masitano on 6/21/2017.
 *
 * This class stores the GPS Co-ordinates of an Urban Observatory Sensor
 */

public class Geom {

    private String type;
    private List<Double> coordinates = null;

    public Geom() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }
}
