package de.htwberlin.f4.ai.ma.android.sensors;

/**
 * Created by benni on 23.07.2017.
 */

public interface Sensor {

    int SENSOR_RATE_UI = 75;
    int SENSOR_RATE_MEASUREMENT = 20;
    int SENSOR_RATE_FASTEST = 0;


    void start();
    void stop();
    SensorData getValues();
    boolean isSensorAvailable();
    void setListener(SensorListener listener);
    SensorType getSensorType();

}
