package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a;

import android.util.Log;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.measure.CalibrationData;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.AltitudeModule;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.DistanceModule;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.OrientationModule;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.PositionModule;

/**
 * Orientation: CompassFusion
 * Altitude: Barometer
 * Distance: Steplength
 *
 * No Lowpass filter
 */

public class PositionModuleA implements PositionModule {

    protected AltitudeModule altitudeModule;
    protected DistanceModule distanceModule;
    protected OrientationModule orientationModule;

    // coordinates[0] = x = movement left / right
    // coordinates[1] = y = movement backward / forward
    // coordinates[2] = z = movement downward / upward
    private float[] coordinates;

    public PositionModuleA(SensorFactory sensorFactory, CalibrationData calibrationData) {
        altitudeModule = new AltitudeModuleA(sensorFactory, calibrationData.getAirPressure());
        distanceModule = new DistanceModuleA(sensorFactory, calibrationData.getStepLength());
        orientationModule = new OrientationModuleA(sensorFactory);
        // set start point to 0,0,0
        coordinates = calibrationData.getCoordinates();
    }

    @Override
    public float[] calculatePosition() {
        // calculate new position with these 3 values
        float altitude = altitudeModule.getAltitude();
        float distance = distanceModule.getDistance();
        float orientation = orientationModule.getOrientation();

        // orientation stuff
        double sina = Math.sin(Math.toRadians(90 - orientation));
        double cosa = Math.cos(Math.toRadians(90 - orientation));
        float x = (float)cosa * distance;
        float y = (float)sina * distance;

        Log.d("tmp", "orientation: " + orientation);
        Log.d("tmp", "calculated x movement: " + x);
        Log.d("tmp", "calculated y movement: " + y);


        coordinates[0] += x;
        coordinates[1] += y;
        // altitude
        coordinates[2] += altitude;

        Log.d("tmp", "new x: " + coordinates[0]);
        Log.d("tmp", "new y: " + coordinates[1]);
        Log.d("tmp", "--------------------");

        return coordinates;
    }

    @Override
    public void start() {
        altitudeModule.start();
        distanceModule.start();
        orientationModule.start();
    }

    @Override
    public void stop() {
        altitudeModule.stop();
        distanceModule.stop();
        orientationModule.stop();
    }
}
