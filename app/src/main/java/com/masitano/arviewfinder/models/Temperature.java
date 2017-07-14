package com.masitano.arviewfinder.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Masitano on 7/6/2017.
 */

public class Temperature {

    //private Data data;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private Map<String, Object> data = new HashMap<String, Object>();
    private Meta meta;

    private float reading = 0;

    public Temperature() {
        reading = 0;
    }

    public float getReading() {
        return reading;
    }

    public void setReading(float reading) {
        this.reading = reading;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
}
