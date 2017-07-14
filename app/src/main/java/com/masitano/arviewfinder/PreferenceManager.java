package com.masitano.arviewfinder;

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
}
