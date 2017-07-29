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
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.stepcounter.StepCounter;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.AltitudeModule;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.DistanceModule;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.OrientationModule;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a.AltitudeModuleImpl;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a.DistanceModuleImpl;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a.OrientationModuleImpl;


/**
 * Class which implements IndoorMeasurement interface
 *
 *
 */

public class IndoorMeasurementImpl implements IndoorMeasurement {

    private SensorFactory sensorFactory;
    private IndoorMeasurementListener listener;
    private List<Sensor> sensorList;
    private SensorDataModel dataModel;

    private AltitudeModule altitudeModule;
    private DistanceModule distanceModule;
    private OrientationModule orientationModule;

    private float stepLength;
    private int stepPeriod;
    private float pressure;

    public IndoorMeasurementImpl(SensorFactory sensorFactory) {
        this.sensorFactory = sensorFactory;
        sensorList = new ArrayList<>();
        dataModel = new SensorDataModelImpl();
    }


    @Override
    public void calibrate(float stepLength, int stepPeriod, float pressure) {
        this.stepLength = stepLength;
        this.stepPeriod = stepPeriod;
        this.pressure = pressure;
    }

    @Override
    public void start(IndoorMeasurementType indoorMeasurementType) {

        Sensor stepSensor = sensorFactory.getSensor(SensorType.STEPCOUNTER);
        stepSensor.setListener(new SensorListener() {
            @Override
            public void valueChanged(SensorData newValue) {
                // calculate new position with every step
                // berechnung vielleicht in thread auslagern?
            }
        });
        stepSensor.start();
        sensorList.add(stepSensor);

        if (indoorMeasurementType == IndoorMeasurementType.VARIANT_A) {
            altitudeModule = new AltitudeModuleImpl(dataModel, pressure);
            distanceModule = new DistanceModuleImpl(dataModel, stepLength);
            orientationModule = new OrientationModuleImpl(dataModel);

            // sensoren listener registrieren und werte automatisch ins model schieben
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
