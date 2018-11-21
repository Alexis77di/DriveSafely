package com.example.simulation.Listeners;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.widget.TextView;

public class AccelerometerListener implements SensorEventListener {

    public int threshold_x_axis;
    public int threshold_y_axis;
    public int threshold_z_axis;
    public String sensor_values = "";
    private TextView[] textTable;
    private Context context;
    private double[] linear_acceleration = new double[3];
    private double[] gravity = new double[3];
    private Handler mHandler = new Handler();
    Runnable run = new Runnable() {

        @Override
        public void run() {

            textTable[0].setText("X: " + linear_acceleration[0]);
            textTable[1].setText("Y: " + linear_acceleration[1]);
            textTable[2].setText("Z: " + linear_acceleration[2]);

            //Kill runnable before a new one starts
            mHandler.removeCallbacks(run);

        }
    };

    public AccelerometerListener(SensorManager SM, int threshold_x_axis, int threshold_y_axis, int threshold_z_axis, TextView[] textTable, Context context) {

        Sensor mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Register sensor listener
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        this.textTable = textTable;
        this.threshold_x_axis = threshold_x_axis;
        this.threshold_y_axis = threshold_y_axis;
        this.threshold_z_axis = threshold_z_axis;
        this.context = context;
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {

        final double alpha = 0.8;
        sensor_values = "";
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];

        sensor_values = sensor_values + Float.toString(event.values[0]) + "," + Float.toString(event.values[1]) + "," + Float.toString(event.values[2]);

    }


    public void unregister(SensorManager SM) {
        SM.unregisterListener(this);
    }


    public String getSensorValue() {
        return sensor_values;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Not in use
    }
}
