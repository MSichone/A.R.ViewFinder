package com.masitano.arviewfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextMapRange;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editTextMapRange = (EditText) findViewById(R.id.editTextMapRange);
        buttonSave = (Button) findViewById(R.id.buttonSave);

        System.out.println("Sensor Map Range: " + SensorStore.getInstance().getMapRange());
        //getting current setting
        //editTextMapRange.setText(SensorStore.getInstance().getMapRange().);

        //attaching listener to button
        buttonSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.buttonSave){
            //save settings
            saveSettings();
        }
    }

    private void saveSettings(){
        SensorStore.getInstance().setMapRange(Integer.parseInt(editTextMapRange.getText().toString()));
        finish();
    }

}
