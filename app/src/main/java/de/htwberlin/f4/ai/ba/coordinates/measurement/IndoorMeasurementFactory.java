package de.htwberlin.f4.ai.ba.coordinates.measurement;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;

/**
 * Factory returning an concrete implementation
 *
 * TODO: throw exception if not calibrated
 */

public class IndoorMeasurementFactory {

    private static IndoorMeasurement indoorMeasurement;

    public static IndoorMeasurement getIndoorMeasurement(SensorFactory sensorFactory) {
        if (indoorMeasurement == null) {
            indoorMeasurement = new IndoorMeasurementImpl(sensorFactory);
        }
        return indoorMeasurement;
    }
}
