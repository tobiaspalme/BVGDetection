package de.htwberlin.f4.ai.ma.android.sensors;

/**
 * SensorFactory interface to create the required sensors
 *
 * Author: Benjamin Kneer
 */

public interface SensorFactory {

    // get the corret sensor
    Sensor getSensor(SensorType sensorType, int sensorRate);
}
