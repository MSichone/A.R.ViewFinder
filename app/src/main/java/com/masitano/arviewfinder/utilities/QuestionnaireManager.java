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
    private static final String AR_KNOWLEDGE = "Ar_Knowledge";
    private static final String PRIVACY = "Privacy";
    private static final String USABILITY = "Usability";
    private static final String HAS_USED_GOOGLE_MAPS = "UsedGoogleMaps";
    private static final String HAS_USED_FOURSQUARE = "UsedFourSquare";
    private static final String HAS_USED_UBER = "UsedUber";
    private static final String HAS_USED_SNAP_MAP = "UsedSnapMap";
    private static final String HAS_USED_POKEMON = "UsedPokemon";
    private static final String HAS_USED_YELP = "UsedYelp";
    private static final String HAS_USED_WALLA_ME = "UsedWallaMe";
    private static final String HAS_USED_SNAP_FILTERS = "UsedSnapFilters";
    private static final String HAS_SELECTED_TRACKING = "SelectedTracking";
    private static final String HAS_SELECTED_TOURISM = "SelectedTourism";
    private static final String HAS_SELECTED_NAVIGATION = "SelectedNavigation";
    private static final String HAS_SELECTED_ADVERTISING = "SelectedAdvertising";
    private static final String HAS_SELECTED_SOCIAL = "SelectedSocial";

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

    public String getArKnowledge(){
        return pref.getString(AR_KNOWLEDGE, "");
    }

    public void setArKnowledge(String arKnowledge){
        editor.putString(AR_KNOWLEDGE, arKnowledge);
        editor.commit();
    }

    public String getPrivacy(){
        return pref.getString(PRIVACY, "");
    }

    public void setPrivacy(String privacy){
        editor.putString(PRIVACY, privacy);
        editor.commit();
    }

    public String getUsability(){
        return pref.getString(USABILITY, "");
    }

    public void setUsability(String usability){
        editor.putString(USABILITY, usability);
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
        editor.putBoolean(HAS_USED_SNAP_MAP, answer);
        editor.commit();
    }

    public boolean hasUsedSnapMap() {
        return pref.getBoolean(HAS_USED_SNAP_MAP, false);
    }

    public void setHasUsedPokemon(boolean answer) {
        editor.putBoolean(HAS_USED_POKEMON, answer);
        editor.commit();
    }

    public boolean hasUsedPokemon() {
        return pref.getBoolean(HAS_USED_POKEMON, false);
    }

    public void setHasUsedYelp(boolean answer) {
        editor.putBoolean(HAS_USED_YELP, answer);
        editor.commit();
    }

    public boolean hasUsedYelp() {
        return pref.getBoolean(HAS_USED_YELP, false);
    }

    public void setHasUsedWallaMe(boolean answer) {
        editor.putBoolean(HAS_USED_WALLA_ME, answer);
        editor.commit();
    }

    public boolean hasUsedWallaMe() {
        return pref.getBoolean(HAS_USED_WALLA_ME, false);
    }

    public void setHasUsedSnapFilters(boolean answer) {
        editor.putBoolean(HAS_USED_SNAP_FILTERS, answer);
        editor.commit();
    }

    public boolean hasUsedSnapFilters() {
        return pref.getBoolean(HAS_USED_SNAP_FILTERS, false);
    }

    public void setHasSelectedAdvertising(boolean answer) {
        editor.putBoolean(HAS_SELECTED_ADVERTISING, answer);
        editor.commit();
    }

    public boolean hasSelectedAdvertising() {
        return pref.getBoolean(HAS_SELECTED_ADVERTISING, false);
    }

    public void setHasSelectedNavigation(boolean answer) {
        editor.putBoolean(HAS_SELECTED_NAVIGATION, answer);
        editor.commit();
    }

    public boolean hasSelectedNavigation() {
        return pref.getBoolean(HAS_SELECTED_NAVIGATION, false);
    }

    public void setHasSelectedTourism(boolean answer) {
        editor.putBoolean(HAS_SELECTED_TOURISM, answer);
        editor.commit();
    }

    public boolean hasSelectedTourism() {
        return pref.getBoolean(HAS_SELECTED_TOURISM, false);
    }

    public void setHasSelectedTracking(boolean answer) {
        editor.putBoolean(HAS_SELECTED_TRACKING, answer);
        editor.commit();
    }

    public boolean hasSelectedTracking() {
        return pref.getBoolean(HAS_SELECTED_TRACKING, false);
    }

    public void setHasSelectedSocial(boolean answer) {
        editor.putBoolean(HAS_SELECTED_SOCIAL, answer);
        editor.commit();
    }

    public boolean hasSelectedSocial() {
        return pref.getBoolean(HAS_SELECTED_SOCIAL, false);
    }
}
