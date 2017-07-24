package de.htwberlin.f4.ai.ba.coordinates.measurement;


import java.util.Map;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;

/**
 * Simple interface for the Indoor Measurement.
 * Responsible for all sensor stuff.
 */

public interface IndoorMeasurement {

    // calibrate air pressure, steep length
    void calibrate();

    // start sensors, multiple sensors can be used at once
    void start(SensorType... sensorType);

    // stop sensors
    void stop();

    // get the relative coordinates
    String getCoordinates();

    // set the listener which receives updates from sensors
    void setListener(IndoorMeasurementListener listener);

    // get the last values of every registered sensor, so we can read them
    // at a specific time. That's required because every sensor got a different
    // interval.
    Map<SensorType, float[]> getSensorValues();
}
