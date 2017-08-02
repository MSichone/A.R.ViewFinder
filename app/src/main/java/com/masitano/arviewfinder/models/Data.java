package com.masitano.arviewfinder.models;

/**
 * Created by Masitano on 6/21/2017.
 */

public class Data {
    private Temperature temperature;
    private Humidity humidity;
    private Sound sound;
    private Pressure pressure;

    public Data() {
        temperature = new Temperature();
        humidity = new Humidity();
        sound = new Sound();
        pressure = new Pressure();
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }

    public Pressure getPressure() {
        return pressure;
    }

    public void setPressure(Pressure pressure) {
        this.pressure = pressure;
    }

    public Humidity getHumidity() {
        return humidity;
    }

    public void setHumidity(Humidity humidity) {
        this.humidity = humidity;
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }
}
