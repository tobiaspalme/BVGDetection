package de.htwberlin.f4.ai.ma.android.sensors;

import de.htwberlin.f4.ai.ma.measurement.IndoorMeasurementType;

/**
 * Created by benni on 14.08.2017.
 */

public interface SensorChecker {

    boolean checkSensor(IndoorMeasurementType indoorMeasurementType);
}
