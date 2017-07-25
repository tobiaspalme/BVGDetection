package de.htwberlin.f4.ai.ba.coordinates.android.sensors;

import android.content.Context;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.accelerometer.AccelerometerLinear;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.accelerometer.AccelerometerSimple;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.barometer.Barometer;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.compass.CompassFusion;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.compass.CompassSimple;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.gravity.GravitySensor;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.gyroscope.Gyroscope;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.gyroscope.GyroscopeUncalibrated;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.magneticfield.MagneticFieldSensor;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.stepcounter.StepCounter;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.temperature.Thermometer;

/**
 * Created by benni on 23.07.2017.
 */

public class SensorFactoryImpl implements SensorFactory{

    private Context context;

    public SensorFactoryImpl(Context context) {
        this.context = context;
    }

    @Override
    public Sensor getSensor(SensorType sensorType) {

        switch (sensorType) {
            case ACCELEROMETER_LINEAR:
                return new AccelerometerLinear(context);
            case ACCELEROMETER_SIMPLE:
                return new AccelerometerSimple(context);
            case BAROMETER:
                return new Barometer(context);
            case COMPASS_FUSION:
                return new CompassFusion(context);
            case COMPASS_SIMPLE:
                return new CompassSimple(context);
            case GRAVITY:
                return new GravitySensor(context);
            case GYROSCOPE:
                return new Gyroscope(context);
            case GYROSCOPE_UNCALIBRATED:
                return new GyroscopeUncalibrated(context);
            case MAGNETIC_FIELD:
                return new MagneticFieldSensor(context);
            case STEPCOUNTER:
                return new StepCounter(context);
            case THERMOMETER:
                return new Thermometer(context);
            default:
                return null;
        }
    }
}
