package com.example.simulation.Listeners;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

public class AccelerometerListener implements SensorEventListener {

    public int threshold_x_axis;
    public int threshold_y_axis;
    public int threshold_z_axis;
    public String sensor_values = "";
    private TextView[] textTable;
    private Context context;

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
        textTable[0].setText("X: " + event.values[0]);
        textTable[1].setText("Y: " + event.values[1]);
        textTable[2].setText("Z: " + event.values[2]);
        sensor_values = "";
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
