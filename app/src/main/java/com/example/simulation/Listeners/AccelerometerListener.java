package com.example.simulation.Listeners;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simulation.util.MyAsyncTask;

import static com.example.simulation.Activities.MainActivity.Port_Ip;
import static com.example.simulation.Activities.MainActivity.locationListener;
import static com.example.simulation.Activities.MainActivity.macAddress;

public class AccelerometerListener implements SensorEventListener {

    public int threshold_x_axis;
    public int threshold_y_axis;
    public int threshold_z_axis;
    public String sensor_values = "";
    private SoundEventListener se;
    private int soundId;
    private int streamId;
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

        //mysound
        se = new SoundEventListener();
        soundId = se.getSoundId(context);


    }


    @Override
    public void onSensorChanged(final SensorEvent event) {
        textTable[0].setText("X: " + event.values[0]);
        textTable[1].setText("Y: " + event.values[1]);
        textTable[2].setText("Z: " + event.values[2]);
        sensor_values = "";
        sensor_values = sensor_values + Float.toString(event.values[0]) + "," + Float.toString(event.values[1]) + "," + Float.toString(event.values[2]);

        String topic = macAddress + "/" + getSensorValue() + "/" + locationListener.getDevLatitude() + "/" + locationListener.getDevLongtitude();

        MyAsyncTask tt = new MyAsyncTask(topic, Port_Ip, context);
        tt.execute();

        CharSequence text = "Βe careful!!";
        final Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 1500);

        //se.playOnce(soundId);
        //se.stopSound(streamId);
        return;
    }



    public void unregister(SensorManager SM) {
        SM.unregisterListener(this);
        se.stopSound(streamId);
    }


    public String getSensorValue() {
        return sensor_values;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Not in use
    }
}
