package de.htwberlin.f4.ai.ma.android.sensors.gyroscope;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;

import java.sql.Timestamp;

import de.htwberlin.f4.ai.ma.android.sensors.SensorData;
import de.htwberlin.f4.ai.ma.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ma.android.sensors.SensorType;

/**
 * Created by benni on 22.07.2017.
 */

public class GyroscopeUncalibrated implements SensorEventListener, de.htwberlin.f4.ai.ma.android.sensors.Sensor {

    private static final SensorType SENSORTYPE = SensorType.GYROSCOPE_UNCALIBRATED;

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorListener listener;

    private SensorData sensorData;
    private int sensorRate;


    public GyroscopeUncalibrated(Context context, int sensorRate) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorData = new SensorData();
        sensorData.setSensorType(SENSORTYPE);
        this.sensorRate = sensorRate;
    }

    @Override
    public void start() {
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        if (gyroscopeSensor != null) {
            sensorManager.registerListener(this, gyroscopeSensor, sensorRate);
        }
    }

    @Override
    public void stop() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this, gyroscopeSensor);
        }
    }

    @Override
    public SensorData getValues() {
        return sensorData;
    }

    @Override
    public boolean isSensorAvailable() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED) == null) {
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
        if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE_UNCALIBRATED) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            long realTimestamp = timestamp.getTime();
            float[] values = new float[sensorEvent.values.length];
            System.arraycopy(sensorEvent.values, 0, values, 0, sensorEvent.values.length);

            long timeOffset = System.currentTimeMillis() - SystemClock.elapsedRealtime();
            long calcTimestamp = (sensorEvent.timestamp / 1000000L) + timeOffset;

            sensorData = new SensorData();
            sensorData.setSensorType(SENSORTYPE);
            sensorData.setTimestamp(calcTimestamp);
            sensorData.setValues(values);

            if (listener != null) {
                listener.valueChanged(sensorData);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
