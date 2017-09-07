package de.htwberlin.f4.ai.ma.measurement.modules.a;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
        this.context = context;
    }

    @Override
    public float[] calculatePosition() {

        // calculate new position with these 3 values
        //float altitude = altitudeModule.getAltitude();
        float altitude = 0f;
        float distance = distanceModule.getDistance(calibrationData.isStairs());
        float orientation = orientationModule.getOrientation();

        //Toast toast = Toast.makeText(context, "Orientation: " + orientation, Toast.LENGTH_SHORT);
        //toast.show();


        // orientation stuff
        double sina = Math.sin(Math.toRadians(90 - orientation));
        double cosa = Math.cos(Math.toRadians(90 - orientation));
        // calculate distance -> altitude = delta z
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
