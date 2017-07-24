package de.htwberlin.f4.ai.ba.coordinates.android.sensors;

import java.util.Map;

/**
 * Created by benni on 24.07.2017.
 */

public interface SensorDataModel {

    // get sensors data mapped by timestamp. each entry has the data of ALL activated sensors
    Map<Long, Map<SensorType, float[]>> getData();
    // insert sensor data into model, mapped by timestamp
    void insertData(Map<SensorType, float[]> data);
}
