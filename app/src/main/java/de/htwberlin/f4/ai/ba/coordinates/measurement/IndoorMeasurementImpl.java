package de.htwberlin.f4.ai.ba.coordinates.measurement;

import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.Sensor;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;

/**
 * Class which implements IndoorMeasurement interface
 *
 * TODO: move to other package?
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
}
