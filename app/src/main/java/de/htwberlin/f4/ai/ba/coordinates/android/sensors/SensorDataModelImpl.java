package de.htwberlin.f4.ai.ba.coordinates.android.sensors;

import android.util.Log;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by benni on 24.07.2017.
 */

public class SensorDataModelImpl implements SensorDataModel {

    // mapping sensor data by timestamp with a Map containing the sensortype + sensor value
    private Map<Long, Map<SensorType, float[]>> sensorData;

    public SensorDataModelImpl() {
        sensorData = new LinkedHashMap<>();
    }

    @Override
    public Map<Long, Map<SensorType, float[]>> getData() {
        return sensorData;
    }

    @Override
    public void insertData(Map<SensorType, float[]> data) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        sensorData.put(timestamp.getTime(), data);
    }
}
