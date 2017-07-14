package com.masitano.arviewfinder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
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

    private FrameLayout arViewPane;
    private LiveView arDisplay;
    private OverlayView arContent;

    private static final int REQUEST_PERMISSIONS = 20;
    private AugmentedLocation mPoi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_logo_launcher);

        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openMainMenuDialog(v);
                        //Toast.makeText(MapsActivity.this, "Toolbar", Toast.LENGTH_SHORT).show();
                    }
                }
        );*/

        // Check if we have permission to use camera (Android 6.0)
        int permissionCheckCamera = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA);
        int permissionCheckFineLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheckCourseLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        System.out.println("permissionCheck" + permissionCheckCamera);
        if (permissionCheckCamera == -1 || permissionCheckFineLocation == -1 || permissionCheckCourseLocation == -1 ){
            new AskPermission.Builder(this)
                    .setPermissions(Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
                    .setCallback(this)
                    .request(REQUEST_PERMISSIONS);

        }else{
            arViewPane = (FrameLayout) findViewById(R.id.ar_view_pane);

            arDisplay = new LiveView(getApplicationContext(),this);
            arViewPane.addView(arDisplay);

            arContent = new OverlayView(getApplicationContext(),this);
            arViewPane.addView(arContent);
            //openMessageDialog("Testing");
            //openMenuDialog();
            //setAugmentedRealityPoint();

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

    /**
     * Open Message dialog.
     */
    private void openMessageDialog(final String dialogMessage) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_message, null);
        final android.app.AlertDialog messageDialog = new android.app.AlertDialog.Builder(this).create();
        messageDialog.setView(mView);
        final TextView txtMessage = (TextView) mView.findViewById(R.id.txt_dialog_message);
        final ImageButton btnOk = (ImageButton) mView.findViewById(R.id.btn_dialog_message_ok);
        txtMessage.setText(dialogMessage);
        btnOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Clicked.", Toast.LENGTH_LONG).show();
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

    private void openMenuDialog(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.float_menu, null);
        final android.app.AlertDialog messageDialog = new android.app.AlertDialog.Builder(this).create();
        messageDialog.setView(mView);
        messageDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                messageDialog.dismiss();
            }
        });
        messageDialog.show();


    }

    private void openMainMenuDialog(View view) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_message, null);
        final android.app.AlertDialog messageDialog = new android.app.AlertDialog.Builder(this).create();
        messageDialog.setView(mView);
        final TextView txtMessage = (TextView) mView.findViewById(R.id.txt_dialog_message);
        final ImageButton btnOk = (ImageButton) mView.findViewById(R.id.btn_dialog_message_ok);
        txtMessage.setText("Switch View");
        btnOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Clicked.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                //onBackPressed();
                System.out.println("Back Pressed: Killing Activity");
                arViewPane.removeAllViews();
                finish();
                //startActivity(new Intent(IntroductionActivity.this, MainActivity.class));
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
