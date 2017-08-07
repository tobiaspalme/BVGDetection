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
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.AltitudeModule;

/**
 * Created by benni on 28.07.2017.
 */

public class AltitudeModuleA implements AltitudeModule {

    // copied from sensormanager
    private static final float PRESSURE_STANDARD = 1013.25f;

    private SensorDataModel dataModel;
    private SensorFactory sensorFactory;
    private float airPressure;
    private Sensor airPressureSensor;
    private long lastStepTimestamp;
    private float lastAltitude;

    public AltitudeModuleA(SensorFactory sensorFactory, float airPressure) {
        dataModel = new SensorDataModelImpl();
        this.sensorFactory = sensorFactory;
        this.airPressure = airPressure;
        lastAltitude = calcAltitude(airPressure);
        lastStepTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
    }

    // simple altitude calculation, using the same formula as SensorManager.getAltitude() does,
    // so we don't need to pass a context to this class in order to create a SensorManager Object.
    // Source: Professional Android Sensor Programming p. 87
    // since we are not interested in an precise absolute altitude, we use this method to calculate
    // the relative altitude between two points
    // sea-level-standard temperature / temperature lapse rate = 44330
    // 1.2mbar ~= 10m
    private float calcAltitude(float pressure) {
        float a = pressure / PRESSURE_STANDARD;
        double aHigh = Math.pow(a, (1/5.255));
        double result = 44330 * (1 - aHigh);
        return (float) result;
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
