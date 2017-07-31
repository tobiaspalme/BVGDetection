package de.htwberlin.f4.ai.ba.coordinates.measurement;

import android.util.Log;

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
    private float azimuth;

    private boolean reversed;
    private boolean firstReverseCalc;

    private float[] coordinates;

    public IndoorMeasurementImpl(SensorFactory sensorFactory) {
        this.sensorFactory = sensorFactory;
        sensorList = new ArrayList<>();
        dataModel = new SensorDataModelImpl();
    }


    @Override
    public void calibrate(float stepLength, int stepPeriod, float airPressure, float azimuth) {
        this.stepLength = stepLength;
        this.stepPeriod = stepPeriod;
        this.airPressure = airPressure;
        this.azimuth = azimuth;
    }

    private void calcNewPosition(float altitude, float distance, float orientation) {
        // calculate new position with every step
        // berechnung vielleicht in thread auslagern?

        // combine these 3 values to calculate new position

        // orientation stuff
        double sina = Math.sin(Math.toRadians(90 - orientation));
        double cosa = Math.cos(Math.toRadians(90 - orientation));
        float x = (float)cosa * distance;
        float y = (float)sina * distance;

        Log.d("tmp", "original x: " + x);
        Log.d("tmp", "original y: " + y);

        // check if the user made a turn
        if (Math.abs(orientation) > 90) {
            firstReverseCalc = true;
            if (!reversed) {
                reversed = true;
            } else {
                reversed = false;
            }
        }

        // crappy calculation
        // when a user made a turn, y is always negative,
        // so we have to compensate for that by using a flag if it's
        // the first calculation since the user made a turn...
        // should be improved
        if (reversed) {
            coordinates[0] -= x;
            if (firstReverseCalc) {
                coordinates[1] += y;
                firstReverseCalc = false;
            } else {
                coordinates[1] -= y;
            }

        }

        if (!reversed) {
            coordinates[0] += x;
            if (firstReverseCalc) {
                coordinates[1] -= y;
                firstReverseCalc = false;
            } else {
                coordinates[1] += y;
            }
        }



        // altitude
        coordinates[2] += altitude;
    }

    @Override
    public void start(IndoorMeasurementType indoorMeasurementType) {
        // coordinates[0] = x = movement left / right
        // coordinates[1] = y = movement forward / backward
        // coordinates[2] = z = movement upward / downward
        coordinates = new float[]{0.0f, 0.0f, 0.0f};
        reversed = false;
        firstReverseCalc = false;

        Sensor stepSensor = sensorFactory.getSensor(SensorType.STEPCOUNTER);
        stepSensor.setListener(new SensorListener() {
            @Override
            public void valueChanged(SensorData newValue) {
                if (altitudeModule != null && distanceModule != null && orientationModule != null) {
                    calcNewPosition(altitudeModule.getAltitude(), distanceModule.getDistance(), orientationModule.getOrientation());
                }


                // inform listener about new coordinates
                if (indoorMeasurementListener != null) {
                    indoorMeasurementListener.onNewCoordinates(coordinates[0], coordinates[1], coordinates[2]);
                }
            }
        });
        stepSensor.start();
        sensorList.add(stepSensor);


        if (indoorMeasurementType == IndoorMeasurementType.VARIANT_A) {
            altitudeModule = new AltitudeModuleImpl(sensorFactory, airPressure);
            distanceModule = new DistanceModuleImpl(sensorFactory, stepLength);
            orientationModule = new OrientationModuleImpl(sensorFactory, azimuth);

            altitudeModule.start();
            distanceModule.start();
            orientationModule.start();

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
