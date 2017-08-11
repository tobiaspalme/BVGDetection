package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.d;

import java.util.List;
import java.util.Map;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.Sensor;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorData;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;
import de.htwberlin.f4.ai.ba.coordinates.measurement.LowPassFilter;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a.AltitudeModuleA;

/**
 * Created by benni on 11.08.2017.
 */

public class AltitudeModuleD extends AltitudeModuleA {

    public AltitudeModuleD(SensorFactory sensorFactory, float airPressure) {
        super(sensorFactory, airPressure);
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
                    float filteredValue = LowPassFilter.filter(latestValue[0], newValue.getValues()[0], 0.1f);
                    //newValue.getValues()[0] = LowPassFilter.filter(latestValue[0], newValue.getValues()[0], 0.1f);
                    newValue.setValues(new float[]{filteredValue});
                }

                dataModel.insertData(newValue);
            }
        });
        airPressureSensor.start();
    }
}
