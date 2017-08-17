package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.b;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.measure.CalibrationData;
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

        altitudeModule = new AltitudeModuleB(sensorFactory, calibrationData.getAirPressure(), calibrationData.getLowpassFilterValue());
        distanceModule = new DistanceModuleB(sensorFactory, calibrationData.getStepLength());
        orientationModule = new OrientationModuleB(sensorFactory, calibrationData.getLowpassFilterValue());
    }

}
