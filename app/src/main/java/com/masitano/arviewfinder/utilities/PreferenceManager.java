package com.masitano.arviewfinder.utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Masitano K.P Sichone on 6/11/2017.
 * Purpose: To store user preferences for AR ViewFinder application on the device.
 */

public class PreferenceManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context appContext;

    // Shared Preference Mode
    int PRIVATE_MODE = 0;

    // Shared Preference Keys
    private static final String PREF_NAME = "ar-viewfinder";
    private static final String IS_FIRST_LAUNCH = "FirstLaunch";
    private static final String IS_TEMPERATURE_STATUS = "TemperatureStatus";
    private static final String IS_HUMIDITY_STATUS = "HumidityStatus";
    private static final String IS_PRESSURE_STATUS = "PressureStatus";
    private static final String IS_SOUND_STATUS = "SoundStatus";
    private static final String IS_GPS_STATUS = "GpStatus";
    private static final String IS_ATTRACTION_STATUS = "AttractionStatus";
    private static final String IS_UNIVERSITY_STATUS = "UniversityStatus";
    private static final String IS_FOOD_STATUS = "FoodStatus";
    private static final String IS_SETTINGS_CHANGED = "SettingsChangedStatus";
    private static final String AR_RANGE = "AugmentedRealityRange";

    public PreferenceManager(Context context) {
        this.appContext = context;
        pref = appContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstLaunch(boolean isFirstLaunch) {
        editor.putBoolean(IS_FIRST_LAUNCH, isFirstLaunch);
        editor.commit();
    }

    public boolean isFirstLaunch() {
        return pref.getBoolean(IS_FIRST_LAUNCH, true);
    }

    public void setTemperatureStatus(boolean active){
        editor.putBoolean(IS_TEMPERATURE_STATUS, active);
        editor.commit();
    }

    public boolean isTemperatureStatus() {
        return pref.getBoolean(IS_TEMPERATURE_STATUS, true);
    }

    public void setHumidityStatus(boolean active){
        editor.putBoolean(IS_HUMIDITY_STATUS, active);
        editor.commit();
    }

    public boolean isHumidityStatus() {
        return pref.getBoolean(IS_HUMIDITY_STATUS, true);
    }

    public void setPressureStatus(boolean active){
        editor.putBoolean(IS_PRESSURE_STATUS, active);
        editor.commit();
    }

    public boolean isPressureStatus() {
        return pref.getBoolean(IS_PRESSURE_STATUS, true);
    }

    public void setSoundStatus(boolean active){
        editor.putBoolean(IS_SOUND_STATUS, active);
        editor.commit();
    }

    public boolean isSoundStatus() {
        return pref.getBoolean(IS_SOUND_STATUS, true);
    }

    public void setAttractionStatus(boolean active){
        editor.putBoolean(IS_ATTRACTION_STATUS, active);
        editor.commit();
    }

    public boolean isAttractionStatus() {
        return pref.getBoolean(IS_ATTRACTION_STATUS, true);
    }

    public void setUniversityStatus(boolean active){
        editor.putBoolean(IS_UNIVERSITY_STATUS, active);
        editor.commit();
    }

    public boolean isUniversityStatus() {
        return pref.getBoolean(IS_UNIVERSITY_STATUS, true);
    }

    public void setFoodStatus(boolean active){
        editor.putBoolean(IS_FOOD_STATUS, active);
        editor.commit();
    }

    public boolean isFoodStatus() {
        return pref.getBoolean(IS_FOOD_STATUS, true);
    }

    public void setSettingsChangedStatus(boolean active){
        editor.putBoolean(IS_SETTINGS_CHANGED, active);
        editor.commit();
    }

    public boolean isSettingsChangedStatus() {
        return pref.getBoolean(IS_SETTINGS_CHANGED, false);
    }


    public void setGpsStatus(boolean active){
        editor.putBoolean(IS_GPS_STATUS, active);
        editor.commit();
    }

    public boolean isGpsStatus() {
        return pref.getBoolean(IS_GPS_STATUS, false);
    }


    public int getArRange(){
        return pref.getInt(AR_RANGE, 20);
    }

    public void setArRange(int range){
        editor.putInt(AR_RANGE, range);
        editor.commit();
    }
    /*
    ss

    public static void saveLocationId(Context context, String locationId) {
        SharedPreferences info = context.getSharedPreferences(PREF_USER_INFO,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = info.edit();
        editor.putString(PREF_KEYS.LOCATION_ID, locationId);
        editor.commit();
    }


    public static String getLocationId(Context context) {
        SharedPreferences info = context.getSharedPreferences(PREF_USER_INFO,
                Context.MODE_PRIVATE);
        return info.getString(PREF_KEYS.LOCATION_ID, "");
    }
    ss

     */
}
