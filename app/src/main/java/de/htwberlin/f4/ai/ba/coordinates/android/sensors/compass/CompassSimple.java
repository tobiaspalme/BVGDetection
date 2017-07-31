package de.htwberlin.f4.ai.ba.coordinates.android.sensors.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.sql.Timestamp;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorData;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;


/**
 * Created by benni on 23.07.2017.
 */

public class CompassSimple implements SensorEventListener, de.htwberlin.f4.ai.ba.coordinates.android.sensors.Sensor{

    private static final SensorType SENSORTYPE = SensorType.COMPASS_SIMPLE;

    private SensorManager sensorManager;
    private SensorListener listener;
    private Sensor accelerometerSensor;
    private Sensor magneticFieldSensor;

    private float[] orientation;
    private float[] rotationMatrix;
    private float[] accelerometerValues;
    private float[] magneticValues;


    private float azimuth;
    private SensorData sensorData;

    public CompassSimple(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorData = new SensorData();
        sensorData.setSensorType(SENSORTYPE);
        orientation = new float[3];
        rotationMatrix = new float[9];
        accelerometerValues = new float[3];
        magneticValues = new float[3];
    }

    @Override
    public void start() {
        accelerometerSensor = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER);
        magneticFieldSensor = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_MAGNETIC_FIELD);

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
    public SensorData getValues() {
        return sensorData;
    }


    @Override
    public boolean isSensorAvailable() {
        if ((sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER) == null) ||
                (sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_MAGNETIC_FIELD) == null)) {
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
        return SENSORTYPE;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == android.hardware.Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(sensorEvent.values, 0, accelerometerValues, 0, sensorEvent.values.length);
        } else if (sensorEvent.sensor.getType() == android.hardware.Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(sensorEvent.values, 0, magneticValues, 0, sensorEvent.values.length);
        }

        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magneticValues);
        SensorManager.getOrientation(rotationMatrix, orientation);
        azimuth = (float) (Math.toDegrees(SensorManager.getOrientation(rotationMatrix, orientation)[0]) + 360) % 360;

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long realTimestamp = timestamp.getTime();
        float[] values = new float[]{azimuth};

        sensorData = new SensorData();
        sensorData.setSensorType(SENSORTYPE);
        sensorData.setTimestamp(realTimestamp);
        sensorData.setValues(values);

        if (listener != null) {
            listener.valueChanged(sensorData);
        }
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int i) {

    }
}
