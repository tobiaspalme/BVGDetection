package de.htwberlin.f4.ai.ba.coordinates.android.record;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactoryImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.accelerometer.AccelerometerLinear;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.accelerometer.AccelerometerSimple;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.barometer.Barometer;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.gravity.GravitySensor;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.gyroscope.Gyroscope;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.gyroscope.GyroscopeUncalibrated;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.magneticfield.MagneticFieldSensor;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurement;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurementFactory;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurementListener;

/**
 * Created by benni on 22.07.2017.
 */

public class RecordControllerImpl implements RecordController {

    private RecordView view;
    private IndoorMeasurement indoorMeasurement;


    @Override
    public void setView(RecordView view) {
        this.view = view;
    }

    @Override
    public void onStartClicked() {

        SensorFactory sensorFactory = new SensorFactoryImpl(view.getContext());
        indoorMeasurement = IndoorMeasurementFactory.getIndoorMeasurement(sensorFactory);
        indoorMeasurement.setListener(new IndoorMeasurementListener() {
            @Override
            public void valueChanged(float[] values, SensorType sensorType) {
                switch (sensorType) {

                    case ACCELEROMETER_SIMPLE:
                        view.updateAcceleration(values);
                        break;
                    case ACCELEROMETER_LINEAR:
                        view.updateAccelerationLinear(values);
                        break;
                    case GRAVITY:
                        view.updateGravity(values);
                        break;
                    case GYROSCOPE:
                        view.updateGyroscope(values);
                        break;
                    case GYROSCOPE_UNCALIBRATED:
                        view.updateGyroscopeUncalibrated(values);
                        break;
                    case MAGNETIC_FIELD:
                        view.updateMagneticField(values);
                        break;
                    case COMPASS_FUSION:
                        view.updateCompassFusion((int) values[0]);
                        break;
                    case COMPASS_SIMPLE:
                        view.updateCompassSimple((int) values[0]);
                        break;
                    case BAROMETER:
                        view.updatePressure(values[0]);
                        break;
                    default:
                        break;

                }
            }
        });

        indoorMeasurement.start(SensorType.ACCELEROMETER_SIMPLE,
                                SensorType.ACCELEROMETER_LINEAR,
                                SensorType.GRAVITY,
                                SensorType.GYROSCOPE,
                                SensorType.GYROSCOPE_UNCALIBRATED,
                                SensorType.MAGNETIC_FIELD,
                                SensorType.COMPASS_FUSION,
                                SensorType.COMPASS_SIMPLE,
                                SensorType.BAROMETER);


    }

    @Override
    public void onStopClicked() {
        if (indoorMeasurement != null) {
            indoorMeasurement.stop();
        }
    }

    @Override
    public void onPause() {
        if (indoorMeasurement != null) {
            indoorMeasurement.stop();
        }
    }


}
