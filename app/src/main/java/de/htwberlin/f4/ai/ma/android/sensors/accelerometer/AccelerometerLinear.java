package de.htwberlin.f4.ai.ma.android.sensors.accelerometer;

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

public class AccelerometerLinear implements SensorEventListener, de.htwberlin.f4.ai.ma.android.sensors.Sensor {

    private static final SensorType SENSORTYPE = SensorType.ACCELEROMETER_LINEAR;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private SensorListener listener;

    private SensorData sensorData;
    private int sensorRate;

    public AccelerometerLinear(Context context, int sensorRate){
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorData = new SensorData();
        sensorData.setSensorType(SENSORTYPE);
        this.sensorRate = sensorRate;
    }

    @Override
    public void start() {
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, sensorRate);
        }
    }

    @Override
    public void stop() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this, accelerometerSensor);
        }
    }

    @Override
    public SensorData getValues() {
        return sensorData;
    }

    @Override
    public boolean isSensorAvailable() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) == null) {
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

    /**
     * values[0]: Acceleration force along the x axis (excluding gravity)
     * values[1]: Acceleration force along the y axis (excluding gravity)
     * values[2]: Acceleration force along the z axis (excluding gravity)
     * @param sensorEvent
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
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
