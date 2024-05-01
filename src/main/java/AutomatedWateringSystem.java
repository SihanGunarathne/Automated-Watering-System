import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutomatedWateringSystem {
    private static final String BROKER = "tcp://20.106.179.116:1883";
    private static final String USERNAME = "server-mqtt";
    private static final String PASSWORD = "server-mqtt";
    private static final String CLIENT_ID = "bootcamp-29";

    private static final String SENSOR_TOPIC = "D2S/SA/V1/digitest-A";
//    private static final String ACTUATOR_SUB_TOPIC = "S2D/SA/V1/contest-A/A/0";
//    private static final String ACTUATOR_PUB_TOPIC = "D2S/SA/V1/contest-A";
    private static final String ACTUATOR_SUB_TOPIC = "D2S/SA/V1/contest-B";
    private static final String ACTUATOR_PUB_TOPIC = "S2D/SA/V1/contest-A/A/0";

    private static final int TEMPERATURE_THRESHOLD = 30;
    private static final int HUMIDITY_THRESHOLD = 60;
    private static final int BATTERY_THRESHOLD = 250;

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleWithFixedDelay(() -> reconnect(), 0, 1, TimeUnit.MINUTES);
    }

    private static void reconnect() {
        try {
            MqttClient client = new MqttClient(BROKER, CLIENT_ID, new MemoryPersistence());

            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setUserName(USERNAME);
            connectOptions.setPassword(PASSWORD.toCharArray());

            client.connect(connectOptions);
//            System.out.println("Connected to broker: " + BROKER);

            client.subscribe(SENSOR_TOPIC, (topic, message) -> {
                String data = new String(message.getPayload());
//                System.out.println("Received sensor data: " + data);
                processData(data, client);
            });

            client.subscribe(ACTUATOR_SUB_TOPIC, (topic, message) -> {
                String data = new String(message.getPayload());
                processActuatorData(data);
//                System.out.println("Received actuator data: " + data);
            });
        } catch (MqttException e) {
            System.err.println("MQTT error occurred: " + e.getMessage());
        }
    }


    private static void processData(String data, MqttClient client) {
        try {
            String[] sensorData = data.split(";");

            for (String reading : sensorData) {
                String[] parts = reading.split(":");
                int value = Integer.parseInt(parts[1]);

                if (parts[0].equals("0-T") && value > TEMPERATURE_THRESHOLD) {
                    turnOnActuator(client);
                } else if (parts[0].equals("1-H") && value > HUMIDITY_THRESHOLD) {
                    turnOnActuator(client);
                } else {
                    turnOffActuator(client);
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing sensor data: " + e.getMessage());
        }
    }

    private static void processActuatorData(String data) {
        try {
            String[] parts = data.split(";");

            for (String part : parts) {
                String[] values = part.split(":");
                if (values[0].equals("0-B")) {
                    int batteryLevel = Integer.parseInt(values[1].split("\\*")[0]);
                    if (batteryLevel < BATTERY_THRESHOLD) {
                        System.out.println("Warning: Actuator battery level is low!");
                    }

                    if (Integer.parseInt(values[2]) == 1) {
                        System.out.println("Warning: 0th Actuator is on!");
                    } else {
                        System.out.println("Warning: 0th Actuator is off!");
                    }

                } else if (values[0].equals("1-S")) {
                    if (Integer.parseInt(values[1]) == 1) {
                        System.out.println("Warning: 1st Actuator is on!");
                    } else {
                        System.out.println("Warning: 1st Actuator is off!");
                    }
                } else if (values[0].equals("2-S")) {
                    if (Integer.parseInt(values[1]) == 1) {
                        System.out.println("Warning: 2nd Actuator is on!");
                    } else {
                        System.out.println("Warning: 2nd Actuator is off!");
                    }
                }

            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing actuator data: " + e.getMessage());
        }
    }

    private static void turnOnActuator(MqttClient client) {
        try {
            String message = "OM:1,TO:15";
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            client.publish(ACTUATOR_PUB_TOPIC, mqttMessage);
        } catch (MqttException e) {
            System.err.println("Error publishing actuator command: " + e.getMessage());
        }
    }

    private static void turnOffActuator(MqttClient client) {
        try {
            String message = "OM:0,TO:15";
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            client.publish(ACTUATOR_PUB_TOPIC, mqttMessage);
        } catch (MqttException e) {
            System.err.println("Error publishing actuator command: " + e.getMessage());
        }
    }
}
