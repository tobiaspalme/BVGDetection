package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.c;

import android.content.Context;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.measure.CalibrationData;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a.PositionModuleA;

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

        altitudeModule = new AltitudeModuleC(context, calibrationData.getAirPressure());
        distanceModule = new DistanceModuleC(context, calibrationData.getStepLength());
        orientationModule = new OrientationModuleC(context);
    }
}
