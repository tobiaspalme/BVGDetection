package de.htwberlin.f4.ai.ba.coordinates.android.sensors.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorListener;

/**
 * https://developer.android.com/guide/topics/sensors/sensors_position.html
 *
 * Compass using SensorFusion with TYPE_ROTATION_VECTOR
 *
 * Sensors used: Accelerometer, Magnetometer, AND (when present) Gyroscope
 *
 * Note: Values might be off, compass calibration might be required (waving 8 form or turn
 * phone around each of its 3 axis)
 */

public class CompassImpl implements Compass, SensorEventListener {

    private SensorManager sensorManager;
    private SensorListener listener;
    private Sensor rotationSensor;

    private float[] orientation = new float[3];
    private float[] rotationMatrix = new float[9];
    private Integer azimuth;

    public CompassImpl(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void start() {
        azimuth = 0;
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void stop() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this, rotationSensor);
        }
    }

    @Override
    public Integer getValue() {
        return azimuth;
    }

    @Override
    public void setListener(SensorListener listener) {
        this.listener = listener;
    }



    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR ){
            SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values );
            // original values are within [-180,180]
            azimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rotationMatrix, orientation)[0]) + 360) % 360;

            if (listener != null) {
                listener.valueChanged(azimuth);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
