package com.masitano.arviewfinder.utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Masitano K.P Sichone on 6/11/2017.
 * Purpose: To store user preferences for AR ViewFinder application on the device.
 */

public class QuestionnaireManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context appContext;

    // Shared Preference Mode
    int PRIVATE_MODE = 0;

    // Shared Preference Keys
    private static final String PREF_NAME = "ar-viewfinder-questionnaire";
    private static final String IS_FIRST_LAUNCH = "FirstLaunch";

    private static final String GENDER = "Gender";
    private static final String AGE = "Age";
    private static final String GPS_KNOWLEDGE = "Gps_Knowledge";
    private static final String HAS_USED_GOOGLE_MAPS = "UsedGoogleMaps";
    private static final String HAS_USED_FOURSQUARE = "UsedFourSquare";
    private static final String HAS_USED_UBER = "UsedUber";
    private static final String HAS_SNAP_MAP = "UsedSnapMap";

    public QuestionnaireManager(Context context) {
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

    public String getGender(){
        return pref.getString(GENDER, "");
    }

    public void setGender(String gender){
        editor.putString(GENDER, gender);
        editor.commit();
    }

    public String getAge(){
        return pref.getString(AGE, "");
    }

    public void setAge(String gender){
        editor.putString(AGE, gender);
        editor.commit();
    }

    public String getGpsKnowledge(){
        return pref.getString(GPS_KNOWLEDGE, "");
    }

    public void setGpsKnowledge(String gpsKnowledge){
        editor.putString(GPS_KNOWLEDGE, gpsKnowledge);
        editor.commit();
    }

    public void setHasUsedGoogleMaps(boolean answer) {
        editor.putBoolean(HAS_USED_GOOGLE_MAPS, answer);
        editor.commit();
    }

    public boolean hasUsedGoogleMaps() {
        return pref.getBoolean(HAS_USED_GOOGLE_MAPS, false);
    }

    public void setHasUsedFoursquare(boolean answer) {
        editor.putBoolean(HAS_USED_FOURSQUARE, answer);
        editor.commit();
    }

    public boolean hasUsedFourSquare() {
        return pref.getBoolean(HAS_USED_FOURSQUARE, false);
    }

    public void setHasUsedUber(boolean answer) {
        editor.putBoolean(HAS_USED_UBER, answer);
        editor.commit();
    }

    public boolean hasUsedUber() {
        return pref.getBoolean(HAS_USED_UBER, false);
    }

    public void setHasSnapMap(boolean answer) {
        editor.putBoolean(HAS_SNAP_MAP, answer);
        editor.commit();
    }

    public boolean hasUsedSnapMap() {
        return pref.getBoolean(HAS_SNAP_MAP, false);
    }
}
