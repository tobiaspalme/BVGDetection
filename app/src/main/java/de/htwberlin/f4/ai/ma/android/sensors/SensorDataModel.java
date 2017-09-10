package de.htwberlin.f4.ai.ma.android.sensors;

import java.util.List;
import java.util.Map;

/**
 * SensorDataModel Interface
 *
 * Model for storing all sensor data in a map (by sensortype)
 *
 * Author: Benjamin Kneer
 */

public interface SensorDataModel {

    // get sensor data mapped by sensortype
    Map<SensorType, List<SensorData>> getData();

    // since we calculate the position every step, we need to calculate the orientation etc
    // by using values in a specific time interval. Filter all values with the timestamp >=
    // start and timestamp <= end
    Map<SensorType, List<SensorData>> getDataInInterval(long start, long end);

    // insert sensor data into model
    void insertData(SensorData sensorData);

    // remove all data from model
    void clearData();
}
