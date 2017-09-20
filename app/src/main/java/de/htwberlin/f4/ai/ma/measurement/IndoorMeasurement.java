package de.htwberlin.f4.ai.ma.measurement;


import java.util.Map;

import de.htwberlin.f4.ai.ma.android.measure.CalibrationData;
import de.htwberlin.f4.ai.ma.android.sensors.SensorData;
import de.htwberlin.f4.ai.ma.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ma.android.sensors.SensorType;
import de.htwberlin.f4.ai.ma.measurement.modules.stepdirection.StepDirectionDetectListener;

/**
 * IndoorMeasurement Interface
 *
 * Used to determine the position and handle all sensor stuff
 *
 * Calculate position in cartesian coordinate system
 *
 * Author: Benjamin Kneer
 */

public interface IndoorMeasurement {

    // calibrate steplength (m), stepperiod (ms), airpressure and some other stuff
    void calibrate(CalibrationData calibrationData);

    // start PositionModule for position calculcation
    void start();

    // stop sensors and PositionModule
    void stop();

    // start 1..x specific sensors with the specified sample rate
    void startSensors(int sensorRate, SensorType... sensorType);

    // get the coordinates in WKT FORMAT
    String getCoordinates();

    // set the listener which receives updates from sensors
    void setSensorListener(SensorListener listener);

    // set the stepdirectiondetect listener
    void setStepDirectionListener(StepDirectionDetectListener listener);

    // get the last values of every registered sensor, so we can read them
    // at a specific time
    Map<SensorType, SensorData> getLastSensorValues();
}
