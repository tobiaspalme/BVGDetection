package de.htwberlin.f4.ai.ba.coordinates.measurement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorData;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModelImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.Sensor;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.PositionModule;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a.PositionModuleImpl;

/**
 * Class which implements IndoorMeasurement interface
 *
 *
 */

public class IndoorMeasurementImpl implements IndoorMeasurement {

    private SensorFactory sensorFactory;
    private IndoorMeasurementListener listener;
    private List<Sensor> sensorList;
    private PositionModule positionModule;
    private SensorDataModel dataModel;

    public IndoorMeasurementImpl(SensorFactory sensorFactory) {
        this.sensorFactory = sensorFactory;
        sensorList = new ArrayList<>();
        dataModel = new SensorDataModelImpl();
    }

    @Override
    public void calibrate() {

    }

    @Override
    public void start(IndoorMeasurementType indoorMeasurementType) {

        if (indoorMeasurementType == IndoorMeasurementType.VARIANT_A) {
            positionModule = new PositionModuleImpl(dataModel);
        }
    }

    @Override
    public void stop() {
        for (Sensor sensor : sensorList) {
            sensor.stop();
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
                    if (listener != null) {
                        listener.valueChanged(newValue);
                    }
                }
            });
            sensor.start();
            sensorList.add(sensor);
        }
    }

    @Override
    public String getCoordinates() {
        /*if (positionModule != null) {
            positionModule.getPosition();
        }*/
        return null;
    }

    @Override
    public void setListener(IndoorMeasurementListener listener) {
        this.listener = listener;
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
