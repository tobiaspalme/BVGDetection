package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a;

import android.util.Log;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.measurement.CalibrationData;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.AltitudeModule;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.DistanceModule;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.OrientationModule;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.PositionModule;

/**
 * Created by benni on 03.08.2017.
 */

public class PositionModuleImpl implements PositionModule {

    private AltitudeModule altitudeModule;
    private DistanceModule distanceModule;
    private OrientationModule orientationModule;

    // coordinates[0] = x = movement left / right
    // coordinates[1] = y = movement forward / backward
    // coordinates[2] = z = movement upward / downward
    private float[] coordinates;

    public PositionModuleImpl(SensorFactory sensorFactory, CalibrationData calibrationData) {
        altitudeModule = new AltitudeModuleImpl(sensorFactory, calibrationData.getAirPressure());
        distanceModule = new DistanceModuleImpl(sensorFactory, calibrationData.getStepLength());
        orientationModule = new OrientationModuleImpl(sensorFactory, calibrationData.getAzimuth());
        // set start point to 0,0,0
        coordinates = new float[]{0.0f, 0.0f, 0.0f};
    }

    @Override
    public float[] calculatePosition() {
        // calculate new position with every step
        // berechnung vielleicht in thread auslagern?

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
        Log.d("tmp", "new x: " + coordinates[0]);
        Log.d("tmp", "new y: " + coordinates[1]);
        Log.d("tmp", "--------------------");
        // altitude
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
