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
 */

public class CompassImpl implements Compass, SensorEventListener {

    private Context context;
    private SensorManager sensorManager;
    private SensorListener listener;

    public CompassImpl(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void start() {
        Sensor magnetFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magnetFieldSensor != null) {
            sensorManager.registerListener(this, magnetFieldSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void stop() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public Float getValue() {
        return null;
    }

    @Override
    public void setListener(SensorListener listener) {
        this.listener = listener;
    }



    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            Log.d("compass", "0: " + sensorEvent.values[0] + " 1: " + sensorEvent.values[1] + " 2: " + sensorEvent.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
