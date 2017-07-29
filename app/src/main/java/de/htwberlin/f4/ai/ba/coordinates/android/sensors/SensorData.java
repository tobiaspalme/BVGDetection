package de.htwberlin.f4.ai.ba.coordinates.android.sensors;

/**
 * Created by benni on 29.07.2017.
 */

public class SensorData {

    private long timestamp;
    private float[] values;
    private SensorType sensorType;

    public SensorData() {
        // initialize with 0 so we dont have to deal with null pointers
        values = new float[]{0.0f, 0.0f, 0.0f};
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float[] getValues() {
        return values;
    }

    public void setValues(float[] values) {
        this.values = values;
    }

    public SensorType getSensorType() {
        return sensorType;
    }

    public void setSensorType(SensorType sensorType) {
        this.sensorType = sensorType;
    }
}
