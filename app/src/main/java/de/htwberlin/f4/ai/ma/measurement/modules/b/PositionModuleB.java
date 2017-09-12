package de.htwberlin.f4.ai.ma.measurement.modules.b;

import android.content.Context;

import de.htwberlin.f4.ai.ma.android.measure.CalibrationData;
import de.htwberlin.f4.ai.ma.measurement.modules.a.PositionModuleA;

/**
 * PositionModuleB Class which implements the PositionModule Interface
 *
 * Used for IndoorMeasurementType.VARIANT_B
 *
 * Orientation: CompassFusion (Rotation Vector)
 * Altitude: Barometer
 * Distance: Steplength
 *
 * Lowpass filter used (weighted smoothing)
 *
 * Author: Benjamin Kneer
 */

public class PositionModuleB extends PositionModuleA {

    public PositionModuleB(Context context, CalibrationData calibrationData) {
        super(context, calibrationData);

        altitudeModule = new AltitudeModuleB(context, calibrationData.getAirPressure(), calibrationData.getLowpassFilterValue(), calibrationData.getBarometerThreshold());
        distanceModule = new DistanceModuleB(context, calibrationData.getStepLength());
        orientationModule = new OrientationModuleB(context, calibrationData.getLowpassFilterValue());
    }

}
