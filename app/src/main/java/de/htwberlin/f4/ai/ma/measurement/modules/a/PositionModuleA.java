package de.htwberlin.f4.ai.ma.measurement.modules.a;

import android.content.Context;
import android.util.Log;

import de.htwberlin.f4.ai.ma.android.measure.CalibrationData;
import de.htwberlin.f4.ai.ma.measurement.modules.AltitudeModule;
import de.htwberlin.f4.ai.ma.measurement.modules.DistanceModule;
import de.htwberlin.f4.ai.ma.measurement.modules.OrientationModule;
import de.htwberlin.f4.ai.ma.measurement.modules.PositionModule;

/**
 * Orientation: CompassFusion (Rotation Vector)
 * Altitude: Barometer
 * Distance: Steplength
 *
 * No Lowpass filter
 */

public class PositionModuleA implements PositionModule {

    protected AltitudeModule altitudeModule;
    protected DistanceModule distanceModule;
    protected OrientationModule orientationModule;
    protected Context context;
    protected CalibrationData calibrationData;

    // coordinates[0] = x = east / west
    // coordinates[1] = y = forward / backward
    // coordinates[2] = z = movement upward / downward
    private float[] coordinates;

    public PositionModuleA(Context context, CalibrationData calibrationData) {
        altitudeModule = new AltitudeModuleA(context, calibrationData.getAirPressure(), calibrationData.getBarometerThreshold());
        distanceModule = new DistanceModuleA(context, calibrationData.getStepLength());
        orientationModule = new OrientationModuleA(context);
        // set start point to 0,0,0
        coordinates = calibrationData.getCoordinates();
        this.calibrationData = calibrationData;
    }

    @Override
    public float[] calculatePosition() {

        // calculate new position with these 3 values
        float altitude = altitudeModule.getAltitude();
        float distance = distanceModule.getDistance();
        float orientation = orientationModule.getOrientation();

        if (calibrationData.isStairs()) {
            // adjusting distance when stair toggle is checked
            // since we dont know if the user goes up or down, we dont manipulate the altitude.
            // we just rely on barometer data and hope threshold is passed
            distance = 0.35f;
        }

        // prevent wrong calculcation if delta altitude is bigger than steplength
        // that can happen when a train arrives in station due to airpressure change...
        if (altitude > distance) {
            altitude = 0.0f;
        }

        // orientation stuff
        double sina = Math.sin(Math.toRadians(90 - orientation));
        double cosa = Math.cos(Math.toRadians(90 - orientation));
        // calculate rho -> altitude = delta z
        float p = (float) Math.sqrt(Math.pow(distance, 2) - Math.pow(altitude, 2));
        // calculate movement along x and y axis using the calculated rho value
        float x = (float)cosa * p;
        float y = (float)sina * p;

        coordinates[0] += x;
        coordinates[1] += y;
        coordinates[2] += altitude;

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
