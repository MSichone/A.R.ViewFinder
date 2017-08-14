package com.masitano.arviewfinder;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.masitano.arviewfinder.models.POI;
import com.masitano.arviewfinder.utilities.PlaceStore;
import com.masitano.arviewfinder.utilities.PreferenceManager;
import com.masitano.arviewfinder.utilities.SensorStore;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Masitano K.P Sichone on 6/19/2017.
 *
 * This class manages the device's GPS in combination with various sensors and outputs data to the application overlay layer.
 *
 * The other sensors used are the device's Accelerometer, Compass and Gyroscope.
 *
 * Loosely based on the works of Shane Conder & Lauren Darcey on Android SDK Augmented Reality: Camera & Sensor Setup
 * Accessed at https://code.tutsplus.com/tutorials/android-sdk-augmented-reality-camera-sensor-setup--mobile-7873
 * Accessed on 19/06/2017.
 *
 * Loosely based on the works of Krzysztof Jackowski on Augmented Reality in Mobile Devices
 * Accessed at https://www.netguru.co/blog/augmented-reality-mobile-android
 * Accessed on 20/06/2017.
 *
 */

public class OverlayView extends View implements SensorEventListener, OnLocationChangedListener, OnConnectedListener {

    public static final String DEBUG_TAG = "ArViewFinder.OLV";

    private SensorManager sensors = null;
    private Sensor sensorAccelerometer, sensorCompass, sensorGyro;
    private boolean isAccelAvailable;
    private boolean isCompassAvailable;
    private boolean isGyroAvailable;

    String accelData = "Accelerometer Data";
    String compassData = "Compass Data";
    String gyroData = "Gyro Data";
    String locationData = "GPS";
    String sensorName,str, temperatureReading,pressureReading, humidityReading,soundReading;

    private double lastLatitude = 0;
    private double lastLongitude = 0;

    private CurrentLocation myCurrentLocation;

    private float verticalFOV;
    private float horizontalFOV;
    private Location lastLocation;
    private float[] lastAccelerometer;
    private float[] lastCompass;
    private TextPaint contentPaint, contentPaint2;
    private Paint targetPaint;
    private Rect rect;
    private Activity act;
    private Context ctx;

    private POI poi = new POI("manual");

    private StringBuilder text, sbPOI;

    private HashMap<String, Bitmap> mapMarkers = new HashMap<String, Bitmap>();
    private static List<POI> pois = new ArrayList<POI>();
    private List<com.masitano.arviewfinder.models.Sensor> urbanObservatorySensors = new ArrayList<com.masitano.arviewfinder.models.Sensor>();

    //variables
    private PreferenceManager prefManager;
    private int sensorsDrawn = 0;
    private int placesDrawn = 0;
    private final static POI cottageChicken = new POI("manual");
    static {
        cottageChicken.setLatitude(54.973614d);
        cottageChicken.setLongitude(-1.619569d);
        cottageChicken.setAltitude(191.5d);
        cottageChicken.getSensor().setName("Cottage Chicken");
    }

    //constructor for overlay view
    public OverlayView(Context context, Activity activity) {
        super(context);

        sensors = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometer = sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorCompass = sensors.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorGyro = sensors.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // access the mobile device's GPS, Accelerometer, Compass & Gyroscope
        startSensors();
        startGPS();

        //initializing bitmaps for markers
        initializeMarkers();

        // get some camera parameters
        Camera camera = Camera.open();
        Camera.Parameters params = camera.getParameters();
        verticalFOV = params.getVerticalViewAngle();
        horizontalFOV = params.getHorizontalViewAngle();
        camera.release();

        // paint for text
        contentPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        contentPaint.setTextAlign(Paint.Align.LEFT);
        contentPaint.setTextSize(25);
        contentPaint.setColor(Color.RED);

        contentPaint2 = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        contentPaint2.setTextAlign(Paint.Align.LEFT);
        contentPaint2.setTextSize(25);
        contentPaint2.setColor(Color.GREEN);

        // paint for target
        targetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        targetPaint.setColor(Color.GREEN);
        rect = new Rect();
        act = activity;
        ctx = context;

        prefManager = new PreferenceManager(act);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        sensorsDrawn = 0;
        placesDrawn = 0;
        /*StringBuilder text = new StringBuilder(accelData).append("\n");
        text.append(compassData).append("\n");
        text.append(gyroData).append("\n");*/

        drawPlaces(canvas);
        canvas.save();
        drawSensors(canvas);

        StringBuilder text = new StringBuilder("A.R Mode ["+ prefManager.getArRange() +"m] Range").append("\n");
        text.append("Sensors: " + sensorsDrawn + "/" + SensorStore.getInstance().getStoreSize() + " in range").append("\n");
        text.append("Places: " + placesDrawn + "/" + PlaceStore.getInstance().getStoreSize() + " in range").append("\n");

        canvas.save();
        canvas.translate(15.0f, 15.0f);
        StaticLayout textBox = new StaticLayout(text.toString(), contentPaint,
                480, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
        textBox.draw(canvas);
        canvas.restore();
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent event) {

        int x=(int)event.getX();
        int y=(int)event.getY();
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if( x > getWidth()/2 && x < getWidth()/2 + 200 && y > getHeight()/2 && y < getHeight()/2 + 200 )
                {
                    Log.e("TOUCHED", "X: " + x + " Y: " + y);
                    //Bitmap touched
                    //openMenuDialog();
                    popUp();


                }

                *//*if(rect.contains(x, y))
                {
                    //BITMAP TOUCHED
                    System.out.println("bitmap touched");
                }*//*
        }


        return super.onTouchEvent(event);
    }*/

    public void drawSensors(Canvas canvas){
        //get sensors
        try {
            if (SensorStore.getInstance().getStoreSize() > 0) {
                //deepcopy
                urbanObservatorySensors = new ArrayList<>();
                urbanObservatorySensors = (ArrayList<com.masitano.arviewfinder.models.Sensor>) SensorStore.getInstance().getSensors();
                for (com.masitano.arviewfinder.models.Sensor sensor : urbanObservatorySensors) {

                    try {
                        poi.setSensor(sensor);
                        poi.setLatitude(sensor.getGeom().getCoordinates().get(1));
                        poi.setLongitude(sensor.getGeom().getCoordinates().get(0));
                        poi.setAltitude(191.5d);
                    } catch (Exception e) {
                        //System.out.println("Adding Test");
                        //e.printStackTrace();
                        poi = cottageChicken;
                    }

                    try {

                        temperatureReading = String.format("Temp: %.1f c", poi.getSensor().getData().getTemperature().getReading());
                        humidityReading = String.format("Humi: %.1f %%", poi.getSensor().getData().getHumidity().getReading());
                        soundReading = String.format("Snd: %.1f db", poi.getSensor().getData().getSound().getReading());
                        pressureReading = String.format("Press: %.1f hpa", poi.getSensor().getData().getPressure().getReading());
                        str = poi.getSensor().getName();
                        sensorName = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
                    } catch (Exception e) {
                    /*System.out.println("Adding Test");
                    e.printStackTrace();*/
                        sensorName = poi.getSensor().getName();
                    }

                    float bearingTo = 0.0f;
                    float distanceTo = 0.0f;

                /*StringBuilder text = new StringBuilder(accelData).append("\n");
                text.append(compassData).append("\n");
                text.append(gyroData).append("\n");
*/
                    if (lastLocation != null) {
                    /*text.append(
                            String.format("GPS = (%.3f, %.3f) @ (%.2f meters up)",
                                    lastLocation.getLatitude(),
                                    lastLocation.getLongitude(),
                                    lastLocation.getAltitude())).append("\n");*/

                        //curBearingToMW = lastLocation.bearingTo(cottageChicken);
                        bearingTo = lastLocation.bearingTo(poi);
                        //distanceTo = lastLocation.distanceTo(cottageChicken);
                        distanceTo = lastLocation.distanceTo(poi);

                    /*text.append(String.format("Bearing to MW: %.3f", curBearingToMW))
                            .append("\n");
                    text.append(String.format("Distance to MW: %.3f m", distanceTo))
                            .append("\n");*/
                    }

                    //check POI is in A.R View Range
                    if (distanceTo < prefManager.getArRange()) {
                        // compute rotation matrix
                        float rotation[] = new float[9];
                        float identity[] = new float[9];
                        if (lastAccelerometer != null && lastCompass != null) {
                            boolean gotRotation = SensorManager.getRotationMatrix(rotation,
                                    identity, lastAccelerometer, lastCompass);
                            if (gotRotation) {
                                float cameraRotation[] = new float[9];
                                // remap such that the camera is pointing straight down the Y
                                // axis
                                SensorManager.remapCoordinateSystem(rotation,
                                        SensorManager.AXIS_X, SensorManager.AXIS_Z,
                                        cameraRotation);

                                // orientation vector
                                float orientation[] = new float[3];
                                SensorManager.getOrientation(cameraRotation, orientation);

                            /*text.append(
                                    String.format("Orientation (%.3f, %.3f, %.3f)",
                                            Math.toDegrees(orientation[0]), Math.toDegrees(orientation[1]), Math.toDegrees(orientation[2])))
                                    .append("\n");*/

                                // draw horizon line (a nice sanity check piece) and the target (if it's on the screen)
                                canvas.save();
                                // use roll for screen rotation
                                canvas.rotate((float) (0.0f - Math.toDegrees(orientation[2])));

                                // Translate, but normalize for the FOV of the camera -- basically, pixels per degree, times degrees == pixels
                                float dx = (float) ((canvas.getWidth() / horizontalFOV) * (Math.toDegrees(orientation[0]) - bearingTo));
                                float dy = (float) ((canvas.getHeight() / verticalFOV) * Math.toDegrees(orientation[1]));

                                // wait to translate the dx so the horizon doesn't get pushed off
                                canvas.translate(0.0f, 0.0f - dy);

                                // make our line big enough to draw regardless of rotation and translation
                                //canvas.drawLine(0f - canvas.getHeight(), canvas.getHeight() / 2, canvas.getWidth() + canvas.getHeight(), canvas.getHeight() / 2, targetPaint);

                                // now translate the dx
                                canvas.translate(0.0f - dx, 0.0f);

                                // draw our point -- we've rotated and translated this to the right spot already
                                canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, 8.0f, targetPaint);
                                rect.set(canvas.getWidth() / 2, canvas.getHeight() / 2, canvas.getWidth() / 2 + 150, canvas.getHeight() / 2 + 150);
                                canvas.drawBitmap(mapMarkers.get("Sensor"), null, rect, null);

                                canvas.drawText("Name: " + sensorName, canvas.getWidth() / 2, canvas.getHeight() / 2 - 160, contentPaint2);
                                canvas.drawText(String.format("Distance: %.2f m", distanceTo), canvas.getWidth() / 2, canvas.getHeight() / 2 - 120, contentPaint2);
                                canvas.drawText(temperatureReading + " " + humidityReading, canvas.getWidth() / 2, canvas.getHeight() / 2 - 80, contentPaint2);
                                canvas.drawText(pressureReading + " " + soundReading, canvas.getWidth() / 2, canvas.getHeight() / 2 - 40, contentPaint2);

                                sensorsDrawn++;
                                canvas.restore();

                            }//end of rotation check
                        }//end of accel & compass check
                    }//end of check A.R Range
                }//end of POI loop
            }//end of sensor size check
        }catch (ConcurrentModificationException e){

        }
    }
    public void drawPlaces(Canvas canvas){

        for (POI poi: PlaceStore.getInstance().getPlaces()) {

            sensorName = poi.getPlaceName();
            float bearingTo = 0.0f;
            float distanceTo = 0.0f;

            if (lastLocation != null) {
                bearingTo = lastLocation.bearingTo(poi);
                distanceTo = lastLocation.distanceTo(poi);
            }

            //check POI is in A.R View Range < prefManager.getArRange()
            if (distanceTo < prefManager.getArRange()){
                // compute rotation matrix
                float rotation[] = new float[9];
                float identity[] = new float[9];
                if (lastAccelerometer != null && lastCompass != null) {
                    boolean gotRotation = SensorManager.getRotationMatrix(rotation,
                            identity, lastAccelerometer, lastCompass);
                    if (gotRotation) {
                        float cameraRotation[] = new float[9];
                        // remap such that the camera is pointing straight down the Y
                        // axis
                        SensorManager.remapCoordinateSystem(rotation,
                                SensorManager.AXIS_X, SensorManager.AXIS_Z,
                                cameraRotation);

                        // orientation vector
                        float orientation[] = new float[3];
                        SensorManager.getOrientation(cameraRotation, orientation);

                        // draw horizon line (a nice sanity check piece) and the target (if it's on the screen)
                        canvas.save();
                        // use roll for screen rotation
                        canvas.rotate((float) (0.0f - Math.toDegrees(orientation[2])));

                        // Translate, but normalize for the FOV of the camera -- basically, pixels per degree, times degrees == pixels
                        float dx = (float) ((canvas.getWidth() / horizontalFOV) * (Math.toDegrees(orientation[0]) - bearingTo));
                        float dy = (float) ((canvas.getHeight() / verticalFOV) * Math.toDegrees(orientation[1]));

                        // wait to translate the dx so the horizon doesn't get pushed off
                        canvas.translate(0.0f, 0.0f - dy);

                        // make our line big enough to draw regardless of rotation and translation
                        //canvas.drawLine(0f - canvas.getHeight(), canvas.getHeight() / 2, canvas.getWidth() + canvas.getHeight(), canvas.getHeight() / 2, targetPaint);

                        // now translate the dx
                        canvas.translate(0.0f - dx, 0.0f);

                        // draw our point -- we've rotated and translated this to the right spot already
                        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, 8.0f, targetPaint);
                        rect.set(canvas.getWidth() / 2, canvas.getHeight() / 2, canvas.getWidth() / 2 + 150, canvas.getHeight() / 2 + 150);
                        canvas.drawBitmap(mapMarkers.get(poi.getPlaceSource()), null, rect, null);

                        canvas.drawText("Name: " + sensorName, canvas.getWidth() / 2, canvas.getHeight() / 2 - 120, contentPaint2);
                        canvas.drawText( String.format("Distance: %.2f m",distanceTo), canvas.getWidth() / 2, canvas.getHeight() / 2 - 80, contentPaint2);
                        //canvas.drawText("Distance: " + distanceTo, canvas.getWidth() / 2, canvas.getHeight() / 2 - 80, contentPaint2);
                        canvas.drawText("Open:" + poi.getOpeningHours(), canvas.getWidth() / 2, canvas.getHeight() / 2 - 40, contentPaint2);
                        placesDrawn++;
                        canvas.restore();
                    }//end of rotation check
                }//end of accel & compass check
            }//end of check A.R Range
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        StringBuilder msg = new StringBuilder(sensorEvent.sensor.getName()).append(" ");
        for(float value: sensorEvent.values)
        {
            //msg.append("[").append(value).append("]");
            msg.append("[").append(String.format("%.3f", value)).append("]");
        }

        switch(sensorEvent.sensor.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:
                //lastAccelerometer = sensorEvent.values.clone();
                lastAccelerometer = lowPass(sensorEvent.values.clone(), lastAccelerometer);
                accelData = msg.toString();
                break;
            case Sensor.TYPE_GYROSCOPE:
                gyroData = msg.toString();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                //lastCompass = sensorEvent.values.clone();
                lastCompass = lowPass(sensorEvent.values.clone(), lastCompass);
                compassData = msg.toString();
                break;
        }


        this.invalidate();
    }

    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + 0.25f * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //Log.v(DEBUG_TAG, "Location Change");

        // store it off for use when we need it
        lastLocation = location;

        lastLatitude = location.getLatitude();
        lastLongitude = location.getLongitude();
        locationData = "["+String.valueOf(lastLatitude)+","+String.valueOf(lastLongitude)+"]";

        Log.v(DEBUG_TAG,"location changed = " + location);

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(DEBUG_TAG,"Connected");
    }

    private void startGPS() {
        myCurrentLocation = new CurrentLocation(this,this,getContext());
        myCurrentLocation.buildGoogleApiClient(getContext());
        myCurrentLocation.start();
    }

    private void startSensors() {
        isAccelAvailable = sensors.registerListener(this, sensorAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
        isCompassAvailable = sensors.registerListener(this, sensorCompass,
                SensorManager.SENSOR_DELAY_NORMAL);
        isGyroAvailable = sensors.registerListener(this, sensorGyro,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void initializeMarkers(){
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_urban_observatory);
        mapMarkers.put("Sensor",BitmapFactory.decodeResource(getResources(), R.drawable.ic_urban_observatory));
        mapMarkers.put("University",BitmapFactory.decodeResource(getResources(), R.drawable.ic_ncl));
        mapMarkers.put("Attraction",BitmapFactory.decodeResource(getResources(), R.drawable.ic_poi));
        mapMarkers.put("Food",BitmapFactory.decodeResource(getResources(), R.drawable.ic_food));
    }

    private void openMenuDialog(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(act);
        View mView = layoutInflaterAndroid.inflate(R.layout.float_menu, null);
        final android.app.AlertDialog messageDialog = new android.app.AlertDialog.Builder(act).create();
        messageDialog.setView(mView);
        messageDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                messageDialog.dismiss();
            }
        });
        messageDialog.show();

    }


    /*private void popUp(){
        // Initialize a new instance of LayoutInflater service
        //LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        //View customView = inflater.inflate(R.layout.custom_layout,null);
        View layout = inflater.inflate(R.layout.custom_layout,
                (ViewGroup) findViewById(R.id.rl_custom_layout));

        // Initialize a new instance of popup window
        *//*popupWindow = new PopupWindow(
                customView,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );*//*
        popupWindow = new PopupWindow(layout, 300, 370, true);
        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);


        // Set an elevation value for popup window
        // Call requires API level 21
        *//*if(Build.VERSION.SDK_INT>=21){
            popupWindow.setElevation(5.0f);
        }*//*

        // Get a reference for the custom view close button
        *//*ImageButton closeButton = (ImageButton) findViewById(R.id.ib);

        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                popupWindow.dismiss();
            }
        });*//*

        // Finally, show the popup window at the center location of root relative layout
        //popupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);
    }*/

    // this is not an override
    public void onPause() {
        Log.v(DEBUG_TAG,"onPause");
        myCurrentLocation.stop();
        sensors.unregisterListener(this);
    }

    // this is not an override
    public void onResume() {
        Log.v(DEBUG_TAG,"onResume");
        startSensors();
        startGPS();
    }
}

