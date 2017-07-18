package de.htwberlin.f4.ai.ba.coordinates.measurement;

/**
 * Factory returning an concrete implementation
 *
 * TODO: throw exception if not calibrated
 */

public class IndoorMeasurementFactory {

    private static IndoorMeasurement indoorMeasurement;

    public static IndoorMeasurement getIndoorMeasurement() {
        if (indoorMeasurement == null) {
            indoorMeasurement = new IndoorMeasurementImpl();
        }
        return indoorMeasurement;
    }
}
