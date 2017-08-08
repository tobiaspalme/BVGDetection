package de.htwberlin.f4.ai.ba.coordinates.android.sensors.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.Log;

import java.sql.Timestamp;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorData;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;


/**
 * Created by benni on 23.07.2017.
 */

public class CompassFusion implements SensorEventListener, de.htwberlin.f4.ai.ba.coordinates.android.sensors.Sensor{

    private static final SensorType SENSORTYPE = SensorType.COMPASS_FUSION;

    private SensorManager sensorManager;
    private SensorListener listener;
    private Sensor rotationSensor;

    private float[] orientation = new float[3];
    private float[] rotationMatrix = new float[16];
    private float azimuth;
    private float pitch;
    private float roll;

    private SensorData sensorData;
    private int sensorRate;

    public CompassFusion(Context context, int sensorRate) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorData = new SensorData();
        sensorData.setSensorType(SENSORTYPE);
        this.sensorRate = sensorRate;
    }

    @Override
    public void start() {
        rotationSensor = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ROTATION_VECTOR);
        if (rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, sensorRate);
        }
    }

    @Override
    public void stop() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this, rotationSensor);
        }
    }

    @Override
    public SensorData getValues() {
        return sensorData;
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
        return SENSORTYPE;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(sensorEvent.sensor.getType() == android.hardware.Sensor.TYPE_ROTATION_VECTOR ){
            SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values );

            float[] remapped = new float[16];


            // default we assume the phone is "laying" on its back, so the azimuth is calculcated
            // related to the default y axis of the phone (points out from the top edge)
            // for example: phone is laying on its back towards north -> shows 0°
            // spin phone while still laying on its back -> change in azimuth

            // original values are within [-180,180]
            orientation = SensorManager.getOrientation(rotationMatrix, orientation);

            azimuth = (float) (Math.toDegrees(orientation[0]) + 360) % 360;
            pitch = (float) (Math.toDegrees(orientation[1]));
            roll = (float) (Math.toDegrees(orientation[2]));

            // detect if the phone is "standing" (selfie camera on top or bottom edge) and screen is facing the user.
            // the normal camera on the backside of the phone points away from the user.
            // if yes, we have to remap the axis.
            // Now the azimuth is calculated relative to the camera(on the backside) / z axis of the phone.
            // we remap the earth coordinates z axis on the phones y axis
            // IMPORTANT: SCREEN MUST POINT TOWARDS USER
            if (Math.abs(pitch) > 70) {
                SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, remapped);
                orientation = SensorManager.getOrientation(remapped, orientation);
                azimuth = (float) (Math.toDegrees(orientation[0]) + 360) % 360;
            }



            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            long realTimestamp = timestamp.getTime();
            float[] values = new float[]{azimuth, pitch, roll};

            long timeOffset = System.currentTimeMillis() - SystemClock.elapsedRealtime();
            long calcTimestamp = (sensorEvent.timestamp / 1000000L) + timeOffset;

            //Log.d("tmp", "Compass[0]: " + azimuth + " original: " + Math.toDegrees(orientation[0]));
            //Log.d("tmp", "Compass[1]: " + pitch + " original: " + Math.toDegrees(orientation[1]));
            //Log.d("tmp", "Compass[2]: " + roll + " original: " + Math.toDegrees(orientation[2]));

            //Log.d("tmp", "realtimestamp: " + realTimestamp);
            //Log.d("tmp", "calctimestamp: " + calcTimestamp);

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
    public void onAccuracyChanged(android.hardware.Sensor sensor, int i) {

    }
}
