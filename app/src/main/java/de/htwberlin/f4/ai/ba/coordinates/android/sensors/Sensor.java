package de.htwberlin.f4.ai.ba.coordinates.android.sensors;

/**
 * Created by benni on 23.07.2017.
 */

public interface Sensor {

    void start();
    void stop();
    SensorData getValues();
    boolean isSensorAvailable();
    void setListener(SensorListener listener);
    SensorType getSensorType();
}
