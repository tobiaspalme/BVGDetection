package de.htwberlin.f4.ai.ma.measurement;

import android.content.Context;

/**
 * IndoorMeasurementFactory Class
 *
 * Create IndoorMeasurement Instance
 *
 * Author: Benjamin Kneer
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
