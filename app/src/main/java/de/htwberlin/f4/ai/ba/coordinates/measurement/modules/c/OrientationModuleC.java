package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.c;

import android.content.Context;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.Sensor;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorData;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a.OrientationModuleA;

/**
 * Created by benni on 10.08.2017.
 */

public class OrientationModuleC extends OrientationModuleA{

    public OrientationModuleC(Context context) {
        super(context);
    }

    @Override
    public float getOrientation() {
        float currentOrientation = 0;
        long currentStepTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
        // calculation
        // just picking the last value in the interval
        Map<SensorType, List<SensorData>> intervalData = dataModel.getDataInInterval(lastStepTimestamp, currentStepTimestamp);
        List<SensorData> dataValues = intervalData.get(SensorType.COMPASS_SIMPLE);
        if (dataValues != null && dataValues.size() > 0) {
            currentOrientation = dataValues.get(dataValues.size()-1).getValues()[0];
            lastStepTimestamp = currentStepTimestamp;
        }
        return currentOrientation;
    }

    @Override
    public void start() {
        compass = sensorFactory.getSensor(SensorType.COMPASS_SIMPLE, Sensor.SENSOR_RATE_MEASUREMENT);
        compass.setListener(new SensorListener() {
            @Override
            public void valueChanged(SensorData newValue) {
                dataModel.insertData(newValue);
            }
        });
        compass.start();
    }
}
