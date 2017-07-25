package de.htwberlin.f4.ai.ba.coordinates.android.sensors.accelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;

/**
 * Created by benni on 22.07.2017.
 */

public class AccelerometerSimple implements SensorEventListener, de.htwberlin.f4.ai.ba.coordinates.android.sensors.Sensor {

    private static final SensorType sensorType = SensorType.ACCELEROMETER_SIMPLE;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private SensorListener listener;

    private float[] values;


    public AccelerometerSimple(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void start() {
        values = new float[3];
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void stop() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this, accelerometerSensor);
        }
    }

    @Override
    public float[] getValues() {
        float[] result = new float[values.length];
        System.arraycopy(values, 0, result, 0, values.length);

        return result;
    }

    @Override
    public boolean isSensorAvailable() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) {
            return false;
        }

        return true;
    }

    @Override
    public void setListener(SensorListener listener) {
        this.listener = listener;
    }

    @Override
    public SensorType getSensorType() {
        return sensorType;
    }

    /**
     * values[0]: Acceleration force along the x axis (including gravity).
     * values[1]: Acceleration force along the y axis (including gravity).
     * values[2]: Acceleration force along the z axis (including gravity).
     * @param sensorEvent
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(sensorEvent.values, 0, values, 0, sensorEvent.values.length);

            if (listener != null) {
                listener.valueChanged(values);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
