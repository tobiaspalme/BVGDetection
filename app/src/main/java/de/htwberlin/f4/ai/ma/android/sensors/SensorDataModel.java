package de.htwberlin.f4.ai.ma.android.sensors;

import java.util.List;
import java.util.Map;

/**
 * Created by benni on 24.07.2017.
 */

public interface SensorDataModel {

    // get sensors data mapped by timestamp. each entry has the data of ALL activated sensors
    Map<SensorType, List<SensorData>> getData();
    // since we calculate the position every step, we need to calculate the orientation etc
    // by using values in a specific time interval. Filter all values with the timestamp >=
    // start and timestamp <= end
    Map<SensorType, List<SensorData>> getDataInInterval(long start, long end);
    // insert sensor data into model, mapped by timestamp
    void insertData(SensorData sensorData);

    void clearData();
}
