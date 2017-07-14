package com.masitano.arviewfinder.models;

/**
 * Created by Masitano on 6/21/2017.
 */

public class Data {
    private Temperature temperature;

    public Data() {
        temperature = new Temperature();
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }
}
