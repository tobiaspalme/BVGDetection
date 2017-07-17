package de.htwberlin.f4.ai.ba.coordinates.measurement;


/**
 * Simple interface for the Indoor Measurement.
 */

public interface IndoorMeasurement {

    // calibrate air pressure
    void calibrate();

    // start recording steps, direction and air pressure
    void start();

    // stop recording
    void stop();

    // get the relative coordinates
    String getCoordinates();
}
