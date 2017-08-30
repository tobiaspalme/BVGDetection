package de.htwberlin.f4.ai.ma.measurement.modules.c;

import android.content.Context;

import de.htwberlin.f4.ai.ma.android.measure.CalibrationData;
import de.htwberlin.f4.ai.ma.measurement.modules.a.PositionModuleA;

/**
 * Orientation: CompassSimple
 * Altitude: Barometer
 * Distance: Steplength
 *
 * No Lowpass filter
 */

public class PositionModuleC extends PositionModuleA{

    public PositionModuleC(Context context, CalibrationData calibrationData) {
        super(context, calibrationData);

        altitudeModule = new AltitudeModuleC(context, calibrationData.getAirPressure(), calibrationData.getBarometerThreshold());
        distanceModule = new DistanceModuleC(context, calibrationData.getStepLength());
        orientationModule = new OrientationModuleC(context);
    }
}
