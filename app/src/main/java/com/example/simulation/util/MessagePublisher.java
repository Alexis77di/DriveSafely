package com.example.simulation.util;


import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MessagePublisher {

    private final Context context;

    public MessagePublisher(Context context) {
        this.context = context;
    }


    public void publish() {
        final String topic = "MQTT Examples";
        final String content = "Message from MqttPublishSample";
        final int qos = 2;
        String broker = "tcp://192.168.1.8:1883";
        String clientId = "JavaSample";
        MemoryPersistence persistence = new MemoryPersistence();

        final MqttAndroidClient sampleClient = new MqttAndroidClient(context, broker, clientId, persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        System.out.println("Connecting to broker: " + broker);
        try {
            sampleClient.connect(connOpts, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Connected");
                    System.out.println("Publishing message: " + content);
                    MqttMessage message = new MqttMessage(content.getBytes());
                    message.setQos(qos);
                    try {
                        sampleClient.publish(topic, message);
                        System.out.println("Message published");
                        sampleClient.disconnect();
                    } catch (MqttException me) {
                        System.out.println("reason " + me.getReasonCode());
                        System.out.println("msg " + me.getMessage());
                        System.out.println("loc " + me.getLocalizedMessage());
                        System.out.println("cause " + me.getCause());
                        System.out.println("excep " + me);
                        me.printStackTrace();
                    }
                    System.out.println("Disconnected");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    System.out.println(exception.toString());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }

}
