package de.htwberlin.f4.ai.ma.measurement;


import java.util.Map;

import de.htwberlin.f4.ai.ma.android.measure.CalibrationData;
import de.htwberlin.f4.ai.ma.android.sensors.SensorData;
import de.htwberlin.f4.ai.ma.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ma.android.sensors.SensorType;
import de.htwberlin.f4.ai.ma.measurement.modules.stepdirection.StepDirectionDetectListener;

/**
 * Simple interface for the Indoor Measurement.
 * Responsible for all sensor stuff.
 */

public interface IndoorMeasurement {

    // calibrate steplength (m), stepperiod (ms), airpressure
    void calibrate(CalibrationData calibrationData);

    // start recording for postion calculcation
    void start();

    // stop sensors
    void stop();

    // start specific sensors
    void startSensors(int sensorRate, SensorType... sensorType);

    // get the relative coordinates in WKT FORMAT
    String getCoordinates();

    // set the listener which receives updates from sensors
    void setSensorListener(SensorListener listener);

    void setStepDirectionListener(StepDirectionDetectListener listener);

    // get the last values of every registered sensor, so we can read them
    // at a specific time. That's required because every sensor got a different
    // interval.
    Map<SensorType, SensorData> getLastSensorValues();
}
