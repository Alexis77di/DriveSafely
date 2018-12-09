package com.example.simulation.util;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.simulation.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttSub {
    private final String topicFilter;
    private final String serverURI;
    private final int qos = 2;
    private final CallBack callback;

    public MqttSub(String serverURI, String topicFilter, Context context) {
        this.topicFilter = topicFilter;
        this.serverURI = serverURI;
        callback = new CallBack(context);
    }

    public void subscribe() {
        String clientId = "ΜyClient2Android";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttAsyncClient sampleClient = new MqttAsyncClient(serverURI, clientId, persistence);
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

    class CallBack  implements MqttCallback {
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
                        // mp.stop();
                        player.release();
                    }
                });
            }

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            System.err.println("delivery complete");

        }
    }
}

