package com.masitano.arviewfinder;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.masitano.arviewfinder.models.Sensor;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Masitano K.P Sichone on 6/19/2017.
 *
 */
public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,OnLocationChangedListener, OnConnectedListener {

    // Debugging TAG for Log
    private static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap;
    private Circle circleMapRange;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private CurrentLocation myCurrentLocation;
    private Location mLastKnownLocation;
    private LatLng mCoordinate;

    // Default Location if No GPS is Newcastle
    private final LatLng mDefaultLocation = new LatLng(54.978188, -1.617513);

    private CameraPosition mCameraPosition;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "CAMERA_POSITION";
    private static final String KEY_LOCATION = "LOCATION";

    // Constants
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int WORLD_ZOOM = 1;
    private static final int CONTINENT_ZOOM = 5;
    private static final int CITY_ZOOM = 10;
    private static final int STREET_ZOOM = 15;

    // Variables
    //private int mapViewRange = 300;
    private int arViewRange = 100;
    private double lastLatitude = 0;
    private double lastLongitude = 0;
    private String locationData = "";

    // Flags
    private boolean mLocationPermissionGranted;
    private boolean sensorsUpdated;

    // Remote Connection
    private Gson gson;
    private static InputStream is = null;
    private static String json = "";
    //private List<Sensor> sensors = new ArrayList<Sensor>();
    private String sensorName, str;

    //scheduler
    private ScheduledThreadPoolExecutor sch = (ScheduledThreadPoolExecutor)
            Executors.newScheduledThreadPool(5);
    private final static DateFormat fmt = DateFormat.getTimeInstance(DateFormat.LONG);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Toolbar Transparent & Home Button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_maps);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_logo_launcher);

        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openHomeMenuDialog(v);
                    }
                }
        );

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        startGPS();
    }

    private void startGPS() {
        myCurrentLocation = new CurrentLocation(this,this,getApplicationContext());
        //myCurrentLocation = new CurrentLocation(this,getApplicationContext());
        myCurrentLocation.buildGoogleApiClient(getApplicationContext());
        myCurrentLocation.start();

    }

    private void stopGPS(){
        myCurrentLocation.stop();
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Adding top padding to accommodate for toolbar
        mMap.setPadding(0, 60, 0, 0);

        // Customising map_style_retro.json the styling of the base map using a JSON object defined
        // in a raw resource file.
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style_retro));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        // Turning on the My Location layer and the related control on the map.
        updateLocationUI();

        // Getting the current location of the device and setting the position of the map.
        getDeviceLocation();

        // Getting the POIs near user
        //new PoiLookup().execute();

        // Setting Schedules Map POI updates
        ScheduledFuture<?> delayFuture = sch.scheduleWithFixedDelay(delayTask, 10, 60, TimeUnit.SECONDS);
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }

    /*

     */
    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Getting the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        if (mLocationPermissionGranted) {
            // getting the last location
            mLastKnownLocation = myCurrentLocation.getLastLocation();
        }

        // Setting the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            // Getting the users location co-ordinates
            mCoordinate = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            // Animating the Cameras movement to position
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(mCoordinate, STREET_ZOOM);
            mMap.animateCamera(yourLocation,5000,null);

            // Looking up POIs
            new PoiLookup().execute();

        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, WORLD_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }


    /**
     * Builds the map when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Building the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Handling suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Play services connection suspended");
    }

    /**
     * Handling the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    /*
    Connecting to External Servers as a background task
     */
    private class PoiLookup extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            //set new sensor data update flag
            sensorsUpdated = false;
            // Getting the users location co-ordinates
            mCoordinate = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            //showDialog(progress_bar_type);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getSensorsInVicinity(mCoordinate);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //dismissDialog(progress_bar_type);
            if (sensorsUpdated){
                setSensorsInVicinity(mCoordinate);
            }

        }
    }

    /**
     * HTTP Request to External Servers for POI Data
     */
    private void getSensorsInVicinity(LatLng latLng) {

        if (mLastKnownLocation != null) {
            //-1.617513,54.978188
            HttpClient httpclient = HttpClients.createDefault();
            try {
                //String coordinatesToSearch = mLastKnownLocation.getLongitude() + "," + mLastKnownLocation.getLatitude() + "," + mapViewRange;
                String coordinatesToSearch = latLng.longitude+ "," + latLng.latitude+ "," + SensorStore.getInstance().getMapRange();

                URIBuilder builder = new URIBuilder("http://uoweb1.ncl.ac.uk/api/v1/sensors/live.json");
                //builder.setParameter("sensor_type", "Environmental");
                builder.setParameter("api_key", "7b1a9ytmmhdcruloy81icy1u2dfk04njuco308y9eaxt9vfygr79aiplatb5c71s9iq30bdlad7swin7qgi7c2nvdt");
                //builder.setParameter("buffer", "-1.617513,54.978188,100");
                builder.setParameter("buffer", coordinatesToSearch);
                builder.setParameter("variable", "Temperature");

                URI uri = builder.build();
                System.out.println("Urban Observatory Request: " + uri.toString());

                HttpGet request = new HttpGet(uri);
                HttpResponse response = httpclient.execute(request);
                HttpEntity entity = response.getEntity();


                if (entity != null) {
                    is = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    json = sb.toString();
                    System.out.println("Urban Observatory Response: " + json);
                    JSONArray jsonArray = new JSONArray(json);
                    gson = new Gson();

                    //replace sensor data
                    if (jsonArray.length() > 0){
                        //set new sensor data update flag
                        sensorsUpdated = true;
                        //clear previous sensor data
                        SensorStore.getInstance().clearSensors();
                        //sensors.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            //System.out.println("jsonObject.toString() = " + jsonObject.toString());
                            //System.out.println("Temperature Reading: " + jsonObject.getJSONObject("data").toString());
                            //System.out.println("Temperature Reading: " + jsonObject.getJSONObject("data").getJSONObject("Temperature").toString());
                            //System.out.println("Temperature Reading: " + jsonObject.getJSONObject("data").getJSONObject("Temperature").getJSONObject("data").toString());
                            Sensor sensor = gson.fromJson(jsonObject.toString(), Sensor.class);
                            //System.out.println("Real Temperature Reading: " + jsonObject.getJSONObject("data").getJSONObject("Temperature").getJSONObject("data").get(sensor.getLatest()));
                            //add temperature reading
                            sensor.getData().getTemperature().setReading(Float.parseFloat(jsonObject.getJSONObject("data").getJSONObject("Temperature").getJSONObject("data").get(sensor.getLatest()).toString()));
                            System.out.println("Real Temp: " + sensor.getData().getTemperature().getReading());;
                            //System.out.println("sensor.getName() = " + sensor.getName());
                            //sensors.add(sensor);
                            SensorStore.getInstance().addSensor(sensor);
                        }
                    }

                }

            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
        Setting the POI into Markers on Map
     */
    private void setSensorsInVicinity(LatLng latLng) {
        // Clearing previous markers
        mMap.clear();

        //draw search range
        circleMapRange = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(SensorStore.getInstance().getMapRange())
                .strokeWidth(3)
                .strokeColor(Color.GREEN));

        circleMapRange = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(arViewRange)
                .strokeWidth(3)
                .strokeColor(Color.BLUE));

        System.out.println("SensorDataExtract from "+ SensorStore.getInstance().getStoreSize() + " sensors");
        //for (Sensor sensor : sensors){
        for (Sensor sensor : SensorStore.getInstance().getSensors()){
            //extract the sensor name
            //System.out.println("SensorDataExtract from "+ sensors.size() + " sensors");

            //System.out.println("SensorDataExtract: " + sensor.getData().getTemperature().getMeta());

            try{
                str = sensor.getName();
                sensorName = str.substring(str.indexOf("(")+1,str.indexOf(")"));
            }catch (Exception e){
                sensorName = sensor.getName();
                //e.printStackTrace();
            }

            LatLng marker = new LatLng(sensor.getGeom().getCoordinates().get(1),sensor.getGeom().getCoordinates().get(0));
            String reading = String.format("Temperature: %.1f c",sensor.getData().getTemperature().getReading());
            mMap.addMarker(new MarkerOptions()
                    .position(marker)
                    .title(sensorName)
                    .snippet(reading)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_urban_obsevatory)));
        }

        //String.format("GPS = (%.3f, %.3f)
        //add newcastle POIs
        LatLng marker = new LatLng(54.978954,-1.613563);
        mMap.addMarker(new MarkerOptions()
                .position(marker)
                .title("King's Gate")
                .snippet("Temperature: 12 C")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ncl)));


    }
    /**
     * Open Message dialog.
     */
    private void openHomeMenuDialog(View view) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MapsActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_message, null);
        final android.app.AlertDialog messageDialog = new android.app.AlertDialog.Builder(this).create();
        messageDialog.setView(mView);
        final TextView txtMessage = (TextView) mView.findViewById(R.id.txt_dialog_message);
        final TextView txtGpsPosition = (TextView) mView.findViewById(R.id.txt_dialog_gps_position);
        //final TextView txtGpsAltitude = (TextView) mView.findViewById(R.id.txt_dialog_gps_altitude);
        final TextView txtGpsAccuracy = (TextView) mView.findViewById(R.id.txt_dialog_gps_accuracy);
        final TextView txtSensorsInRange = (TextView) mView.findViewById(R.id.txt_dialog_sensors_in_range);
        //final ImageButton btnOk = (ImageButton) mView.findViewById(R.id.btn_dialog_message_ok);
        final Button btnArMode = (Button) mView.findViewById(R.id.btnArMode);
        final Button btnSettings = (Button) mView.findViewById(R.id.btnSettings);

        txtMessage.setText("Menu");
        txtGpsPosition.setText("GPS Position: " + mLastKnownLocation.getLatitude()+","+mLastKnownLocation.getLongitude());
        //txtGpsAltitude.setText("GPS Altitude: " + mLastKnownLocation.getAltitude()+"m");
        txtGpsAccuracy.setText("GPS Accuracy: " + mLastKnownLocation.getAccuracy()+"m ("+mLastKnownLocation.getProvider()+")");
        txtSensorsInRange.setText("Sensors in ("+ SensorStore.getInstance().getMapRange() +"m) Range: " + SensorStore.getInstance().getStoreSize());



        btnArMode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Clicked.", Toast.LENGTH_LONG).show();
                //System.out.println("Switch to AR: Maps GPS Stopped");
                //stopGPS();
                startActivity(new Intent(MapsActivity.this, MainActivity.class));

                messageDialog.dismiss();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(new Intent(MapsActivity.this, SettingsActivity.class));

                messageDialog.dismiss();
            }
        });
        //capturing the cancel button
        messageDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                messageDialog.dismiss();
            }
        });
        messageDialog.show();
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // store it off for use when we need it
        mLastKnownLocation = location;

        lastLatitude = location.getLatitude();
        lastLongitude = location.getLongitude();
        locationData = "["+String.valueOf(lastLatitude)+","+String.valueOf(lastLongitude)+"]";
        Log.v(TAG,"location changed = " + location);
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        System.out.println("On Pause: Maps GPS Stopped");
        stopGPS();
    }*/

    /*@Override
    protected void onResume() {
        super.onResume();
        System.out.println("On Resume: Maps GPS Started");
        startGPS();
    }*/

    private Runnable delayTask = new Runnable(){
        @Override
        public void run() {
            try{
                System.out.println("\t delayTask Execution Time: "
                        + fmt.format(new Date()));

                //Thread.sleep(10 * 1000);
                new PoiLookup().execute();

                /*System.out.println("\t delayTask End Time: "
                        + fmt.format(new Date()));*/
            }catch(Exception e){

            }
        }
    };
}
