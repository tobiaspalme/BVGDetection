package de.htwberlin.f4.ai.ma.android.record;

import android.os.Handler;

import java.util.List;
import java.util.Map;

import de.htwberlin.f4.ai.ma.android.sensors.SensorData;
import de.htwberlin.f4.ai.ma.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ma.android.sensors.SensorType;
import de.htwberlin.f4.ai.ma.measurement.IndoorMeasurement;
import de.htwberlin.f4.ai.ma.measurement.LowPassFilter;

/**
 * RecordRunnable Class
 *
 * Used to record sensor data in a thread with a fixed period
 *
 * Author: Benjamin Kneer
 */

public class RecordRunnable implements Runnable {

    private SensorDataModel model;
    private IndoorMeasurement indoorMeasurement;
    private Handler handler;
    private long period;
    private float lowpassFilterValue;

    public RecordRunnable(SensorDataModel model, IndoorMeasurement indoorMeasurement, Handler handler, long period, float lowpassFilterValue) {
        this.model = model;
        this.indoorMeasurement = indoorMeasurement;
        this.handler = handler;
        this.period = period;
        this.lowpassFilterValue = lowpassFilterValue;
    }

    @Override
    public void run() {
        Map<SensorType, SensorData> sensorValues = indoorMeasurement.getLastSensorValues();
        for (Map.Entry<SensorType, SensorData> entry : sensorValues.entrySet()) {

            // get sensor data from model
            Map<SensorType, List<SensorData>> modelMap = model.getData();
            // get specific sensor data
            List<SensorData> oldValues = modelMap.get(entry.getKey());
            if (oldValues != null) {
                float[] latestValue = oldValues.get(oldValues.size()-1).getValues();
                float[] newValue = entry.getValue().getValues();
                for (int i = 0; i < latestValue.length; i++) {
                    // apply lowpass filter
                    newValue[i] = LowPassFilter.filter(latestValue[i], newValue[i], lowpassFilterValue);
                }
            }
            // insert new calculated values into model
            model.insertData(entry.getValue());
        }
        // restart thread with delay
        handler.postDelayed(this, period);
    }
}
