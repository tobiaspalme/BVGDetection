package de.htwberlin.f4.ai.ba.coordinates.android.sensors.stepcounter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorListener;

/**
 * Note: sometimes it doesn't recognize the last step properly
 */

public class StepCounterImpl implements StepCounter, SensorEventListener {

    private SensorManager sensorManager;
    private SensorListener listener;
    private Sensor stepCounterSensor;

    private Integer stepCount;



    public StepCounterImpl(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void start() {
        stepCount = 0;
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void stop() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this, stepCounterSensor);
        }
    }

    @Override
    public Integer getValue() {
        return stepCount;
    }

    @Override
    public void setListener(SensorListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            stepCount++;
            if (listener != null) {
                listener.valueChanged(stepCount);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
