package de.htwberlin.f4.ai.ma.measurement.modules.b;

import android.content.Context;

import java.util.List;
import java.util.Map;

import de.htwberlin.f4.ai.ma.android.sensors.Sensor;
import de.htwberlin.f4.ai.ma.android.sensors.SensorData;
import de.htwberlin.f4.ai.ma.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ma.android.sensors.SensorType;
import de.htwberlin.f4.ai.ma.measurement.LowPassFilter;
import de.htwberlin.f4.ai.ma.measurement.modules.a.OrientationModuleA;

/**
 * Created by benni on 04.08.2017.
 */

public class OrientationModuleB extends OrientationModuleA {

    float lowpassFilterValue;

    public OrientationModuleB(Context context, float lowpassFilterValue) {
        super(context);
        this.lowpassFilterValue = lowpassFilterValue;
    }

    @Override
    public void start() {
        compass = sensorFactory.getSensor(SensorType.COMPASS_FUSION, Sensor.SENSOR_RATE_MEASUREMENT);
        compass.setListener(new SensorListener() {
            @Override
            public void valueChanged(SensorData newValue) {
                Map<SensorType, List<SensorData>> sensorData = dataModel.getData();
                List<SensorData> oldValues = sensorData.get(SensorType.COMPASS_FUSION);
                if (oldValues != null) {
                    float[] latestValue = oldValues.get(oldValues.size()-1).getValues();
                    float filteredValue = LowPassFilter.filter(latestValue[0], newValue.getValues()[0], lowpassFilterValue);
                    newValue.setValues(new float[]{filteredValue});
                }

                dataModel.insertData(newValue);
            }
        });
        compass.start();

    }


}
