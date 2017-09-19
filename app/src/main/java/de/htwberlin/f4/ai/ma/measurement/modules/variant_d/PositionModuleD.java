package de.htwberlin.f4.ai.ma.measurement.modules.variant_d;

import android.content.Context;

import de.htwberlin.f4.ai.ma.android.measure.CalibrationData;
import de.htwberlin.f4.ai.ma.measurement.modules.variant_a.PositionModuleA;

/**
 * PositionModuleD Class which implements the PositionModule Interface
 *
 * Used for IndoorMeasurementType.VARIANT_D
 *
 * Orientation: CompassSimple (Accelerometer + Magnetic field sensor)
 * Altitude: Barometer
 * Distance: Steplength
 *
 * Lowpass filter used
 *
 * Author: Benjamin Kneer
 */

public class PositionModuleD extends PositionModuleA{

    public PositionModuleD(Context context, CalibrationData calibrationData) {
        super(context, calibrationData);

        altitudeModule = new AltitudeModuleD(context, calibrationData.getAirPressure(), calibrationData.getLowpassFilterValue(), calibrationData.getBarometerThreshold());
        distanceModule = new DistanceModuleD(context, calibrationData.getStepLength());
        orientationModule = new OrientationModuleD(context, calibrationData.getLowpassFilterValue());
    }
}
