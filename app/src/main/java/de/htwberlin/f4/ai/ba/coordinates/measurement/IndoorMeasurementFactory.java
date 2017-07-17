package de.htwberlin.f4.ai.ba.coordinates.measurement;

/**
 * Factory returning an concrete implementation
 *
 * TODO: throw exception if not calibrated
 */

public class IndoorMeasurementFactory {

    public static IndoorMeasurement getIndoorMeasurement() {
        return new IndoorMeasurementImpl();
    }
}
