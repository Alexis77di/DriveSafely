package com.example.simulation.util;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttPublisher {

    private final String serverURI;
    private final String topicFilter;

    public MqttPublisher(String serverURI, String topicFilter) {
        this.serverURI = serverURI;
        this.topicFilter = topicFilter;
    }

    public void main(String content) {
        int qos = 2;
        String clientId = "ΜyclientidAndroid";
        MemoryPersistence persistence = new MemoryPersistence();


        try {
            MqttClient sampleClient = new MqttClient(serverURI, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + serverURI);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            System.out.println("Publishing message: " + content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);

            sampleClient.publish(topicFilter, message);
            System.out.println("Message published");


            //  sampleClient.disconnect();
            //System.out.println("Disconnected");

        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

}
