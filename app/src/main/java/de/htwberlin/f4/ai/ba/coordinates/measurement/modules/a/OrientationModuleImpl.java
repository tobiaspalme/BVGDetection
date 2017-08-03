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

public class OrientationModuleImpl implements OrientationModule {

    private SensorDataModel dataModel;
    private SensorFactory sensorFactory;
    private Sensor compass;
    //private float orientation;
    private float lastOrientation;
    private long lastStepTimestamp;

    public OrientationModuleImpl(SensorFactory sensorFactory, float azimuth) {
        dataModel = new SensorDataModelImpl();
        this.sensorFactory = sensorFactory;
        lastStepTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
        lastOrientation = azimuth;
    }

    // calculate the orientation change from calibrated azimuth
    @Override
    public float getOrientation() {

        float orientationDiff = 0.0f;
        long currentStepTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
        // calculation
        // just picking the last value in the interval
        Map<SensorType, List<SensorData>> intervalData = dataModel.getDataInInterval(lastStepTimestamp, currentStepTimestamp);
        List<SensorData> dataValues = intervalData.get(SensorType.COMPASS_FUSION);
        if (dataValues != null && dataValues.size() > 0) {
            float currentOrientation = dataValues.get(dataValues.size()-1).getValues()[0];
            orientationDiff = currentOrientation - lastOrientation;
            lastStepTimestamp = currentStepTimestamp;
        }
        return orientationDiff;

    }

    @Override
    public void start() {
        compass = sensorFactory.getSensor(SensorType.COMPASS_FUSION);
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
