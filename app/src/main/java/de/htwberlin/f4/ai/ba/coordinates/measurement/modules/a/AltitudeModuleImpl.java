package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a;

import java.sql.Timestamp;

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

public class AltitudeModuleImpl implements AltitudeModule {

    // copied from sensormanager
    private static final float PRESSURE_STANDARD = 1013.25f;

    private SensorDataModel dataModel;
    private SensorFactory sensorFactory;
    private float airPressure;
    private Sensor airPressureSensor;
    private long lastStepTimestamp;
    private float altitude;

    public AltitudeModuleImpl(SensorFactory sensorFactory, float airPressure) {
        dataModel = new SensorDataModelImpl();
        this.sensorFactory = sensorFactory;
        this.airPressure = airPressure;
        altitude = calcAltitude(airPressure);
    }

    // simple altitude calculation, using the same formula as SensorManager.getAltitude() does,
    // so we don't need to pass a context to this class in order to create a SensorManager Object.
    // Source: Professional Android Sensor Programming p. 87
    // since we are not interested in an precise absolute altitude, we use this method to calculate
    // the relative altitude between two points
    // sea-level-standard temperature / temperature lapse rate = 44330
    private float calcAltitude(float pressure) {
        float a = pressure / PRESSURE_STANDARD;
        double aHigh = Math.pow(a, (1/5.255));
        double result = 44330 * (1 - aHigh);
        return (float) result;
    }

    @Override
    public float getAltitude() {
        long currentStepTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
        // calculation


        // set new timestamp
        lastStepTimestamp = currentStepTimestamp;
        return airPressure;
    }

    @Override
    public void start() {
        airPressureSensor = sensorFactory.getSensor(SensorType.BAROMETER);
        airPressureSensor.setListener(new SensorListener() {
            @Override
            public void valueChanged(SensorData newValue) {
                dataModel.insertData(newValue);
            }
        });
        airPressureSensor.start();
        lastStepTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
    }

    @Override
    public void stop() {
        if (airPressureSensor != null) {
            airPressureSensor.stop();
        }
    }
}
