package de.htwberlin.f4.ai.ma.measurement.modules.variant_c;

import android.content.Context;

import de.htwberlin.f4.ai.ma.android.measure.CalibrationData;
import de.htwberlin.f4.ai.ma.measurement.modules.variant_a.PositionModuleA;

/**
 * PositionModuleC Class which implements the PositionModule Interface
 *
 * Used for IndoorMeasurementType.VARIANT_C
 *
 * Orientation: CompassSimple (Accelerometer + Magnetic field sensor)
 * Altitude: Barometer
 * Distance: Steplength
 *
 * No Lowpass filter used
 *
 * Author: Benjamin Kneer
 */

public class PositionModuleC extends PositionModuleA{

    public PositionModuleC(Context context, CalibrationData calibrationData) {
        super(context, calibrationData);

        altitudeModule = new AltitudeModuleC(context, calibrationData.getAirPressure(), calibrationData.getBarometerThreshold());
        distanceModule = new DistanceModuleC(context, calibrationData.getStepLength());
        orientationModule = new OrientationModuleC(context);
    }
}
