package com.example.simulation.util;

import android.content.Context;

import java.util.Comparator;

import static com.example.simulation.Activities.MainActivity.broker_run_flag;
import static com.example.simulation.Activities.MainActivity.rate;

public class MyAsyncTask extends android.os.AsyncTask<Void, Void, Void> {

    private final Comparator<Float> comparator;
    private String topic;
    private Context context;
    private String ip_port;
    private MqttSub subscriber;
    private MqttPublisher publisher;
    private boolean running = true;

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
            if (!broker_run_flag) {
                subscriber = new MqttSub();
                subscriber.main("7c:76:68:f2:65:20", ip_port, context);
                broker_run_flag = true;
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
