package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.d;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.measure.CalibrationData;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a.PositionModuleA;

/**
 * Orientation: CompassSimple
 * Altitude: Barometer
 * Distance: Steplength
 *
 * Lowpass filter
 */

public class PositionModuleD extends PositionModuleA{

    public PositionModuleD(SensorFactory sensorFactory, CalibrationData calibrationData) {
        super(sensorFactory, calibrationData);

        altitudeModule = new AltitudeModuleD(sensorFactory, calibrationData.getAirPressure());
        distanceModule = new DistanceModuleD(sensorFactory, calibrationData.getStepLength());
        orientationModule = new OrientationModuleD(sensorFactory);
    }
}
