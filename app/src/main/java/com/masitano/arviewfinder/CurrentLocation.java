package com.masitano.arviewfinder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

/**
 * Created by Masitano on 6/19/2017.
 *
 * This class manages mobile device's location sensors through a higher-level Google Play Services API
 *
 * Loosely based on the works of Codepath on Retrieving Location with LocationServices API
 * Accessed at https://guides.codepath.com/android/Retrieving-Location-with-LocationServices-API
 * Accessed on 21/06/2017
 *
 * Loosely based on the works of Krzysztof Jackowski on Augmented Reality in Mobile Devices
 * Accessed at https://www.netguru.co/blog/augmented-reality-mobile-android
 * Accessed on 20/06/2017.
 *
 */
public class CurrentLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private OnLocationChangedListener onLocationChangedListener;
    private OnConnectedListener onConnectedListener;


    private Context context;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    public CurrentLocation(OnLocationChangedListener onLocationChangedListener, Context context) {
        this.onLocationChangedListener = onLocationChangedListener;
        this.context = context;
    }

    public CurrentLocation(OnLocationChangedListener onLocationChangedListener, OnConnectedListener onConnectedListener, Context context) {
        this.onLocationChangedListener = onLocationChangedListener;
        this.context = context;
        this.onConnectedListener = onConnectedListener;
    }

    public synchronized void buildGoogleApiClient(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)//PRIORITY_BALANCED_POWER_ACCURACY
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
    }

    public void start() {
        mGoogleApiClient.connect();
    }

    public void stop() {
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            // letting the mapsActivity now we have a location
            onLocationChangedListener.onLocationChanged(mLastLocation);
        }

        // letting the mapsActivity now we have a connection
        onConnectedListener.onConnected(bundle);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("MyApp", "Location services connection failed with code " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            onLocationChangedListener.onLocationChanged(mLastLocation);
        }
    }

    public Location getLastLocation() {
        return mLastLocation;
    }

}
