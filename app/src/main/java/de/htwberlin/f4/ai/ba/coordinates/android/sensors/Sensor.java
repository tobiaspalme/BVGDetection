package de.htwberlin.f4.ai.ba.coordinates.android.sensors;

/**
 * Common interface for a sensor
 */

public interface Sensor<T> {

    void start();
    void stop();
    T getValue();
    boolean isSensorAvailable();
    void setListener(SensorListener listener);
}
