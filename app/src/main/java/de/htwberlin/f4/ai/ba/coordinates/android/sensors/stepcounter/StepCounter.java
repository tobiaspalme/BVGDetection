package de.htwberlin.f4.ai.ba.coordinates.android.sensors.stepcounter;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.Sensor;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;

/**
 * Created by benni on 23.07.2017.
 */

public class StepCounter implements Sensor, SensorEventListener{

    private static final SensorType sensorType = SensorType.STEPCOUNTER;

    private SensorManager sensorManager;
    private SensorListener listener;
    private android.hardware.Sensor stepCounterSensor;

    private Integer stepCount;



    public StepCounter(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void start() {
        // initialize with -1, because as soon as we start the sensor the event onSensorChanged() gets triggered,
        // without doing any steps
        stepCount = -1;
        stepCounterSensor = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_STEP_DETECTOR);
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
    public float[] getValues() {
        return new float[]{stepCount};
    }


    @Override
    public boolean isSensorAvailable() {
        if (sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_STEP_DETECTOR) == null) {
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

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == android.hardware.Sensor.TYPE_STEP_DETECTOR) {
            stepCount++;
            if (listener != null) {
                listener.valueChanged(new float[]{stepCount});
            }
        }
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int i) {

    }
}
