package com.example.simulation.util;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Date;

public class MqttSubscriber {
    private final String topic;
    private final MqttAndroidClient sampleClient;

    public MqttSubscriber(Context context, String topic, String broker) {

        this.topic = topic;

        String clientId = "ΜyClient2Android";
        MemoryPersistence persistence = new MemoryPersistence();


        sampleClient = new MqttAndroidClient(context, broker, clientId, persistence);
        sampleClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String time = new Date().toString();
                System.out.println("Time:\t" + time + "Topic\t" + topic + "Message:\t" + new String(message.getPayload()) + " Qos:\t" + message.getQos());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }

    public void connect() throws MqttException, InterruptedException {


        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        System.out.println("Connecting to broker");
        sampleClient.connect(connOpts);
        System.out.println("Connected");
        Thread.sleep(1000);

    }

    public void subscribe() throws MqttException {
        int qos = 2;
        sampleClient.subscribe(topic, qos);
        System.out.println("Subscribed");


    }

    public void disconnect() throws MqttException {
        sampleClient.disconnect();
        System.out.println("Disconnected");
    }

}
