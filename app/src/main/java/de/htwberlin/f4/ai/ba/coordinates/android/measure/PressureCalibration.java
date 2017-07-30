package de.htwberlin.f4.ai.ba.coordinates.android.measure;

import java.util.List;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorData;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;

/**
 * Created by benni on 30.07.2017.
 */

public class PressureCalibration implements Runnable {

    private SensorDataModel sensorDataModel;
    private PressureCalibrationListener listener;

    public PressureCalibration(SensorDataModel sensorDataModel) {
        this.sensorDataModel = sensorDataModel;
    }

    public void setListener(PressureCalibrationListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        // simply calculating the avg, should be improved
        List<SensorData> sensorDataList = sensorDataModel.getData().get(SensorType.BAROMETER);
        if (sensorDataList != null) {
            float pressureSum = 0.0f;
            float pressureAvg;

            // sum up all airpressure values
            for (SensorData data : sensorDataList) {
                pressureSum += data.getValues()[0];
            }
            // calculate avg
            pressureAvg = pressureSum / sensorDataList.size();

            if (listener != null) {
                listener.onFinish(pressureAvg);
            }
        }


    }

}
