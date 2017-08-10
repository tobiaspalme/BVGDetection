package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.c;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.measurement.CalibrationData;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a.PositionModuleA;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.b.AltitudeModuleB;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.b.DistanceModuleB;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.b.OrientationModuleB;

/**
 * Orientation: CompassSimple
 * Altitude: Barometer
 * Distance: Steplength
 *
 * No Lowpass filter
 */

public class PositionModuleC extends PositionModuleA{

    public PositionModuleC(SensorFactory sensorFactory, CalibrationData calibrationData) {
        super(sensorFactory, calibrationData);

        altitudeModule = new AltitudeModuleC(sensorFactory, calibrationData.getAirPressure());
        distanceModule = new DistanceModuleC(sensorFactory, calibrationData.getStepLength());
        orientationModule = new OrientationModuleC(sensorFactory, calibrationData.getAzimuth());
    }
}
