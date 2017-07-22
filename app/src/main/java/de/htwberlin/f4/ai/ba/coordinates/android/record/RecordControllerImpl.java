package de.htwberlin.f4.ai.ba.coordinates.android.record;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.accelerometer.Accelerometer;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.accelerometer.AccelerometerLinear;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.accelerometer.AccelerometerSimple;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.accelerometer.AccelerometerListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.barometer.Barometer;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.barometer.BarometerImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.barometer.BarometerListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.compass.Compass;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.compass.CompassImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.compass.CompassListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.compass.CompassSimple;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.gravity.GravitySensor;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.gravity.GravitySensorImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.gravity.GravitySensorListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.gyroscope.Gyroscope;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.gyroscope.GyroscopeImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.gyroscope.GyroscopeListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.gyroscope.GyroscopeUncalibrated;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.magneticfield.MagneticFieldSensor;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.magneticfield.MagneticFieldSensorImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.magneticfield.MagneticFieldSensorListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.temperature.Thermometer;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.temperature.ThermometerImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.temperature.ThermometerListener;

/**
 * Created by benni on 22.07.2017.
 */

public class RecordControllerImpl implements RecordController {

    private RecordView view;
    private Accelerometer accelerometer;
    private Accelerometer accelerometerLinear;
    private GravitySensor gravitySensor;
    private Gyroscope gyroscope;
    private Gyroscope gyroscopeUncalibrated;
    private MagneticFieldSensor magneticFieldSensor;
    private Compass compassFusion;
    private Compass compassSimple;
    private Barometer barometer;


    @Override
    public void setView(RecordView view) {
        this.view = view;
    }

    @Override
    public void onStartClicked() {
        accelerometer = new AccelerometerSimple(view.getContext());
        accelerometer.setListener(new AccelerometerListener() {
            @Override
            public void valueChanged(float[] newValue) {
                view.updateAcceleration(newValue);
            }
        });
        accelerometer.start();

        accelerometerLinear = new AccelerometerLinear(view.getContext());
        accelerometerLinear.setListener(new AccelerometerListener() {
            @Override
            public void valueChanged(float[] newValue) {
                view.updateAccelerationLinear(newValue);
            }
        });
        accelerometerLinear.start();

        gravitySensor = new GravitySensorImpl(view.getContext());
        gravitySensor.setListener(new GravitySensorListener() {
            @Override
            public void valueChanged(float[] newValue) {
                view.updateGravity(newValue);
            }
        });
        gravitySensor.start();

        gyroscope = new GyroscopeImpl(view.getContext());
        gyroscope.setListener(new GyroscopeListener() {
            @Override
            public void valueChanged(float[] newValue) {
                view.updateGyroscope(newValue);
            }
        });
        gyroscope.start();

        gyroscopeUncalibrated = new GyroscopeUncalibrated(view.getContext());
        gyroscopeUncalibrated.setListener(new GyroscopeListener() {
            @Override
            public void valueChanged(float[] newValue) {
                view.updateGyroscopeUncalibrated(newValue);
            }
        });
        gyroscopeUncalibrated.start();

        magneticFieldSensor = new MagneticFieldSensorImpl(view.getContext());
        magneticFieldSensor.setListener(new MagneticFieldSensorListener() {
            @Override
            public void valueChanged(float[] newValue) {
                view.updateMagneticField(newValue);
            }
        });
        magneticFieldSensor.start();

        compassFusion = new CompassImpl(view.getContext());
        compassFusion.setListener(new CompassListener() {
            @Override
            public void valueChanged(Integer newValue) {
                view.updateCompassFusion(newValue);
            }
        });
        compassFusion.start();

        compassSimple = new CompassSimple(view.getContext());
        compassSimple.setListener(new CompassListener() {
            @Override
            public void valueChanged(Integer newValue) {
                view.updateCompassSimple(newValue);
            }
        });
        compassSimple.start();

        barometer = new BarometerImpl(view.getContext());
        barometer.setListener(new BarometerListener() {
            @Override
            public void valueChanged(Float newValue) {
                view.updatePressure(newValue);
            }
        });
        barometer.start();
    }

    @Override
    public void onStopClicked() {
        stopSensors();
    }

    @Override
    public void onPause() {
        stopSensors();
    }

    private void stopSensors() {
        if (accelerometer != null) {
            accelerometer.stop();
        }
        if (accelerometerLinear != null) {
            accelerometerLinear.stop();
        }
        if (gravitySensor != null) {
            gravitySensor.stop();
        }
        if (gyroscope != null) {
            gyroscope.stop();
        }
        if (gyroscopeUncalibrated != null) {
            gyroscopeUncalibrated.stop();
        }
        if (magneticFieldSensor != null) {
            magneticFieldSensor.stop();
        }
        if (compassFusion != null) {
            compassFusion.stop();
        }
        if (compassSimple != null) {
            compassSimple.stop();
        }
        if (barometer != null) {
            barometer.stop();
        }
    }
}
