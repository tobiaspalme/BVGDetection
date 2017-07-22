package de.htwberlin.f4.ai.ba.coordinates.android.sensors.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorListener;

/**
 * Created by benni on 21.07.2017.
 *
 * If no gyroscope is available, we just use the TYPE_ACCELEROMETER and TYPE_MAGNETIC_FIELD sensor.
 *
 * Note: the value is much more unstable than TYPE_ROTATION_VECTOR
 *
 */

public class CompassSimple implements Compass, SensorEventListener {

    private SensorManager sensorManager;
    private SensorListener listener;
    private Sensor accelerometerSensor;
    private Sensor magneticFieldSensor;

    private float[] orientation = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] accelerometerValues = new float[3];
    private float[] magneticValues = new float[3];


    private int azimuth;

    public CompassSimple(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void start() {
        azimuth = 0;
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        if ((accelerometerSensor != null) && (magneticFieldSensor != null)) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void stop() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this, accelerometerSensor);
            sensorManager.unregisterListener(this, magneticFieldSensor);
        }
    }

    @Override
    public Integer getValue() {
        return azimuth;
    }

    @Override
    public boolean isSensorAvailable() {
        if ((sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) ||
                (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)) {
            return false;
        }

        return true;
    }

    @Override
    public void setListener(SensorListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(sensorEvent.values, 0, accelerometerValues, 0, sensorEvent.values.length);
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(sensorEvent.values, 0, magneticValues, 0, sensorEvent.values.length);
        }

        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magneticValues);
        SensorManager.getOrientation(rotationMatrix, orientation);
        azimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rotationMatrix, orientation)[0]) + 360) % 360;

        if (listener != null) {
            listener.valueChanged(azimuth);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
