package de.htwberlin.f4.ai.ba.coordinates.measurement;


import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;

/**
 * Simple interface for the Indoor Measurement.
 */

public interface IndoorMeasurement {

    // calibrate air pressure
    void calibrate();

    // start recording steps, direction and air pressure
    void start(SensorType... sensorType);

    // stop recording
    void stop();

    // get the relative coordinates
    String getCoordinates();

    void setListener(IndoorMeasurementListener listener);
}
