package com.example.simulation.util;

import android.content.Context;

import static com.example.simulation.Activities.MainActivity.flag;
import static com.example.simulation.Activities.MainActivity.macAddress;
import static com.example.simulation.Activities.MainActivity.rate;

public class MyAsyncTask extends android.os.AsyncTask<Void, Void, Void> {

    private String topic;
    private Context context;
    private String ip_port;
    private MqttSub subscriber;
    private MqttPublisher publisher;



    public MyAsyncTask(String topic, String ip_port, Context context) {
        this.topic = topic;
        this.ip_port = ip_port;
        this.context = context;

    }

    @Override
    protected Void doInBackground(Void... params) {


        try {

            Thread.sleep(rate);  // Sleeping for given time period,by default 4 secs
            publisher = new MqttPublisher();
            publisher.main(topic, ip_port);
            if (!flag) {                                  //only one subscribe
                subscriber = new MqttSub();
                subscriber.main(macAddress, ip_port, context);

//                final MediaPlayer mp = MediaPlayer.create(context, R.raw.alarm);
//                mp.start();
//                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    public void onCompletion(MediaPlayer player) {
//                        player.release();
//                    }
//                });


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
