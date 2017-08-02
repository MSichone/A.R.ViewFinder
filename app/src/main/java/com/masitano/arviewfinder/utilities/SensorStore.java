package com.masitano.arviewfinder.utilities;

import com.google.android.gms.location.LocationSettingsResult;
import com.masitano.arviewfinder.models.Sensor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Masitano K.P Sichone on 7/6/2017.
 */

public class SensorStore {

    private static SensorStore _selfInstance = null;
    private List<Sensor> sensors;
    private LocationSettingsResult result;

    private int mapRange;

    /**
     * Private constructor to prevent further instantiation
     */
    private SensorStore() {
        sensors = new ArrayList<Sensor>();
        mapRange = 300;
        result = null;
    }
    /**
     * Factory method to get the instance of this class. This method ensures
     * that this class will have one and only one instance at any point of
     * time. This is the only way to get the instance of this class. No other
     * way will be made available to the programmer to instantiate this class.
     *
     * @return the object of this class.
     */
    public static SensorStore getInstance() {
        if (_selfInstance == null) {
            _selfInstance = new SensorStore();
        }
        return _selfInstance;
    }

    public void clearSensors(){
        setSensors(new ArrayList<Sensor>());
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    public void addSensor(Sensor sensor){
        this.sensors.add(sensor);
    }

    public int getMapRange() {
        return mapRange;
    }

    public void setMapRange(int mapRange) {
        this.mapRange = mapRange;
    }

    public LocationSettingsResult getResult() {
        return result;
    }

    public void setResult(LocationSettingsResult result) {
        this.result = result;
    }

    public int getStoreSize(){
        return this.sensors.size();
    }

}
