# Automated Watering System using MQTT

## Overview
This project involves the implementation of an automated watering system for agricultural farms. The system relies on sensor data to determine when to water the crops and controls the water pump's operation based on the received commands. The communication between sensors, actuators, and the central system is facilitated through the MQTT (Message Queuing Telemetry Transport) protocol using a Vernemq broker.

## Components
- **Sensors**: Real-time sensors provide readings of humidity and temperature.
- **Actuators**: Responsible for controlling the water pump's operation based on commands received.
- **Vernemq Broker**: Facilitates communication between sensors, actuators, and the central system.

## Tasks
### 1. Subscribe to Sensor Data
- **Topic**: D2S/SA/V1/digitest-A
- **Data Format**: 0-T:25;1-H:45
- Explanation: 
  - 0-T:25: Temperature is 25°C
  - 1-H:45: Humidity is 45%

### 2. Configure Threshold Values
- **Temperature and Humidity**: Set threshold values for temperature and humidity. If readings exceed the configured values, send a turn-on command to the actuator.
- **Actuator Topic**: S2D/SA/V1/contest-A/A/0
- **Data Format**: OM:1,TO:15
- Explanation:
  - OM:1: Turn ON
  - TO:15: Timeout for 15 minutes

### 3. Monitor Actuator Status and Battery Level
- **Actuator Topic**: D2S/SA/V1/contest-A
- **Data Format**: 0-B:295*0-S:1;1-S:0;2-S:0
- Explanation:
  - 0-B:295: Battery level of actuators is 295
  - 0-S:1: Actuator status (0 - OFF, 1 - ON)
- Print warning messages if:
  - Actuator status changes.
  - Battery level goes below the configured threshold.

## VerneMQ Broker Details
- **Server**: 20.106.179.116
- **Port**: 1883
- **User**: server-mqtt
- **Password**: server-mqtt

## Usage
1. Clone the repository.
2. Configure threshold values for temperature, humidity, and battery level.
3. Run the program to subscribe to sensor data and actuator commands.
4. Monitor the system for status changes and warnings.
