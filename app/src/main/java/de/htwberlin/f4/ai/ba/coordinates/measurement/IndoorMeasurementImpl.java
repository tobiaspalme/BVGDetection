package de.htwberlin.f4.ai.ba.coordinates.measurement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorData;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.Sensor;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.PositionModule;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a.PositionModuleA;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.b.PositionModuleB;


/**
 * Class which implements IndoorMeasurement interface
 *
 *
 */

public class IndoorMeasurementImpl implements IndoorMeasurement {

    private SensorFactory sensorFactory;
    private SensorListener sensorListener;
    private List<Sensor> sensorList;
    private CalibrationData calibrationData;

    private PositionModule positionModule;


    public IndoorMeasurementImpl(SensorFactory sensorFactory) {
        this.sensorFactory = sensorFactory;
        sensorList = new ArrayList<>();
    }


    @Override
    public void calibrate(CalibrationData calibrationData) {
        this.calibrationData = calibrationData;
    }

    @Override
    public void start(IndoorMeasurementType indoorMeasurementType) {
        if (indoorMeasurementType == IndoorMeasurementType.VARIANT_A) {
            positionModule = new PositionModuleA(sensorFactory, calibrationData);
            positionModule.start();
        } else if (indoorMeasurementType == IndoorMeasurementType.VARIANT_B) {
            positionModule = new PositionModuleB(sensorFactory, calibrationData);
            positionModule.start();
        }

    }

    @Override
    public void stop() {
        // stop all sensors controlled by this class
        for (Sensor sensor : sensorList) {
            sensor.stop();
        }
        if (positionModule != null) {
            positionModule.stop();
        }
    }

    @Override
    public void startSensors(SensorType... sensorType) {
        sensorList.clear();

        for (final SensorType type : sensorType) {
            Sensor sensor = sensorFactory.getSensor(type);
            sensor.setListener(new SensorListener() {
                @Override
                public void valueChanged(SensorData newValue) {
                    if (sensorListener != null) {
                        sensorListener.valueChanged(newValue);
                    }
                }
            });
            sensor.start();
            sensorList.add(sensor);
        }
    }

    @Override
    public float[] getCoordinates() {
        float[] result = null;

        if (positionModule != null) {
            result = positionModule.calculatePosition();
        }
        return result;
    }

    @Override
    public void setSensorListener(SensorListener listener) {
        sensorListener = listener;
    }

    @Override
    public Map<SensorType, SensorData> getLastSensorValues() {
        Map<SensorType, SensorData> sensorValues = new HashMap<>();

        for (Sensor sensor : sensorList) {
            sensorValues.put(sensor.getSensorType(), sensor.getValues());
        }

        return sensorValues;
    }
}
