package de.htwberlin.f4.ai.ba.coordinates.android.sensors.gravity;

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

public class GravitySensor implements SensorEventListener, de.htwberlin.f4.ai.ba.coordinates.android.sensors.Sensor {

    private static final SensorType sensorType = SensorType.GRAVITY;

    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private SensorListener listener;

    private float[] values;


    public GravitySensor(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void start() {
        values = new float[3];
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        if (gravitySensor != null) {
            sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void stop() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this, gravitySensor);
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
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) == null) {
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
     * values[0]: Force of gravity along the x axis.
     * values[1]: Force of gravity along the y axis.
     * values[2]: Force of gravity along the z axis.
     * @param sensorEvent
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_GRAVITY) {
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
