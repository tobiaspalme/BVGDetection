package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a;

import android.util.Log;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.Sensor;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorData;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModelImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.OrientationModule;

/**
 * Created by benni on 28.07.2017.
 */

public class OrientationModuleA implements OrientationModule {

    protected SensorDataModel dataModel;
    protected SensorFactory sensorFactory;
    protected Sensor compass;
    //private float orientation;
    protected float lastOrientation;
    protected long lastStepTimestamp;

    public OrientationModuleA(SensorFactory sensorFactory) {
        dataModel = new SensorDataModelImpl();
        this.sensorFactory = sensorFactory;
        lastStepTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
    }

    // calculate the orientation change from calibrated azimuth
    @Override
    public float getOrientation() {

        float currentOrientation = 0;
        long currentStepTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
        // calculation
        // just picking the last value in the interval
        Map<SensorType, List<SensorData>> intervalData = dataModel.getDataInInterval(lastStepTimestamp, currentStepTimestamp);
        List<SensorData> dataValues = intervalData.get(SensorType.COMPASS_FUSION);
        if (dataValues != null && dataValues.size() > 0) {
            currentOrientation = dataValues.get(dataValues.size()-1).getValues()[0];
            lastStepTimestamp = currentStepTimestamp;
        }

        return currentOrientation;
    }

    @Override
    public void start() {
        compass = sensorFactory.getSensor(SensorType.COMPASS_FUSION, Sensor.SENSOR_RATE_MEASUREMENT);
        compass.setListener(new SensorListener() {
            @Override
            public void valueChanged(SensorData newValue) {
                dataModel.insertData(newValue);
            }
        });
        compass.start();
    }

    @Override
    public void stop() {
        if (compass != null) {
            compass.stop();
        }
    }
}
