package de.htwberlin.f4.ai.ba.coordinates.measurement;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;

/**
 * Created by benni on 23.07.2017.
 */

public interface IndoorMeasurementListener {

    void valueChanged(float[] values, SensorType sensorType);
}
