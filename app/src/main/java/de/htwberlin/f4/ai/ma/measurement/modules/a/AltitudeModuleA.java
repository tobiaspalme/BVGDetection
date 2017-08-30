package de.htwberlin.f4.ai.ma.measurement.modules.a;

import android.content.Context;
import android.hardware.SensorManager;

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
import de.htwberlin.f4.ai.ma.measurement.modules.AltitudeModule;

/**
 * Created by benni on 28.07.2017.
 */

public class AltitudeModuleA implements AltitudeModule {


    protected SensorDataModel dataModel;
    protected SensorFactory sensorFactory;
    protected float airPressure;
    protected Sensor airPressureSensor;
    protected long lastStepTimestamp;
    protected float lastAltitude;
    protected Context context;

    public AltitudeModuleA(Context context, float airPressure) {
        this.context = context;
        dataModel = new SensorDataModelImpl();
        sensorFactory = new SensorFactoryImpl(context);
        this.airPressure = airPressure;
        lastAltitude = calcAltitude(airPressure);
        lastStepTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
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
        // just picking the last value
        Map<SensorType, List<SensorData>> intervalData = dataModel.getData();
        List<SensorData> dataValues = intervalData.get(SensorType.BAROMETER);
        if (dataValues != null && dataValues.size() > 0) {
            currentAirPressure = dataValues.get(dataValues.size()-1).getValues()[0];
            currentAltitude = calcAltitude(currentAirPressure);
            altitudeDiff = currentAltitude - lastAltitude;
            // set new values
            lastStepTimestamp = currentStepTimestamp;
            //lastAltitude = currentAltitude;
            if (Math.abs(altitudeDiff) >= 0.1) {
                lastAltitude = currentAltitude;
            } else {
                altitudeDiff = 0;
            }
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
