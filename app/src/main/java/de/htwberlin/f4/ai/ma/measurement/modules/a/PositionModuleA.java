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
 * PositionModuleA Class which implements the PositionModule Interface
 *
 * Orientation: CompassFusion (Rotation Vector)
 * Altitude: Barometer
 * Distance: Steplength
 *
 * No Lowpass filter
 *
 * Author: Benjamin Kneer
 */

public class PositionModuleA implements PositionModule {

    protected AltitudeModule altitudeModule;
    protected DistanceModule distanceModule;
    protected OrientationModule orientationModule;
    protected Context context;
    protected CalibrationData calibrationData;

    // coordinates[0] = x = east / west
    // coordinates[1] = y = north / south
    // coordinates[2] = z = movement upward / downward
    private float[] coordinates;


    public PositionModuleA(Context context, CalibrationData calibrationData) {
        altitudeModule = new AltitudeModuleA(context, calibrationData.getAirPressure(), calibrationData.getBarometerThreshold());
        distanceModule = new DistanceModuleA(context, calibrationData.getStepLength());
        orientationModule = new OrientationModuleA(context);
        coordinates = calibrationData.getCoordinates();
        this.calibrationData = calibrationData;
        this.context = context;
    }


    /************************************************************************************
    *                                                                                   *
    *                              Interface Methods                                    *
    *                                                                                   *
    *************************************************************************************/


    /**
     * calculate new position based on previous coordinates
     *
     * @return new calculated cartesian coordinates
     */
    @Override
    public float[] calculatePosition() {

        // calculate new position with these 3 values
        float altitude = altitudeModule.getAltitude();
        float distance = distanceModule.getDistance(calibrationData.isStairs());
        float orientation = orientationModule.getOrientation();

        //Toast toast = Toast.makeText(context, "Orientation: " + orientation, Toast.LENGTH_SHORT);
        //toast.show();

        /*
        // orientation stuff
        double sina = Math.sin(Math.toRadians(90 - orientation));
        double cosa = Math.cos(Math.toRadians(90 - orientation));
        // calculate distance -> altitude = delta z
        float p = (float) Math.sqrt(Math.pow(distance, 2) - Math.pow(altitude, 2));
        // calculate movement along x and y axis using the calculated rho value
        float x = (float)cosa * p;
        float y = (float)sina * p;
        */

        // calculate spherical coordinates and transform them to cartesian coordinates
        double sina = Math.sin(Math.toRadians(90 - orientation));
        double cosa = Math.cos(Math.toRadians(90 - orientation));
        // calculate inclination angle
        double phi = Math.toDegrees(Math.asin(altitude / distance));
        double cosphi = Math.cos(Math.toRadians(phi));

        float x = (float) (distance * cosphi * cosa);
        float y = (float) (distance * cosphi * sina);

        coordinates[0] += x;
        coordinates[1] += y;
        coordinates[2] += altitude;

        return coordinates;
    }


    /**
     * start all modules
     */
    @Override
    public void start() {
        altitudeModule.start();
        distanceModule.start();
        orientationModule.start();
    }


    /**
     * stop all modules
     */
    @Override
    public void stop() {
        altitudeModule.stop();
        distanceModule.stop();
        orientationModule.stop();
    }
}
