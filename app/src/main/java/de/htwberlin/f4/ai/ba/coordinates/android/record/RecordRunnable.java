package de.htwberlin.f4.ai.ba.coordinates.android.record;

import android.os.Handler;
import android.util.Log;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurement;

/**
 * Created by benni on 24.07.2017.
 */

public class RecordRunnable implements Runnable {

    private SensorDataModel model;
    private IndoorMeasurement indoorMeasurement;
    private Handler handler;
    private long period;

    public RecordRunnable(SensorDataModel model, IndoorMeasurement indoorMeasurement, Handler handler, long period) {
        this.model = model;
        this.indoorMeasurement = indoorMeasurement;
        this.handler = handler;
        this.period = period;
    }

    @Override
    public void run() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Log.d("recordtimer", String.valueOf(timestamp));
        Log.d("recordtimer2", String.valueOf(timestamp.getTime()));

        /*
        // make sure to make a REAL copy of the map containing sensor datas, otherwise
        // we will get always the same data, if working with references
        Map<SensorType, float[]> originalMap = indoorMeasurement.getSensorValues();
        Map<SensorType, float[]> copyMap = new HashMap<>();
        for (Map.Entry<SensorType, float[]> entry : originalMap.entrySet()) {
            float[] copyArray = new float[entry.getValue().length];
            System.arraycopy(entry.getValue(), 0, copyArray, 0, entry.getValue().length);
            copyMap.put(entry.getKey(), copyArray);
        }*/


        model.insertData(indoorMeasurement.getSensorValues());
        handler.postDelayed(this, period);
    }
}
