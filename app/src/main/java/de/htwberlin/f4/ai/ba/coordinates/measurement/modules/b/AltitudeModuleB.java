package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.b;

import android.content.Context;
import android.hardware.SensorManager;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.Sensor;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorData;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModelImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactoryImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;
import de.htwberlin.f4.ai.ba.coordinates.measurement.LowPassFilter;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.AltitudeModule;

/**
 * Created by benni on 04.08.2017.
 */

public class AltitudeModuleB implements AltitudeModule {


    private SensorDataModel dataModel;
    private SensorFactory sensorFactory;
    private float airPressure;
    private Sensor airPressureSensor;
    private long lastStepTimestamp;
    private float lastAltitude;
    private float lowpassFilterValue;
    private Context context;

    public AltitudeModuleB(Context context, float airPressure, float lowpassFilterValue) {
        this.context = context;
        dataModel = new SensorDataModelImpl();
        sensorFactory = new SensorFactoryImpl(context);
        this.airPressure = airPressure;
        lastAltitude = calcAltitude(airPressure);
        lastStepTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
        this.lowpassFilterValue = lowpassFilterValue;
    }


    // since we are not interested in an precise absolute altitude, we use this method to calculate
    // the relative altitude between two points
    private float calcAltitude(float pressure) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        return sensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);
    }

    // because pressure drifts over time, we calculate the relative altitude change
    // compared to previous altitude
    @Override
    public float getAltitude() {
        float currentAirPressure;
        float currentAltitude;
        float altitudeDiff = 0.0f;
        long currentStepTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
        // calculation
        // just picking the last value in the interval
        Map<SensorType, List<SensorData>> intervalData = dataModel.getDataInInterval(lastStepTimestamp, currentStepTimestamp);
        List<SensorData> dataValues = intervalData.get(SensorType.BAROMETER);
        if (dataValues != null && dataValues.size() > 0) {
            currentAirPressure = dataValues.get(dataValues.size()-1).getValues()[0];
            currentAltitude = calcAltitude(currentAirPressure);
            altitudeDiff = currentAltitude - lastAltitude;
            // set new values
            lastStepTimestamp = currentStepTimestamp;
            lastAltitude = currentAltitude;
        }

        return altitudeDiff;
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
                    //newValue.getValues()[0] = LowPassFilter.filter(latestValue[0], newValue.getValues()[0], 0.1f);
                    newValue.setValues(new float[]{filteredValue});
                }

                dataModel.insertData(newValue);
            }
        });
        airPressureSensor.start();
    }

    @Override
    public void stop() {
        if (airPressureSensor != null) {
            airPressureSensor.stop();
        }
    }
}
