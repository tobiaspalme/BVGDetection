package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.b;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.measurement.CalibrationData;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.PositionModule;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a.DistanceModuleA;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a.PositionModuleA;

/**
 * Orientation: CompassFusion
 * Altitude: Barometer
 * Distance: Steplength
 *
 * Lowpass filter
 */

public class PositionModuleB extends PositionModuleA {

    public PositionModuleB(SensorFactory sensorFactory, CalibrationData calibrationData) {
        super(sensorFactory, calibrationData);

        altitudeModule = new AltitudeModuleB(sensorFactory, calibrationData.getAirPressure());
        distanceModule = new DistanceModuleB(sensorFactory, calibrationData.getStepLength());
        orientationModule = new OrientationModuleB(sensorFactory, calibrationData.getAzimuth());
    }

}
