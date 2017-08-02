package com.masitano.arviewfinder;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsStates;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.kishan.askpermission.AskPermission;
import com.kishan.askpermission.PermissionCallback;
import com.masitano.arviewfinder.models.POI;
import com.masitano.arviewfinder.models.Sensor;
import com.masitano.arviewfinder.models.UserInteraction;
import com.masitano.arviewfinder.utilities.PlaceStore;
import com.masitano.arviewfinder.utilities.PreferenceManager;
import com.masitano.arviewfinder.utilities.SensorStore;


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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Masitano K.P Sichone on 6/19/2017.
 *
 */
public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, OnLocationChangedListener, OnConnectedListener,
        PermissionCallback, GoogleMap.OnInfoWindowClickListener {

    // Debugging TAG for Log
    private static final String TAG = MapsActivity.class.getSimpleName();

    private Toolbar toolbar;
    private GoogleMap mMap;
    private Circle circleMapRange;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private CurrentLocation myCurrentLocation;
    private Location mLastKnownLocation;
    //private Marker mLastMarker;
    private String mLastPoiInteractionName = "N/A";
    private LatLng mCoordinate;

    // Default Location if No GPS is Newcastle
    private final LatLng mDefaultLocation = new LatLng(54.978188, -1.617513);

    private CameraPosition mCameraPosition;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "CAMERA_POSITION";
    private static final String KEY_LOCATION = "LOCATION";

    // Constants
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_PERMISSIONS = 20;
    private static final int WORLD_ZOOM = 1;
    private static final int CONTINENT_ZOOM = 5;
    private static final int CITY_ZOOM = 10;
    private static final int STREET_ZOOM = 15;

    // Variables
    private PreferenceManager prefManager;
    private int arViewRange = 100;
    private double lastLatitude = 0;
    private double lastLongitude = 0;
    private String locationData = "";

    // Flags
    private boolean logInUploaded = false;
    private boolean mLocationPermissionGranted;
    private boolean sensorsUpdated, sensorResponseFailed;

    // Remote Connection
    private Gson gson;
    private static InputStream is = null;
    private static String json = "";
    //private List<Sensor> sensors = new ArrayList<Sensor>();
    private String sensorName, str;

    //HashMap of Icons
    private Map<String, Integer> sourceIconMap = new HashMap<String, Integer>();
    private Map<String, Integer> placePictures = new HashMap<String, Integer>();

    //Firebase
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser user;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference dbUserInteractions;

    //scheduler
    private ScheduledThreadPoolExecutor sch = (ScheduledThreadPoolExecutor)
            Executors.newScheduledThreadPool(5);
    ScheduledFuture<?> externalPollSchedule;
    private final static DateFormat fmt = DateFormat.getTimeInstance(DateFormat.LONG);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // FIrebase Cloud Services
        checkFirebaseConnection();

        // Application Preference Store
        prefManager = new PreferenceManager(this);

        //HashMap of Icons
        sourceIconMap.put("Sensor", R.drawable.ic_urban_observatory);
        sourceIconMap.put("University", R.drawable.ic_ncl);
        sourceIconMap.put("Attraction", R.drawable.ic_poi);
        sourceIconMap.put("Food", R.drawable.ic_food);

        //HaspMap of Place Photos
        placePictures.put("1", R.drawable.poi_greys);
        placePictures.put("2", R.drawable.poi_kings_gate);
        placePictures.put("3", R.drawable.poi_cottage_chicken);
        placePictures.put("4", R.drawable.ic_poi_stjames);
        placePictures.put("5", R.drawable.poi_nubs);
        placePictures.put("6", R.drawable.poi_subway);

        // Toolbar Transparent & Home Button
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_maps);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_logo_launcher);
        // Toolbar listener
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openHomeMenuDialog();
                    }
                }
        );

        // Check if we have permission to use camera and GPS sensor
        int permissionCheckCamera = ContextCompat.checkSelfPermission(MapsActivity.this,
                android.Manifest.permission.CAMERA);
        int permissionCheckFineLocation = ContextCompat.checkSelfPermission(MapsActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheckCourseLocation = ContextCompat.checkSelfPermission(MapsActivity.this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCallPhone = ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.CALL_PHONE);

        if (permissionCheckCamera == -1 || permissionCheckFineLocation == -1 || permissionCheckCourseLocation == -1 || permissionCallPhone == -1) {
            new AskPermission.Builder(this)
                    .setPermissions(android.Manifest.permission.CAMERA, android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE)
                    .setCallback(this)
                    .request(REQUEST_PERMISSIONS);

        }

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Start GPS
        startGPS();

    }

    /**
     * Starts the Google Maps API Client
     */
    private void startGPS() {
        myCurrentLocation = new CurrentLocation(this, this, getApplicationContext());
        myCurrentLocation.buildGoogleApiClient(getApplicationContext());
        myCurrentLocation.start();
    }

    /**
     * Firebase Cloud Connection
     */
    private void checkFirebaseConnection() {
        //get firebase authorization instance
        auth = FirebaseAuth.getInstance();

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        //verify if user is still active
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MapsActivity.this, SignUpActivity.class));
                    finish();
                }
            }
        };

        //load the user interaction database
        mFirebaseInstance = FirebaseDatabase.getInstance();
        dbUserInteractions = mFirebaseInstance.getReference("userInteractions");

        //upload the user logging in
        if (!logInUploaded) {
            uploadUserInteraction("Login");
            logInUploaded = true;
        }
    }

    /**
     * Manipulate the map once available.
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

        // Setup custom info windows
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());

        // Setting a listener for info window events.
        mMap.setOnInfoWindowClickListener(this);

        // Turning on the My Location layer and the related control on the map.
        updateLocationUI();

        // Getting the current location of the device and setting the position of the map.
        getDeviceLocation();

        // Setting Schedules Map POI updates
        externalPollSchedule = sch.scheduleWithFixedDelay(delayTask, 10, 60, TimeUnit.SECONDS);
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
            mMap.animateCamera(yourLocation, 5000, null);

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

    @Override
    public void onPermissionsGranted(int requestCode) {
        startGPS();
    }

    @Override
    public void onPermissionsDenied(int requestCode) {
        String errorMessage = "The application has been unable to get permission to use the camera and GPS sensors and thus we not be able to work.";
        openDialog(errorMessage);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // open more detailed dialog for non-sensor markers
        if (!marker.getSnippet().equals("Sensor")) {
            POI poi = (POI) marker.getTag();
            openPlaceDialog(poi);
        }
    }

    /*
    Connecting to External Servers as a background task
     */
    private class PoiLookup extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            //set new sensor data update flag
            sensorsUpdated = false;
            // Getting the users location co-ordinates
            mCoordinate = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getSensorsInVicinity(mCoordinate);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //dismissDialog(progress_bar_type);
            if (sensorsUpdated) {
                refreshMap(mCoordinate);
            }

            if (sensorResponseFailed) {
                //clear previous sensor data
                SensorStore.getInstance().clearSensors();
                Toast.makeText(MapsActivity.this, "No Response from External Data Server", Toast.LENGTH_SHORT).show();
                refreshMap(mCoordinate);
            }
        }
    }

    /**
     * HTTP Request to External Servers for POI Data
     */
    private void getSensorsInVicinity(LatLng latLng) {
        sensorResponseFailed = false;
        if (mLastKnownLocation != null) {
            HttpClient httpclient = HttpClients.createDefault();
            try {
                String coordinatesToSearch = latLng.longitude + "," + latLng.latitude + "," + SensorStore.getInstance().getMapRange();

                URIBuilder builder = new URIBuilder("http://uoweb1.ncl.ac.uk/api/v1/sensors/live.json");
                builder.setParameter("api_key", "7b1a9ytmmhdcruloy81icy1u2dfk04njuco308y9eaxt9vfygr79aiplatb5c71s9iq30bdlad7swin7qgi7c2nvdt");
                builder.setParameter("buffer", coordinatesToSearch);

                //check which extra variables to add (temperature is default)
                StringBuilder apiVariables = new StringBuilder("Temperature");

                System.out.println("Humidity: " + String.valueOf(prefManager.isHumidityStatus()));
                if (prefManager.isHumidityStatus()) {
                    apiVariables.append("-and-Humidity");
                }
                System.out.println("Pressure: " + String.valueOf(prefManager.isPressureStatus()));
                if (prefManager.isPressureStatus()) {
                    apiVariables.append("-and-Pressure");
                }
                System.out.println("Sound: " + String.valueOf(prefManager.isSoundStatus()));
                if (prefManager.isSoundStatus()) {
                    apiVariables.append("-and-Sound");
                }
                // adding the variables to the call
                builder.setParameter("variable", apiVariables.toString());

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
                    if (jsonArray.length() > 0) {
                        //set new sensor data update flag
                        sensorsUpdated = true;
                        //clear previous sensor data
                        SensorStore.getInstance().clearSensors();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Sensor sensor = gson.fromJson(jsonObject.toString(), Sensor.class);

                            //add temperature reading
                            sensor.getData().getTemperature().setReading(Float.parseFloat(jsonObject.getJSONObject("data").getJSONObject("Temperature").getJSONObject("data").get(sensor.getLatest()).toString()));

                            if (prefManager.isHumidityStatus()) {
                                if (jsonObject.getJSONObject("data").has("Humidity")) {
                                    sensor.getData().getHumidity().setReading(Float.parseFloat(jsonObject.getJSONObject("data").getJSONObject("Humidity").getJSONObject("data").get(sensor.getLatest()).toString()));
                                }
                            }
                            if (prefManager.isPressureStatus()) {
                                if (jsonObject.getJSONObject("data").has("Pressure")) {
                                    sensor.getData().getPressure().setReading(Float.parseFloat(jsonObject.getJSONObject("data").getJSONObject("Pressure").getJSONObject("data").get(sensor.getLatest()).toString()));
                                }
                            }
                            if (prefManager.isSoundStatus()) {
                                if (jsonObject.getJSONObject("data").has("Sound")) {
                                    sensor.getData().getSound().setReading(Float.parseFloat(jsonObject.getJSONObject("data").getJSONObject("Sound").getJSONObject("data").get(sensor.getLatest()).toString()));
                                }
                            }
                            //add to the internal memory store
                            SensorStore.getInstance().addSensor(sensor);
                        }
                    }
                }

            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("No Network Connection");
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
                //set new sensor data update flag
                sensorResponseFailed = true;
            }
        }
    }

    /**
     Setting the POIs into Markers on Map
     */
    private void refreshMap(LatLng latLng) {
        // Clearing previous markers
        mMap.clear();

        //draw urban observatory search range circle
        circleMapRange = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(SensorStore.getInstance().getMapRange())
                .strokeWidth(3)
                .strokeColor(Color.GREEN));

        //draw A.R view range circle
        circleMapRange = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(prefManager.getArRange())
                .strokeWidth(3)
                .strokeColor(Color.BLUE));

        System.out.println("SensorDataExtract from " + SensorStore.getInstance().getStoreSize() + " sensors");
        // drawing sensors on map
        for (Sensor sensor : SensorStore.getInstance().getSensors()) {
            //extract the sensor name
            try {
                str = sensor.getName();
                sensorName = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
            } catch (Exception e) {
                sensorName = sensor.getName();
                //e.printStackTrace();
            }
            LatLng marker = new LatLng(sensor.getGeom().getCoordinates().get(1), sensor.getGeom().getCoordinates().get(0));
            mMap.addMarker(new MarkerOptions()
                    .position(marker)
                    .title(sensorName)
                    .snippet("Sensor")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_urban_observatory)))
                    .setTag(sensor);
        }

        //drawing place of interest on map
        for (POI poi : PlaceStore.getInstance().getPlaces()) {
            LatLng marker = new LatLng(poi.getLatitude(), poi.getLongitude());

            if (poi.getPlaceType() == "University" && prefManager.isUniversityStatus()) {
                mMap.addMarker(new MarkerOptions()
                        .position(marker)
                        .title(poi.getPlaceName())
                        .snippet(poi.getPlaceType())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ncl)))
                        .setTag(poi);
            }

            if (poi.getPlaceType() == "Attraction" && prefManager.isAttractionStatus()) {
                mMap.addMarker(new MarkerOptions()
                        .position(marker)
                        .title(poi.getPlaceName())
                        .snippet(poi.getPlaceType())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_poi)))
                        .setTag(poi);
            }

            if (poi.getPlaceType() == "Food" && prefManager.isFoodStatus()) {
                mMap.addMarker(new MarkerOptions()
                        .position(marker)
                        .title(poi.getPlaceName())
                        .snippet(poi.getPlaceType())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_food)))
                        .setTag(poi);
            }
        }
    }

    /**
     * Open Message dialog.
     */
    private void openHomeMenuDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MapsActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_message, null);
        final android.app.AlertDialog messageDialog = new android.app.AlertDialog.Builder(this).create();
        messageDialog.setView(mView);
        final TextView txtMessage = (TextView) mView.findViewById(R.id.txt_dialog_message);
        final TextView txtUser = (TextView) mView.findViewById(R.id.txt_dialog_user);
        final TextView txtUserId = (TextView) mView.findViewById(R.id.txt_dialog_user_id);
        final TextView txtGpsPosition = (TextView) mView.findViewById(R.id.txt_dialog_gps_position);
        //final TextView txtGpsAltitude = (TextView) mView.findViewById(R.id.txt_dialog_gps_altitude);
        final TextView txtGpsAccuracy = (TextView) mView.findViewById(R.id.txt_dialog_gps_accuracy);
        final TextView txtSensorsInMapRange = (TextView) mView.findViewById(R.id.txt_dialog_sensors_in_map_range);
        final TextView txtSensorsInArRange = (TextView) mView.findViewById(R.id.txt_dialog_sensors_in_ar_range);
        //final ImageButton btnOk = (ImageButton) mView.findViewById(R.id.btn_dialog_message_ok);
        final Button btnArMode = (Button) mView.findViewById(R.id.btnArMode);
        final Button btnSettings = (Button) mView.findViewById(R.id.btnSettings);
        final Button btnQuestionnaire = (Button) mView.findViewById(R.id.btnQuestionnaire);
        final Button btnExit = (Button) mView.findViewById(R.id.btnExit);

        txtMessage.setText("Menu");
        txtUserId.setText("User ID: " + user.getUid());
        txtUser.setText("User Mail: " + user.getEmail());

        if (mLastKnownLocation != null) {
            txtGpsPosition.setText("GPS Position: " + mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude());
            txtGpsAccuracy.setText("GPS Accuracy: " + mLastKnownLocation.getAccuracy() + "m (" + mLastKnownLocation.getProvider() + ")");
        } else {
            txtGpsPosition.setText("GPS Position: Not Obtained");
            txtGpsAccuracy.setText("GPS Accuracy: Not Obtained");
        }
        //txtGpsAltitude.setText("GPS Altitude: " + mLastKnownLocation.getAltitude()+"m");
        txtSensorsInMapRange.setText("Sensors in (" + SensorStore.getInstance().getMapRange() + "m) Range: " + SensorStore.getInstance().getStoreSize());

        //show sensors in visible in AR mode


        btnArMode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, MainActivity.class));
                uploadUserInteraction("A.R Mode");
                messageDialog.dismiss();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(new Intent(MapsActivity.this, SettingsActivity.class));
                messageDialog.dismiss();
            }
        });

        btnQuestionnaire.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                uploadUserInteraction("Questionnaire Opened");
                startActivity(new Intent(MapsActivity.this, QuestionnaireActivity.class));
                messageDialog.dismiss();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                uploadUserInteraction("Exit App");
                String message = " Thank you for using this application and please remember to give us feedback.";
                openDialog(message);
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
     * Open Message dialog.
     */
    private void openPlaceDialog(final POI poi) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MapsActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_place, null);
        final android.app.AlertDialog messageDialog = new android.app.AlertDialog.Builder(this).create();
        messageDialog.setView(mView);
        final TextView txtMessage = (TextView) mView.findViewById(R.id.txt_dialog_message);
        final TextView txtGpsPosition = (TextView) mView.findViewById(R.id.txt_dialog_gps_position);
        final TextView txtAddress = (TextView) mView.findViewById(R.id.txt_dialog_address);
        final TextView txtOperating = (TextView) mView.findViewById(R.id.txt_dialog_operating);
        final TextView txtDescription = (TextView) mView.findViewById(R.id.txt_dialog_description);
        final TextView txtPhoneNumber = (TextView) mView.findViewById(R.id.textPhoneNumber);

        final ImageView imgPlacePoi = (ImageView) mView.findViewById(R.id.ivPoi);
        final Button btnPhoneNumber = (Button) mView.findViewById(R.id.btnPhoneNumber);
        final Button btnWebsite = (Button) mView.findViewById(R.id.btnWebsite);

        txtMessage.setText(poi.getPlaceName());
        imgPlacePoi.setImageResource(placePictures.get(String.valueOf(poi.getPlaceId())));

        if (mLastKnownLocation != null) {
            txtGpsPosition.setText("GPS Position: " + mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude());
        } else {
            txtGpsPosition.setText("GPS Position: Not Obtained");
        }
        txtAddress.setText("Address: " + poi.getAddress());
        txtOperating.setText("Hours: " + poi.getOpeningHours());
        txtPhoneNumber.setText("Call " + poi.getPhoneNumber());
        txtDescription.setText(poi.getPlaceDescription());

        btnPhoneNumber.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_CALL, Uri.parse("tel: "+ poi.getPhoneNumber()));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                startActivity(intent);
                messageDialog.dismiss();
            }
        });

        btnWebsite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(poi.getPlaceWebsite()));
                startActivity(browserIntent);
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

    private void openDialog(String message) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MapsActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_message_error, null);
        final android.app.AlertDialog messageDialog = new android.app.AlertDialog.Builder(this).create();
        messageDialog.setView(mView);
        final TextView txtMessage = (TextView) mView.findViewById(R.id.txt_dialog_message);
        final ImageButton btnOk = (ImageButton) mView.findViewById(R.id.btn_dialog_message_ok);
        txtMessage.setText(message);
        btnOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                messageDialog.dismiss();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        //capturing the cancel button
        messageDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                messageDialog.dismiss();
                android.os.Process.killProcess(android.os.Process.myPid());
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
        Log.v(TAG,"location changed = " + location);
        System.out.println("altitude: " + location.getAltitude());

        // store it off for use when we need it
        mLastKnownLocation = location;
        lastLatitude = location.getLatitude();
        lastLongitude = location.getLongitude();
        locationData = "["+String.valueOf(lastLatitude)+","+String.valueOf(lastLongitude)+"]";
        toolbar.setTitle("GPS: "+  String.valueOf(mLastKnownLocation.getAccuracy())+ "meters");

        //check if settings have changed and refresh view
        if (prefManager.isSettingsChangedStatus()){
            if (mMap != null){
                refreshMap(mCoordinate);
            }
            //clear flag
            prefManager.setSettingsChangedStatus(false);
            //upload user changed settings
            uploadUserInteraction("Change of Parameters");
        }
        //user POI location awareness monitoring
        proximityCheck();

    }

    // call to external data source
    private Runnable delayTask = new Runnable(){
        @Override
        public void run() {
            try{
                System.out.println("\t External DataSource Call Execution Time: "
                        + fmt.format(new Date()));
                //check for GPS availability
                if (SensorStore.getInstance().getResult() != null){
                    showGpsSettingAlert();
                }else{
                    new PoiLookup().execute();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    };


    private void proximityCheck(){
        // declare vibrator
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // for all non sensor points of interest check if user in in 25m range
        for (POI poi: PlaceStore.getInstance().getPlaces()){
            if (mLastKnownLocation.distanceTo(poi) < 25){
                if (poi.isProximityAlert()){
                    poi.setProximityAlert(false);
                    // notify user by vibrating phone for 500 milliseconds
                    v.vibrate(500);
                    //upload user got in proximity
                    mLastPoiInteractionName = poi.getPlaceName();
                    uploadUserInteraction("Proximity");
                    //open the window to show details of the point of interest
                    openPlaceDialog(poi);
                }
            }
        }
    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter(){
            myContentsView = getLayoutInflater().inflate(R.layout.info_window, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            //save the marker clicked and upload the interaction
            mLastPoiInteractionName = marker.getTitle();
            uploadUserInteraction("POI Click");

            ImageView ivSource = ((ImageView) myContentsView.findViewById(R.id.img_source));
            TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.tv_title));
            TextView tvTime = ((TextView)myContentsView.findViewById(R.id.tv_time));
            TextView tvDistance = ((TextView)myContentsView.findViewById(R.id.tv_distance));
            TextView tvTemp = ((TextView)myContentsView.findViewById(R.id.tv_temp));
            TextView tvHumidity = ((TextView)myContentsView.findViewById(R.id.tv_humidity));
            TextView tvPressure= ((TextView)myContentsView.findViewById(R.id.tv_pressure));
            TextView tvSound= ((TextView)myContentsView.findViewById(R.id.tv_sound));
            TextView tvEndText= ((TextView)myContentsView.findViewById(R.id.tv_endText));
            LinearLayout layoutTime = ((LinearLayout)myContentsView.findViewById(R.id.layoutTime));
            LinearLayout layoutTempHumidity = ((LinearLayout)myContentsView.findViewById(R.id.layoutTempHumidity));
            LinearLayout layoutPressureSound = ((LinearLayout)myContentsView.findViewById(R.id.layoutPressureSound));

            tvTitle.setText(marker.getTitle());

            if (marker.getSnippet().equals("Sensor")){

                // display sensor
                Sensor sensor = (Sensor) marker.getTag();
                ivSource.setImageResource(sourceIconMap.get(marker.getSnippet()));

                layoutTime.setVisibility(View.VISIBLE);
                layoutTempHumidity.setVisibility(View.VISIBLE);
                layoutPressureSound.setVisibility(View.VISIBLE);

                tvTime.setText(sensor.getLatest());
                Location location = new Location("manual");
                location.setLongitude(marker.getPosition().longitude);
                location.setLatitude(marker.getPosition().latitude);

                tvDistance.setText((int) mLastKnownLocation.distanceTo(location) + "m");
                String temperatureReading = String.format("%.1fc",sensor.getData().getTemperature().getReading());
                tvTemp.setText(temperatureReading);

                if (prefManager.isPressureStatus()){
                    String humidityReading = String.format("%.1f%%",sensor.getData().getHumidity().getReading());
                    tvHumidity.setText(humidityReading);
                }else{
                    tvHumidity.setText("N/A");
                }

                if (prefManager.isPressureStatus()){
                    String pressureReading = String.format("%.1fhpa",sensor.getData().getPressure().getReading());
                    tvPressure.setText(pressureReading);
                }else {
                    tvPressure.setText("N/A");
                }

                if (prefManager.isSoundStatus()){
                    String soundReading = String.format("S: %.1fdB",sensor.getData().getSound().getReading());
                    tvSound.setText(soundReading);
                }else{
                    tvSound.setText("N/A");
                }

                tvEndText.setText("");

            }else{
                // display POI
                POI poi = (POI) marker.getTag();
                ivSource.setImageResource(sourceIconMap.get(marker.getSnippet()));

                float distance = mLastKnownLocation.distanceTo(poi);
                tvDistance.setText((int) distance + "m away");

                layoutTime.setVisibility(View.GONE);
                layoutTempHumidity.setVisibility(View.GONE);
                layoutPressureSound.setVisibility(View.GONE);
                tvEndText.setText("Click for more details");

            }
            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    public void uploadUserInteraction(String action){
        final UserInteraction userInteraction = new UserInteraction();

        userInteraction.setUserId(user.getUid());
        userInteraction.setAction(action);
        userInteraction.setPoiName(mLastPoiInteractionName);
        if (mLastKnownLocation != null ){
            userInteraction.setLatitude(mLastKnownLocation.getLatitude());
            userInteraction.setLongitude(mLastKnownLocation.getLongitude());
            userInteraction.setAccuracy(mLastKnownLocation.getAccuracy());
        }
        //send interaction to cloud
        dbUserInteractions.child(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Write new post
                        String key = dbUserInteractions.child(user.getUid()).push().getKey();

                        Map<String, Object> postValues = userInteraction.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(user.getUid() + "/" + key, postValues);

                        dbUserInteractions.updateChildren(childUpdates);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("Error:"+ databaseError.toString());
                    }
                }
        );
    }

    public void showGpsSettingAlert() {
        final Status status = SensorStore.getInstance().getResult().getStatus();
        final LocationSettingsStates state = SensorStore.getInstance().getResult().getLocationSettingsStates();

        try {
            // Show the dialog by calling startResolutionForResult(),
            // and check the result in onActivityResult().
            status.startResolutionForResult(
                    this, 1000);
        } catch (IntentSender.SendIntentException e) {
            // Ignore the error.
        }
    }
}
