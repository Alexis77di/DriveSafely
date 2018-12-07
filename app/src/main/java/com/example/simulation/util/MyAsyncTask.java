package com.example.simulation.util;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.simulation.R;

import java.util.Comparator;

import static com.example.simulation.Activities.MainActivity.flag;
import static com.example.simulation.Activities.MainActivity.macAddress;
import static com.example.simulation.Activities.MainActivity.rate;

public class MyAsyncTask extends android.os.AsyncTask<Void, Void, Void> {

    private final Comparator<Float> comparator;
    private String topic;
    private Context context;
    private String ip_port;
    private MqttSub subscriber;
    private MqttPublisher publisher;


    public MyAsyncTask(String topic, String ip_port, Context context) {
        this.topic = topic;
        this.ip_port = ip_port;
        this.context = context;


        this.comparator = new Comparator<Float>() {
            @Override
            public int compare(Float lhs, Float rhs) {
                return lhs.compareTo(rhs);
            }
        };
    }

    @Override
    protected Void doInBackground(Void... params) {


        try {
            // Sleeping for given time period
            Thread.sleep(rate);
            publisher = new MqttPublisher();
            publisher.main(topic, ip_port);
            if (!flag) {
                subscriber = new MqttSub();
                subscriber.main(macAddress, ip_port, context);

                final MediaPlayer mp = MediaPlayer.create(context, R.raw.alarm);
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer player) {
                        player.release();
                    }
                });

                flag = true;

            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
