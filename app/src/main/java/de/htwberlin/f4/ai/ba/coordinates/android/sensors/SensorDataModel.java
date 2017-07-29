package de.htwberlin.f4.ai.ba.coordinates.android.sensors;

import java.util.List;
import java.util.Map;

/**
 * Created by benni on 24.07.2017.
 */

public interface SensorDataModel {

    // get sensors data mapped by timestamp. each entry has the data of ALL activated sensors
    Map<SensorType, List<SensorData>> getData();
    // insert sensor data into model, mapped by timestamp
    void insertData(SensorData sensorData);
}
