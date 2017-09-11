package de.htwberlin.f4.ai.ma.measurement.modules.d;

import android.content.Context;

import java.util.List;
import java.util.Map;

import de.htwberlin.f4.ai.ma.android.sensors.Sensor;
import de.htwberlin.f4.ai.ma.android.sensors.SensorData;
import de.htwberlin.f4.ai.ma.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ma.android.sensors.SensorType;
import de.htwberlin.f4.ai.ma.measurement.LowPassFilter;
import de.htwberlin.f4.ai.ma.measurement.modules.c.OrientationModuleC;

/**
 * OrientationModuleD Class which implements the OrientationModule interface
 *
 * Calculate current heading / azimuth so the system knows the direction
 * of the user's movement
 *
 * Sensor: CompassSimple
 *
 * lowpass filter used
 *
 * Author: Benjamin Kneer
 */

public class OrientationModuleD extends OrientationModuleC {

    private float lowpassFilterValue;

    public OrientationModuleD(Context context, float lowpassFilterValue) {
        super(context);
        this.lowpassFilterValue = lowpassFilterValue;
    }


    /************************************************************************************
    *                                                                                   *
    *                               Interface Methods                                   *
    *                                                                                   *
    *************************************************************************************/


    /**
     * start sensor, register listener and apply lowpass filter
     */
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
                    float filteredValue = LowPassFilter.filter(latestValue[0], newValue.getValues()[0], lowpassFilterValue);
                    newValue.setValues(new float[]{filteredValue});
                }

                dataModel.insertData(newValue);
            }
        });
        compass.start();
    }
}
