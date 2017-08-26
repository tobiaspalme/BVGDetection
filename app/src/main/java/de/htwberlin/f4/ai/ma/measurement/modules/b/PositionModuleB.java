package de.htwberlin.f4.ai.ma.measurement.modules.b;

import android.content.Context;

import de.htwberlin.f4.ai.ma.android.measure.CalibrationData;
import de.htwberlin.f4.ai.ma.measurement.modules.a.PositionModuleA;

/**
 * Orientation: CompassFusion
 * Altitude: Barometer
 * Distance: Steplength
 *
 * Lowpass filter
 */

public class PositionModuleB extends PositionModuleA {

    public PositionModuleB(Context context, CalibrationData calibrationData) {
        super(context, calibrationData);

        altitudeModule = new AltitudeModuleB(context, calibrationData.getAirPressure(), calibrationData.getLowpassFilterValue());
        distanceModule = new DistanceModuleB(context, calibrationData.getStepLength());
        orientationModule = new OrientationModuleB(context, calibrationData.getLowpassFilterValue());
    }

}
