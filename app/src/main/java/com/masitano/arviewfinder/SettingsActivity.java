package com.masitano.arviewfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.masitano.arviewfinder.utilities.PreferenceManager;
import com.masitano.arviewfinder.utilities.SensorStore;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtMapRange, txtArViewRange;
    private SeekBar seekBarMapRange, seekBarArRange;
    private Button buttonSave;
    private CheckBox chkTemperature, chkHumidity, chkPressure, chkSound;
    private CheckBox chkAttraction, chkUniversity, chkFood;
    private PreferenceManager prefManager;
    private int currentMapRange = 300;
    private int currentArViewRange;
    int step = 50;
    int max = 500;
    int min = 50;

    // ar view constants
    int arViewStep = 10;
    int arViewMax = 100;
    int arViewMin = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // initiations
        txtMapRange = (TextView) findViewById(R.id.txtMapRange);
        txtArViewRange = (TextView) findViewById(R.id.txtArViewRange);
        chkTemperature = (CheckBox) findViewById(R.id.checkbox_Temperature);
        chkHumidity = (CheckBox) findViewById(R.id.checkbox_Humidity);
        chkPressure = (CheckBox) findViewById(R.id.checkbox_Pressure);
        chkSound = (CheckBox) findViewById(R.id.checkbox_Sound);
        chkAttraction= (CheckBox) findViewById(R.id.checkbox_Attractions);
        chkUniversity = (CheckBox) findViewById(R.id.checkbox_University);
        chkFood= (CheckBox) findViewById(R.id.checkbox_Food);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        seekBarMapRange = (SeekBar)findViewById(R.id.seekBarMapRange);
        seekBarArRange = (SeekBar)findViewById(R.id.seekBarArRange);
        prefManager = new PreferenceManager(this);

        // setup slider scales
        seekBarMapRange.setMax( (max - min) / step );
        seekBarArRange.setMax( (arViewMax - arViewMin) / arViewStep );
        System.out.println("com.mas.map seekbarmax: " + seekBarArRange.getMax());

        // getting current settings
        currentMapRange = SensorStore.getInstance().getMapRange();
        currentArViewRange = prefManager.getArRange();
        txtMapRange.setText("Map Range set to " + currentMapRange + " meters");
        txtArViewRange.setText("A.R Range set to " + currentArViewRange + " meters");

        //int value = min + (currentMapRange * step);
        //seekBarMapRange.setProgress(value);
        int mapValue = (currentMapRange - min)/step;
        int arViewValue = (currentArViewRange - arViewMin)/arViewStep;
        seekBarMapRange.setProgress(mapValue);
        seekBarArRange.setProgress(arViewValue);


        chkTemperature.setChecked(prefManager.isTemperatureStatus());
        chkHumidity.setChecked(prefManager.isHumidityStatus());
        chkPressure.setChecked(prefManager.isPressureStatus());
        chkSound.setChecked(prefManager.isSoundStatus());
        chkAttraction.setChecked(prefManager.isAttractionStatus());
        chkUniversity.setChecked(prefManager.isUniversityStatus());
        chkFood.setChecked(prefManager.isFoodStatus());

        //attaching listener to button
        buttonSave.setOnClickListener(this);

        // perform seek bar change listener event used for getting the progress value
        seekBarMapRange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                //calculate user input from scale
                int value = min + (progressChangedValue * step);
                txtMapRange.setText("Map Range changed to " + value + " meters");
            }
        });

        // perform seek bar change listener event used for getting the progress value
        seekBarArRange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                //calculate user input from scale
                int value = arViewMin + (progressChangedValue * arViewStep);
                txtArViewRange.setText("A.R Range changed to " + value + " meters");
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.buttonSave){
            //save settings
            saveSettings();
        }
    }

    private void saveSettings(){
        //save sensor settings
        prefManager.setTemperatureStatus(chkTemperature.isChecked());
        prefManager.setHumidityStatus(chkHumidity.isChecked());
        prefManager.setPressureStatus(chkPressure.isChecked());
        prefManager.setSoundStatus(chkSound.isChecked());
        prefManager.setAttractionStatus(chkAttraction.isChecked());
        prefManager.setUniversityStatus(chkUniversity.isChecked());
        prefManager.setFoodStatus(chkFood.isChecked());

        //set flag of changes
        prefManager.setSettingsChangedStatus(true);

        System.out.println("com.mas.map found seekbarselectedprogress: " + seekBarArRange.getProgress());

        int value = min + (seekBarMapRange.getProgress() * step);
        int arViewValue = arViewMin + (seekBarArRange.getProgress() * arViewStep);

        SensorStore.getInstance().setMapRange(value);
        prefManager.setArRange(arViewValue);
        finish();
    }

}
