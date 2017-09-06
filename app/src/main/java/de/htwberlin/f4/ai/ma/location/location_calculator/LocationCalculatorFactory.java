package de.htwberlin.f4.ai.ma.location.location_calculator;

import android.content.Context;

/**
 * Created by Johann Winter
 */

public class LocationCalculatorFactory {

    public static LocationCalculator createInstance(Context context) {
        return new LocationCalculatorImpl(context);
    }
}
