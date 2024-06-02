package com.example.compassandgpscamera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class compass extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor magnetometer;
    private ImageView compassImage;
    private TextView compassAngle;
    private TextView compassDirection;

    private float[] gravity;
    private float[] gyro;
    private float[] magnetic;

    private static final float NS2S = 1.0f / 1000000000.0f;
    private float timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        compassImage = findViewById(R.id.img);
        compassAngle = findViewById(R.id.textView);
        compassDirection = findViewById(R.id.direction);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        gravity = new float[3];
        gyro = new float[3];
        magnetic = new float[3];
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (timestamp != 0) {
                final float dT = (event.timestamp - timestamp) * NS2S;
                gyro[0] += event.values[0] * dT;
                gyro[1] += event.values[1] * dT;
                gyro[2] += event.values[2] * dT;
            }
            timestamp = event.timestamp;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magnetic = event.values;
        }

        float rotationMatrix[] = new float[9];
        boolean success = SensorManager.getRotationMatrix(rotationMatrix, null, gravity, magnetic);
        if (success) {
            float orientation[] = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientation);

            float azimuth = (float) Math.toDegrees(orientation[0]);
            if (azimuth < 0) {
                azimuth += 360;
            }

            compassAngle.setText("Angle: " + azimuth + "°");

            // Determine direction
            String direction;
            if (azimuth >= 315 || azimuth < 45) {
                direction = "NORTH";
            } else if (azimuth >= 45 && azimuth < 135) {
                direction = "EAST";
            } else if (azimuth >= 135 && azimuth < 225) {
                direction = "SOUTH";
            } else {
                direction = "WEST";
            }

            compassDirection.setText("Direction: " + direction);

            compassImage.setRotation(-azimuth);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not in use for this example
    }
}
