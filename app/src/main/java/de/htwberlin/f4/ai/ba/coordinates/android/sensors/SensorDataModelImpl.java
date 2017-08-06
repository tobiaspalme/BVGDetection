package de.htwberlin.f4.ai.ba.coordinates.android.sensors;

import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by benni on 24.07.2017.
 */

public class SensorDataModelImpl implements SensorDataModel {


    private Map<SensorType, List<SensorData>> data;

    public SensorDataModelImpl() {
        data = new HashMap<>();
    }

    @Override
    public Map<SensorType, List<SensorData>> getData() {
        return data;
    }

    @Override
    public Map<SensorType, List<SensorData>> getDataInInterval(long start, long end) {
        Map<SensorType, List<SensorData>> result = new HashMap<>();

        // loop through all sensor datas
        for (Map.Entry<SensorType, List<SensorData>> entry : data.entrySet()) {
            SensorType sensorType = entry.getKey();
            List<SensorData> sensorDataFiltered = new ArrayList<>();
            List<SensorData> sensorDataUnfiltered = entry.getValue();
            // check if sensordata is in interval
            for (SensorData data : sensorDataUnfiltered) {
                if ( (data.getTimestamp() >= start) && (data.getTimestamp() <= end) ) {
                    // add sensordata to filtered list
                    sensorDataFiltered.add(data);
                }
            }
            result.put(sensorType, sensorDataFiltered);
        }

        return result;
    }

    @Override
    public void insertData(SensorData sensorData) {
        boolean keyExists = false;

        // check if the key already exists
        // if yes, we just have to get the list by key
        // and add the value to the list
        for (Map.Entry<SensorType, List<SensorData>> entry : data.entrySet()) {
            SensorType sensorType = entry.getKey();

            if (sensorType == sensorData.getSensorType()) {
                keyExists = true;
                List<SensorData> valueList = entry.getValue();
                valueList.add(sensorData);
            }
        }
        // if the key doesn't exist, we have to create
        // a new list for the sensor values
        if (!keyExists) {
            List<SensorData> valueList = new ArrayList<>();
            valueList.add(sensorData);
            data.put(sensorData.getSensorType(), valueList);
        }
    }

    @Override
    public void clearData() {
        data.clear();
    }
}
