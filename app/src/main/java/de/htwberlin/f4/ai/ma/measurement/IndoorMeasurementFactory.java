package de.htwberlin.f4.ai.ma.measurement;

import android.content.Context;

/**
 * Factory returning an concrete implementation
 *
 * TODO: throw exception if not calibrated
 */

public class IndoorMeasurementFactory {

    private static IndoorMeasurement indoorMeasurement;

    public static IndoorMeasurement getIndoorMeasurement(Context context) {
        if (indoorMeasurement == null) {
            indoorMeasurement = new IndoorMeasurementImpl(context);
        }
        return indoorMeasurement;
    }
}
