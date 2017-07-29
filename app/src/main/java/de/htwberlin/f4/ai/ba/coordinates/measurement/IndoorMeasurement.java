package de.htwberlin.f4.ai.ba.coordinates.measurement;


import java.util.List;
import java.util.Map;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorData;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;

/**
 * Simple interface for the Indoor Measurement.
 * Responsible for all sensor stuff.
 */

public interface IndoorMeasurement {

    // calibrate air pressure, steep length
    void calibrate();

    // start recording for postion calculcation
    void start(IndoorMeasurementType indoorMeasurementType);

    // stop sensors
    void stop();

    // start specific sensors
    void startSensors(SensorType... sensorType);

    // get the relative coordinates
    String getCoordinates();

    // set the listener which receives updates from sensors
    void setListener(IndoorMeasurementListener listener);

    // get the last values of every registered sensor, so we can read them
    // at a specific time. That's required because every sensor got a different
    // interval.
    Map<SensorType, SensorData> getLastSensorValues();
}
