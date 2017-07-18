package de.htwberlin.f4.ai.ba.coordinates.android.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

/**
 * Note: sometimes it doesn't recognize the last step properly
 */

public class StepCounterImpl implements StepCounter, SensorEventListener {

    // we need the context so we can call getSystemService and get the sensor manager
    private Context context;
    private SensorManager sensorManager;
    private int stepCount;


    public StepCounterImpl(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void start() {
        stepCount = 0;
        Sensor stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void stop() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public int getStepCount() {
        return stepCount;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        stepCount++;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
