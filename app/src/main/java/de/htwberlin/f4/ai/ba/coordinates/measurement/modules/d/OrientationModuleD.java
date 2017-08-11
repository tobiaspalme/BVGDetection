package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.d;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.Sensor;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorData;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;
import de.htwberlin.f4.ai.ba.coordinates.measurement.LowPassFilter;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a.OrientationModuleA;

/**
 * Created by benni on 11.08.2017.
 */

public class OrientationModuleD extends OrientationModuleA {

    public OrientationModuleD(SensorFactory sensorFactory, float azimuth) {
        super(sensorFactory, azimuth);
    }

    @Override
    public float getOrientation() {
        float orientationDiff = 0.0f;
        long currentStepTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
        // calculation
        // just picking the last value in the interval
        Map<SensorType, List<SensorData>> intervalData = dataModel.getDataInInterval(lastStepTimestamp, currentStepTimestamp);
        List<SensorData> dataValues = intervalData.get(SensorType.COMPASS_SIMPLE);
        if (dataValues != null && dataValues.size() > 0) {
            float currentOrientation = dataValues.get(dataValues.size()-1).getValues()[0];
            orientationDiff = currentOrientation - lastOrientation;
            lastStepTimestamp = currentStepTimestamp;
        }
        return orientationDiff;
    }

    @Override
    public void start() {
        compass = sensorFactory.getSensor(SensorType.COMPASS_SIMPLE, Sensor.SENSOR_RATE_MEASUREMENT);
        compass.setListener(new SensorListener() {
            @Override
            public void valueChanged(SensorData newValue) {
                Map<SensorType, List<SensorData>> sensorData = dataModel.getData();
                List<SensorData> oldValues = sensorData.get(SensorType.COMPASS_SIMPLE);
                if (oldValues != null) {
                    float[] latestValue = oldValues.get(oldValues.size()-1).getValues();
                    float filteredValue = LowPassFilter.filter(latestValue[0], newValue.getValues()[0], 0.1f);
                    newValue.setValues(new float[]{filteredValue});
                    //newValue.getValues()[0] = LowPassFilter.filter(latestValue[0], newValue.getValues()[0], 0.1f);
                    //newValue.getValues()[1] = LowPassFilter.filter(latestValue[1], newValue.getValues()[1], 0.1f);
                    //newValue.getValues()[2] = LowPassFilter.filter(latestValue[2], newValue.getValues()[2], 0.1f);
                }

                dataModel.insertData(newValue);
            }
        });
        compass.start();
    }
}
