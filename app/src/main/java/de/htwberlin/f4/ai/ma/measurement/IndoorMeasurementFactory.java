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

    public static IndoorMeasurement getIndoorMeasurement(Context context) {
        return new IndoorMeasurementImpl(context);
    }
}
