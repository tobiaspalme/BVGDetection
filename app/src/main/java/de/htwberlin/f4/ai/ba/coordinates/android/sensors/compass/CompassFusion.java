package de.htwberlin.f4.ai.ba.coordinates.android.sensors.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;


/**
 * Created by benni on 23.07.2017.
 */

public class CompassFusion implements SensorEventListener, de.htwberlin.f4.ai.ba.coordinates.android.sensors.Sensor{

    private static final SensorType sensorType = SensorType.COMPASS_FUSION;

    private SensorManager sensorManager;
    private SensorListener listener;
    private Sensor rotationSensor;

    private float[] orientation = new float[3];
    private float[] rotationMatrix = new float[9];
    private Integer azimuth;

    public CompassFusion(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void start() {
        azimuth = 0;
        rotationSensor = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ROTATION_VECTOR);
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
    public float[] getValues() {
        return new float[]{azimuth};
    }


    @Override
    public boolean isSensorAvailable() {
        if (sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ROTATION_VECTOR) == null) {
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

        if(sensorEvent.sensor.getType() == android.hardware.Sensor.TYPE_ROTATION_VECTOR ){
            SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values );
            // original values are within [-180,180]
            azimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rotationMatrix, orientation)[0]) + 360) % 360;

            if (listener != null) {
                listener.valueChanged(new float[]{azimuth});
            }
        }
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int i) {

    }
}
