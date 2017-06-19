package com.masitano.arviewfinder;

import android.Manifest;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.kishan.askpermission.AskPermission;
import com.kishan.askpermission.PermissionCallback;

/**
 * Created by Masitano on 6/19/2017.
 *
 * Loosely based on the works of Shane Conder & Lauren Darcey on Android SDK Augmented Reality: Camera & Sensor Setup
 * Accessed at https://code.tutsplus.com/tutorials/android-sdk-augmented-reality-camera-sensor-setup--mobile-7873
 * Accessed on 19/06/2017
 */
public class MainActivity extends AppCompatActivity implements PermissionCallback {

    private static final int REQUEST_PERMISSIONS = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if we have permission to use camera (Android 6.0)
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA);
        System.out.println("permissionCheck" + permissionCheck);
        if (permissionCheck == -1){
            new AskPermission.Builder(this)
                    .setPermissions(Manifest.permission.CAMERA/*,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION*/)
                    .setCallback(this)
                    .request(REQUEST_PERMISSIONS);

        }else{
            FrameLayout arViewPane = (FrameLayout) findViewById(R.id.ar_view_pane);

            LiveView arDisplay = new LiveView(this,this);
            arViewPane.addView(arDisplay);

            OverlayView arContent = new OverlayView(getApplicationContext());
            arViewPane.addView(arContent);

        }
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        Toast.makeText(this, "Permissions Granted.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode) {
        Toast.makeText(this, "Permissions Denied.", Toast.LENGTH_LONG).show();
    }
}
