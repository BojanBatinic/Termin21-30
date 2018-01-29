package com.example.androiddevelopment.glumcilegende;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by BBLOJB on 23.1.2018..
 */

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    SensorManager sensorManager;

    TextView tvAccelerometar;
    TextView tvLinearAccelerometar;
    TextView tvMagneticField;
    TextView tvProximitySensor;
    TextView tvGyroscope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        tvAccelerometar = (TextView) findViewById(R.id.tvAccelerometar);
        tvLinearAccelerometar = (TextView) findViewById(R.id.tvLinearAccelerometer);
        tvMagneticField = (TextView) findViewById(R.id.tvMagneticField);
        tvProximitySensor = (TextView) findViewById(R.id.tvProximitySensor);
        tvGyroscope = (TextView) findViewById(R.id.tvGyroscope);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            tvAccelerometar.setText("Accelerometer: [" + x + ", " + y + ", " + z + "]");
        }else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            tvLinearAccelerometar.setText("Non-gravity accelerometer: [" + x + ", " + y + ", " + z + "]");
        }else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            tvMagneticField.setText("Magnetic field: [" + x + ", " + y + ", " + z + "]");
        }else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            tvGyroscope.setText("Gyroscpe : [" + x + ", " + y + ", " + z + "]");
        }else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            float[] values = event.values;
            float x = values[0];

            if (x>0){
                tvProximitySensor.setText("Daleko");
            }else {
                tvProximitySensor.setText("Blizu");
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
