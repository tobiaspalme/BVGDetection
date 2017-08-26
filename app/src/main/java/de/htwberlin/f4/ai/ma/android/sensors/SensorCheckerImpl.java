package de.htwberlin.f4.ai.ma.android.sensors;

import android.content.Context;

import de.htwberlin.f4.ai.ma.measurement.IndoorMeasurementType;

/**
 * Created by benni on 14.08.2017.
 */

public class SensorCheckerImpl implements SensorChecker {

    private Context context;

    public SensorCheckerImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean checkSensor(IndoorMeasurementType indoorMeasurementType) {
        boolean rotation;
        boolean barometer;
        boolean magnetometer;
        boolean accelerometer;
        boolean stepcounter;

        SensorFactory sensorFactory = new SensorFactoryImpl(context);

        Sensor rotationSensor = sensorFactory.getSensor(SensorType.COMPASS_FUSION, Sensor.SENSOR_RATE_UI);
        rotation = rotationSensor.isSensorAvailable();

        Sensor barometerSensor = sensorFactory.getSensor(SensorType.BAROMETER, Sensor.SENSOR_RATE_UI);
        barometer = barometerSensor.isSensorAvailable();

        Sensor magnetometerSensor = sensorFactory.getSensor(SensorType.MAGNETIC_FIELD, Sensor.SENSOR_RATE_UI);
        magnetometer = magnetometerSensor.isSensorAvailable();

        Sensor accelerometerSensor = sensorFactory.getSensor(SensorType.ACCELEROMETER_SIMPLE, Sensor.SENSOR_RATE_UI);
        accelerometer = accelerometerSensor.isSensorAvailable();

        Sensor stepcounterSensor = sensorFactory.getSensor(SensorType.STEPCOUNTER, Sensor.SENSOR_RATE_UI);
        stepcounter = stepcounterSensor.isSensorAvailable();


        if (indoorMeasurementType == IndoorMeasurementType.VARIANT_A || indoorMeasurementType == IndoorMeasurementType.VARIANT_B) {
            if (rotation && barometer && stepcounter) {
                return true;
            } else {
                return false;
            }
        } else if (indoorMeasurementType == IndoorMeasurementType.VARIANT_C || indoorMeasurementType == IndoorMeasurementType.VARIANT_D) {
            if (magnetometer && accelerometer && barometer && stepcounter) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }
}
