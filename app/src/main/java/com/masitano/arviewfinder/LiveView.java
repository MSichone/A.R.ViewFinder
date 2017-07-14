package com.masitano.arviewfinder;

import android.app.Activity;
import android.content.Context;

import android.hardware.Camera;
import android.util.Log;

import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Created by Masitano on 6/19/2017.
 *
 * This class manages the device's camera and outputs live video to the application live layer.
 *
 * Loosely based on the works of Shane Conder & Lauren Darcey on Android SDK Augmented Reality: Camera & Sensor Setup
 * Accessed at https://code.tutsplus.com/tutorials/android-sdk-augmented-reality-camera-sensor-setup--mobile-7873
 * Accessed on 19/06/2017
 */

public class LiveView extends SurfaceView implements SurfaceHolder.Callback {

    public static final String DEBUG_TAG = "ArViewFinder.LiveView ";
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private Activity mActivity;

    //constructor for live view
    public LiveView(Context context, Activity activity) {
        super(context);

        mActivity = activity;
        mHolder = getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        try {
            //access the mobile device camera
            mCamera = Camera.open();

            //set camera’s display orientation based on the current orientation of the device
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
            int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0: degrees = 0; break;
                case Surface.ROTATION_90: degrees = 90; break;
                case Surface.ROTATION_180: degrees = 180; break;
                case Surface.ROTATION_270: degrees = 270; break;
            }
            Camera.Parameters params = mCamera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(params);
            mCamera.setDisplayOrientation((info.orientation - degrees + 360) % 360);

            //attach the camera output to the surface view
            mCamera.setPreviewDisplay(mHolder);

        } catch (IOException e) {
            Log.e(DEBUG_TAG, "SurfaceCreated Exception: ", e);
        } catch (Exception e){
            Log.e(DEBUG_TAG, "Camera Start Exception: ", e);
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        // Determine appropriate preview size that fits the surface
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> prevSizes = params.getSupportedPreviewSizes();
        for (Camera.Size s : prevSizes)
        {
            if((s.height <= height) && (s.width <= width))
            {
                params.setPreviewSize(s.width, s.height);
                break;
            }
        }
        mCamera.setParameters(params);

        //start the live preview
        mCamera.startPreview();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //debugging release of camera when app closed or minimized
        Log.d(DEBUG_TAG, "SurfaceDestroyed & Camera Released");

        //stop the live preview and relinquish access to camera
        mCamera.stopPreview();
        mCamera.release();
    }
}
