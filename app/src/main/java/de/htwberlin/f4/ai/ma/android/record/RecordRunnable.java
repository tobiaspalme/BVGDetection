package de.htwberlin.f4.ai.ma.android.record;

import android.os.Handler;

import java.util.Map;

import de.htwberlin.f4.ai.ma.android.sensors.SensorData;
import de.htwberlin.f4.ai.ma.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ma.android.sensors.SensorType;
import de.htwberlin.f4.ai.ma.measurement.IndoorMeasurement;

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
        Map<SensorType, SensorData> sensorValues = indoorMeasurement.getLastSensorValues();
        for (Map.Entry<SensorType, SensorData> entry : sensorValues.entrySet()) {
            model.insertData(entry.getValue());
        }

        handler.postDelayed(this, period);
    }
}
