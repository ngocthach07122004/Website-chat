package com.dadn.demo;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ToggleController {

    private static final String BROKER_URL = "tcp://io.adafruit.com:1883";
    private static final String ADAFRUIT_USERNAME = "diavolo";
    private static final String ADAFRUIT_AIO_KEY = "aio_AnXx61cSx9X0H2v76WrW92dOXFYY"; // Replace with your Adafruit IO key
    private static final String FEED_TOPIC = ADAFRUIT_USERNAME + "/feeds/lightkey";

    @GetMapping("/toggle")
    public ResponseEntity<String> toggle(@RequestParam("state") String state) {
        try {
            // Create and connect the MQTT client
            MqttClient client = new MqttClient(BROKER_URL, MqttClient.generateClientId());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(ADAFRUIT_USERNAME);
            options.setPassword(ADAFRUIT_AIO_KEY.toCharArray());
            client.connect(options);
            
            // Publish the command ("1" for ON, "0" for OFF)
            MqttMessage message = new MqttMessage(state.getBytes());
            message.setQos(0);
            client.publish(FEED_TOPIC, message);
            
            client.disconnect();
            return ResponseEntity.ok("Toggle set to " + state);
        } catch (MqttException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error: " + e.getMessage());
        }
    }
}
