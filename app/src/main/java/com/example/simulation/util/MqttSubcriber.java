package com.example.simulation.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;

import com.example.simulation.Activities.MainActivity;
import com.example.simulation.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttSubcriber {
    private final String topicFilter;
    private final String serverURI;
    private final int qos = 2;
    private final CallBack callback;
    private final MainActivity mainactivity;

    public MqttSubcriber(String serverURI, String topicFilter, Context context, MainActivity mainActivity) {
        this.topicFilter = topicFilter;
        this.serverURI = serverURI;
        this.mainactivity = mainActivity;
        callback = new CallBack(context);
    }

    public void subscribe() {
        String clientId = "ΜyClient2Android";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(serverURI, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setWill("Test/clienterrors", "crashed".getBytes(), 2, false);
            connOpts.setCleanSession(true);
            sampleClient.setCallback(callback);
            System.out.println("Connecting to broker: " + serverURI);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            Thread.sleep(1000);
            sampleClient.subscribe(topicFilter, qos);
            System.out.println("Subscribed");
            //  sampleClient.disconnect();
            //System.out.println("Disconnected");

        } catch (Exception me) {
            if (me instanceof MqttException) {
                System.out.println("reason " + ((MqttException) me).getReasonCode());
            }
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }

    }

    class CallBack implements MqttCallback {
        private final Context context;

        CallBack(Context context) {
            this.context = context;
        }

        @Override
        public void connectionLost(Throwable cause) {
            System.err.println("connection lost");

        }

        @Override
        public void messageArrived(String topic, final MqttMessage message) {
            System.out.println("topic: " + topic);
            final String mes = new String(message.getPayload());
            System.out.println("message: " + mes);

            if (mes.equals("alarm")) {
                final MediaPlayer mp = MediaPlayer.create(context, R.raw.sound);
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer player) {
                        player.release();
                    }
                });
            } else if (mes.equals("flash")) {
                final MediaPlayer mp = MediaPlayer.create(context, R.raw.sound);
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer player) {
                        player.release();
                    }
                });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    try {
                        for (int i = 0; i < 3; i++) {
                            mainactivity.flashLightOn();
                            Thread.sleep(1000);
                            mainactivity.flashLightOff();
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }

        }


        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            System.err.println("delivery complete");

        }
    }
}

