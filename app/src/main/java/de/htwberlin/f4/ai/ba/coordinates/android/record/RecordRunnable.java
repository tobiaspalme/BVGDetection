package de.htwberlin.f4.ai.ba.coordinates.android.record;

import android.os.Handler;
import android.util.Log;

import java.sql.Timestamp;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
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
        model.insertData(indoorMeasurement.getSensorValues());
        handler.postDelayed(this, period);
    }
}
