package de.htwberlin.f4.ai.ma.measurement.modules.variant_a;

import android.content.Context;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import de.htwberlin.f4.ai.ma.android.sensors.Sensor;
import de.htwberlin.f4.ai.ma.android.sensors.SensorData;
import de.htwberlin.f4.ai.ma.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ma.android.sensors.SensorDataModelImpl;
import de.htwberlin.f4.ai.ma.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ma.android.sensors.SensorFactoryImpl;
import de.htwberlin.f4.ai.ma.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ma.android.sensors.SensorType;
import de.htwberlin.f4.ai.ma.measurement.modules.OrientationModule;

/**
 * OrientationModuleA Class which implements the OrientationModule interface
 *
 * Calculate current heading / azimuth so the system knows the direction
 * of the user's movement
 *
 * Sensor: CompassFusion
 *
 * No lowpass filter used
 *
 * Author: Benjamin Kneer
 */

public class OrientationModuleA implements OrientationModule {

    protected SensorDataModel dataModel;
    protected SensorFactory sensorFactory;
    protected Sensor compass;
    protected long lastStepTimestamp;
    protected Context context;

    public OrientationModuleA(Context context) {
        this.context = context;
        dataModel = new SensorDataModelImpl();
        sensorFactory = new SensorFactoryImpl(context);
        lastStepTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
    }


    /************************************************************************************
    *                                                                                   *
    *                               Interface Methods                                   *
    *                                                                                   *
    *************************************************************************************/


    /**
     * get azimuth from compassfusion sensor
     *
     * @return azimuth, pitch, roll
     */
    @Override
    public float[] getOrientation() {
        float[] result = new float[3];
        float currentOrientation = 0;
        long currentStepTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
        // calculation
        // just picking the last value
        Map<SensorType, List<SensorData>> intervalData = dataModel.getData();
        List<SensorData> dataValues = intervalData.get(SensorType.COMPASS_FUSION);
        if (dataValues != null && dataValues.size() > 0) {
            currentOrientation = dataValues.get(dataValues.size()-1).getValues()[0];
            lastStepTimestamp = currentStepTimestamp;

            result[0] = currentOrientation;
            result[1] = dataValues.get(dataValues.size()-1).getValues()[1];
            result[2] = dataValues.get(dataValues.size()-1).getValues()[2];
        }

        return result;
    }


    /**
     * start sensor and register listener
     */
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


    /**
     * stop sensor and unregister listener
     */
    @Override
    public void stop() {
        if (compass != null) {
            compass.stop();
        }
    }
}
