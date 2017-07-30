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
    private IndoorMeasurementListener indoorMeasurementListener;
    private SensorListener sensorListener;
    private List<Sensor> sensorList;
    private SensorDataModel dataModel;

    private AltitudeModule altitudeModule;
    private DistanceModule distanceModule;
    private OrientationModule orientationModule;

    private float stepLength;
    private int stepPeriod;
    private float airPressure;

    public IndoorMeasurementImpl(SensorFactory sensorFactory) {
        this.sensorFactory = sensorFactory;
        sensorList = new ArrayList<>();
        dataModel = new SensorDataModelImpl();
    }


    @Override
    public void calibrate(float stepLength, int stepPeriod, float airPressure) {
        this.stepLength = stepLength;
        this.stepPeriod = stepPeriod;
        this.airPressure = airPressure;
    }

    @Override
    public void start(final IndoorMeasurementType indoorMeasurementType) {





        Sensor stepSensor = sensorFactory.getSensor(SensorType.STEPCOUNTER);
        stepSensor.setListener(new SensorListener() {
            @Override
            public void valueChanged(SensorData newValue) {
                // calculate new position with every step
                // berechnung vielleicht in thread auslagern?
                float altitude = 0.0f;
                float distance = 0.0f;
                float orientation = 0.0f;

                if (altitudeModule != null) {
                    altitude = altitudeModule.getAltitude();
                }
                if (distanceModule != null) {
                    distance = distanceModule.getDistance();
                }
                if (orientationModule != null) {
                    orientation = orientationModule.getOrientation();
                }

                // combine these 3 values to calculate new position

                // inform listener about new coordinates
                if (indoorMeasurementListener != null) {
                    indoorMeasurementListener.onNewCoordinates(0.0f, 0.0f, altitude);
                }
            }
        });
        stepSensor.start();
        sensorList.add(stepSensor);

        if (indoorMeasurementType == IndoorMeasurementType.VARIANT_A) {
            altitudeModule = new AltitudeModuleImpl(sensorFactory, airPressure);
            distanceModule = new DistanceModuleImpl(sensorFactory, stepLength);
            orientationModule = new OrientationModuleImpl(sensorFactory);

            altitudeModule.start();
            distanceModule.start();
            orientationModule.start();


            // sensoren listener registrieren und werte automatisch ins model schieben
        }
    }

    @Override
    public void stop() {
        // stop all sensors controlled by this class
        for (Sensor sensor : sensorList) {
            sensor.stop();
        }
        // stop all sensors controlled by altitudemodule
        if (altitudeModule != null) {
            altitudeModule.stop();
        }
        // stop all sensors controlled by distancemodul
        if (distanceModule != null) {
            distanceModule.stop();
        }
        // stop all sensors controlled by orientationmodule
        if (orientationModule != null) {
            orientationModule.stop();
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
    public String getCoordinates() {
        /*if (positionModule != null) {
            positionModule.getPosition();
        }*/
        return null;
    }

    @Override
    public void setSensorListener(SensorListener listener) {
        sensorListener = listener;
    }

    @Override
    public void setIndoorMeasurementListener(IndoorMeasurementListener listener) {
        indoorMeasurementListener = listener;
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
