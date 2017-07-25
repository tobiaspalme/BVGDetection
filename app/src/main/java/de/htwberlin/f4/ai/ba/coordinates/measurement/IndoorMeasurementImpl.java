package de.htwberlin.f4.ai.ba.coordinates.measurement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.Sensor;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;

/**
 * Class which implements IndoorMeasurement interface
 *
 *
 */

public class IndoorMeasurementImpl implements IndoorMeasurement {

    private SensorFactory sensorFactory;
    private IndoorMeasurementListener listener;
    private List<Sensor> sensorList;


    public IndoorMeasurementImpl(SensorFactory sensorFactory) {
        this.sensorFactory = sensorFactory;
        sensorList = new ArrayList<>();
    }

    @Override
    public void calibrate() {

    }

    /**
     * start one or more sensors
     * @param sensorType
     */
    @Override
    public void start(SensorType... sensorType) {

        sensorList.clear();

        for (final SensorType type : sensorType) {
            Sensor sensor = sensorFactory.getSensor(type);
            sensor.setListener(new SensorListener() {
                @Override
                public void valueChanged(float[] newValue) {
                    if (listener != null) {
                        listener.valueChanged(newValue, type);
                    }
                }
            });
            sensor.start();
            sensorList.add(sensor);
        }
    }


    @Override
    public void stop() {
        for (Sensor sensor : sensorList) {
            sensor.stop();
        }
    }

    @Override
    public String getCoordinates() {
        return null;
    }

    @Override
    public void setListener(IndoorMeasurementListener listener) {
        this.listener = listener;
    }

    @Override
    public Map<SensorType, float[]> getSensorValues() {
        Map<SensorType, float[]> sensorValues = new HashMap<>();
        for (Sensor sensor : sensorList) {
            sensorValues.put(sensor.getSensorType(), sensor.getValues());
        }

        return sensorValues;
    }
}
