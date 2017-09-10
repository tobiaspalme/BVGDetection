package de.htwberlin.f4.ai.ma.measurement.modules.b;

import android.content.Context;

import java.util.List;
import java.util.Map;

import de.htwberlin.f4.ai.ma.android.sensors.Sensor;
import de.htwberlin.f4.ai.ma.android.sensors.SensorData;
import de.htwberlin.f4.ai.ma.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ma.android.sensors.SensorType;
import de.htwberlin.f4.ai.ma.measurement.LowPassFilter;
import de.htwberlin.f4.ai.ma.measurement.modules.a.AltitudeModuleA;

/**
 * AltitudeModuleB Class which implements the AltitudeModule interface.
 *
 * Calculate the relative height using the airpressure from barometer sensor
 *
 * Lowpassfilter is used
 *
 * Author: Benjamin Kneer
 */

public class AltitudeModuleB extends AltitudeModuleA {


    private float lowpassFilterValue;


    public AltitudeModuleB(Context context, float airPressure, float lowpassFilterValue, float threshold) {
        super(context, airPressure, threshold);
        this.lowpassFilterValue = lowpassFilterValue;
    }

    @Override
    public void start() {
        airPressureSensor = sensorFactory.getSensor(SensorType.BAROMETER, Sensor.SENSOR_RATE_MEASUREMENT);
        airPressureSensor.setListener(new SensorListener() {
            @Override
            public void valueChanged(SensorData newValue) {
                Map<SensorType, List<SensorData>> sensorData = dataModel.getData();
                List<SensorData> oldValues = sensorData.get(SensorType.BAROMETER);
                if (oldValues != null) {
                    float[] latestValue = oldValues.get(oldValues.size()-1).getValues();
                    float filteredValue = LowPassFilter.filter(latestValue[0], newValue.getValues()[0], lowpassFilterValue);
                    newValue.setValues(new float[]{filteredValue});
                }

                dataModel.insertData(newValue);
            }
        });
        airPressureSensor.start();
    }

}
