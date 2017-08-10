package de.htwberlin.f4.ai.ba.coordinates.android.measure;

import java.util.List;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorData;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurementType;
import de.htwberlin.f4.ai.ba.coordinates.measurement.LowPassFilter;

/**
 * Created by benni on 30.07.2017.
 */

public class MeasureCalibration implements Runnable {

    private SensorDataModel sensorDataModel;
    private MeasureCalibrationListener listener;
    private IndoorMeasurementType indoorMeasurementType;

    public MeasureCalibration(SensorDataModel sensorDataModel, IndoorMeasurementType measurementType) {
        this.sensorDataModel = sensorDataModel;
        indoorMeasurementType = measurementType;
    }

    public void setListener(MeasureCalibrationListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        // simply calculating the avg, should be improved
        float pressureAvg = 0.0f;
        float azimuthAvg = 0.0f;

        if (indoorMeasurementType == IndoorMeasurementType.VARIANT_A) {
            // calc avg barometer
            List<SensorData> barometerData = sensorDataModel.getData().get(SensorType.BAROMETER);
            if (barometerData != null) {
                float pressureSum = 0.0f;

                // sum up all airpressure values
                for (SensorData data : barometerData) {
                    pressureSum += data.getValues()[0];
                }
                // calculate avg
                pressureAvg = pressureSum / barometerData.size();
            }
            // calc avg azimuth
            List<SensorData> compassData = sensorDataModel.getData().get(SensorType.COMPASS_FUSION);
            if (compassData != null) {
                float azimuthSum = 0.0f;

                // sum up all azimuth values
                for (SensorData data : compassData) {
                    azimuthSum += data.getValues()[0];
                }
                // calculate avg
                azimuthAvg = azimuthSum / compassData.size();
            }
        } else if (indoorMeasurementType == IndoorMeasurementType.VARIANT_B) {

            List<SensorData> barometerData = sensorDataModel.getData().get(SensorType.BAROMETER);
            if (barometerData != null) {

                // apply lowpass filter
                for (int i = 1; i < barometerData.size(); i++) {
                    float lastValue = barometerData.get(i-1).getValues()[0];
                    float newValue = barometerData.get(i).getValues()[0];
                    newValue = LowPassFilter.filter(lastValue, newValue, 0.1f);
                    barometerData.get(i).setValues(new float[]{newValue});
                }

                /*
                float pressureSum = 0.0f;

                // sum up all airpressure values
                for (SensorData data : barometerData) {
                    pressureSum += data.getValues()[0];
                }
                // calculate avg
                pressureAvg = pressureSum / barometerData.size();
                */
                pressureAvg = barometerData.get(barometerData.size()-1).getValues()[0];
            }

            List<SensorData> compassData = sensorDataModel.getData().get(SensorType.COMPASS_FUSION);
            if (compassData != null) {

                // apply lowpass filter
                for (int i = 1; i < compassData.size(); i++) {
                    float lastValue = compassData.get(i-1).getValues()[0];
                    float newValue = compassData.get(i).getValues()[0];
                    newValue = LowPassFilter.filter(lastValue, newValue, 0.1f);
                    compassData.get(i).setValues(new float[]{newValue});
                }

                float azimuthSum = 0.0f;

                // sum up all azimuth values
                for (SensorData data : compassData) {
                    azimuthSum += data.getValues()[0];
                }
                // calculate avg
                azimuthAvg = azimuthSum / compassData.size();
            }

        } else if (indoorMeasurementType == IndoorMeasurementType.VARIANT_C) {
            // calc avg barometer
            List<SensorData> barometerData = sensorDataModel.getData().get(SensorType.BAROMETER);
            if (barometerData != null) {
                float pressureSum = 0.0f;

                // sum up all airpressure values
                for (SensorData data : barometerData) {
                    pressureSum += data.getValues()[0];
                }
                // calculate avg
                pressureAvg = pressureSum / barometerData.size();
            }
            // calc avg azimuth
            List<SensorData> compassData = sensorDataModel.getData().get(SensorType.COMPASS_SIMPLE);
            if (compassData != null) {
                float azimuthSum = 0.0f;

                // sum up all azimuth values
                for (SensorData data : compassData) {
                    azimuthSum += data.getValues()[0];
                }
                // calculate avg
                azimuthAvg = azimuthSum / compassData.size();
            }
        }





        if (listener != null) {
            listener.onFinish(pressureAvg, azimuthAvg);
        }
    }

}
