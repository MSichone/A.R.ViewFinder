package com.masitano.arviewfinder.models;

import java.util.List;

/**
 * Created by Masitano on 6/21/2017.
 *
 * This class is a data store for an Urban Observatory Sensor
 */

public class Sensor {

    private String name;
    private String sensorHeight;
    private String latest;
    private Source source;
    private Geom geom;
    private Data data;
    private String active;
    private String type;
    private String baseHeight;

    public Sensor() {
        data = new Data();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSensorHeight() {
        return sensorHeight;
    }

    public void setSensorHeight(String sensorHeight) {
        this.sensorHeight = sensorHeight;
    }

    public String getLatest() {
        return latest;
    }

    public void setLatest(String latest) {
        this.latest = latest;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Geom getGeom() {
        return geom;
    }

    public void setGeom(Geom geom) {
        this.geom = geom;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBaseHeight() {
        return baseHeight;
    }

    public void setBaseHeight(String baseHeight) {
        this.baseHeight = baseHeight;
    }

    // method to return the gps co-ordinates of the Sensor
    public List<Double> getCoordinates(){
        List<Double> coordinates;
        coordinates= geom.getCoordinates();
        return coordinates;
    }
}
