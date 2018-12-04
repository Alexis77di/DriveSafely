package com.example.simulation.util;

import android.content.Context;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Date;

public class MqttSub implements MqttCallback {
    public Context context;

    public void main(Context context) {
        this.context = context;

        String topic = "#";
        int qos = 2;
        String broker = "tcp://localhost:1883";
        String clientId = "JavaSampleSubscriber";
        MemoryPersistence persistence = new MemoryPersistence();


        try {
            //Connect client to MQTT Broker
            MqttAsyncClient sampleClient = new MqttAsyncClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            //Set callback

            sampleClient.setCallback(new MqttSub());
            System.out.println("Connecting to broker:" + broker);

            sampleClient.connect(connOpts);
            System.out.println("Connected");
            Thread.sleep(1000);
            sampleClient.subscribe(topic, qos);
            System.out.println("Subscribed");

        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
            me.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void connectionLost(Throwable cause) {
        System.out.println("Connection lost!" + cause);
        System.exit(1);
    }

    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    public void messageArrived(String topic, MqttMessage message) {
        String time = new Date().toString();
        System.out.println("Time:\t" + time + " Topic\t" + topic);
    }
}