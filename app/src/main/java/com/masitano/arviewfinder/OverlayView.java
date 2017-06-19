package com.masitano.arviewfinder;

        import android.content.Context;
        import android.content.res.Resources;
        import android.graphics.BitmapFactory;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.drawable.Drawable;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.LinearLayout;

/**
 * Created by Masitano on 6/19/2017.
 *
 * Loosely based on the works of Shane Conder & Lauren Darcey on Android SDK Augmented Reality: Camera & Sensor Setup
 * Accessed at https://code.tutsplus.com/tutorials/android-sdk-augmented-reality-camera-sensor-setup--mobile-7873
 * Accessed on 19/06/2017
 */

public class OverlayView extends View{

    public static final String DEBUG_TAG = "OverlayView Log";
    String testData = "Testing Data";

    public OverlayView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint contentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        contentPaint.setTextAlign(Paint.Align.CENTER);
        contentPaint.setTextSize(20);
        contentPaint.setColor(Color.RED);
        canvas.drawText(testData, canvas.getWidth()/2, canvas.getHeight()/4, contentPaint);

        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_logo_launcher), 0, -0, null); // Set drawable



    }

    /*private void addMenuIcon(){
        // Create a LinearLayout in which to add the ImageView
        mLinearLayout = new LinearLayout(this);

        // Instantiate an ImageView and define its properties
        ImageView i = new ImageView(this);
        i.setImageResource(R.drawable.my_image);
        i.setAdjustViewBounds(true); // set the ImageView bounds to match the Drawable's dimensions
        i.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        // Add the ImageView to the layout and set the layout as the content view
        mLinearLayout.addView(i);
        setContentView(mLinearLayout);
    }*/
}
