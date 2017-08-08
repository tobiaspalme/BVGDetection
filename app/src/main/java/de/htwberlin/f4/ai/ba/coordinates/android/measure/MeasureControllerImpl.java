package de.htwberlin.f4.ai.ba.coordinates.android.measure;

import android.app.AlertDialog;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ba.coordinates.android.calibrate.CalibratePersistance;
import de.htwberlin.f4.ai.ba.coordinates.android.calibrate.CalibratePersistanceImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.Sensor;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorData;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModelImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactoryImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;
import de.htwberlin.f4.ai.ba.coordinates.measurement.CalibrationData;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurement;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurementFactory;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurementType;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerImplementation;

/**
 * Created by benni on 18.07.2017.
 */

public class MeasureControllerImpl implements MeasureController {

    private static final int CALIBRATION_TIME = 3000;

    private MeasureView view;
    private IndoorMeasurement indoorMeasurement;
    private Handler timerHandler;
    private MeasureCalibration pressureCalibration;
    private SensorDataModel sensorDataModel;
    private boolean calibrated;
    private AlertDialog calibrationDialog;
    private int stepCount;

    private IndoorMeasurementType measurementType;
    private DatabaseHandler databaseHandler;

    @Override
    public void setView(MeasureView view) {
        this.view = view;
    }


    @Override
    public void onStartClicked() {
        databaseHandler = new DatabaseHandlerImplementation(view.getContext());
        //List<Node> nodeList = databaseHandler.getAllNodes();


        stepCount = 0;

        view.updateStepCount(stepCount);
        view.updateCoordinates(0.0f, 0.0f, 0.0f);
        //view.updatePressure(0.0f);


        calibrated = false;
        sensorDataModel = new SensorDataModelImpl();

        SensorFactory sensorFactory = new SensorFactoryImpl(view.getContext());
        indoorMeasurement = IndoorMeasurementFactory.getIndoorMeasurement(sensorFactory);

        indoorMeasurement.setSensorListener(new SensorListener() {
            @Override
            public void valueChanged(SensorData sensorData) {
                SensorType sensorType = sensorData.getSensorType();
                switch (sensorType) {

                    case COMPASS_FUSION:
                        view.updateAzimuth(sensorData.getValues()[0]);
                        Log.d("tmp", "Compass[0]: " + sensorData.getValues()[0]);
                        Log.d("tmp", "Compass[1]: " + sensorData.getValues()[1]);
                        Log.d("tmp", "Compass[2]: " + sensorData.getValues()[2]);
                        // store compass data in model, while calibration
                        // isn't finished
                        if (!calibrated) {
                            sensorDataModel.insertData(sensorData);
                        }
                        break;
                    case BAROMETER:
                        //view.updatePressure(sensorData.getValues()[0]);
                        // store barometer data in model, while calibration
                        // isn't finished
                        if (!calibrated) {
                            sensorDataModel.insertData(sensorData);
                        }
                        break;
                    case STEPCOUNTER:
                        // with each step we get the new position
                        stepCount++;
                        float[] coordinates = indoorMeasurement.getCoordinates();
                        if (coordinates != null) {
                            view.updateCoordinates(coordinates[0], coordinates[1], coordinates[2]);
                            StepData stepData = new StepData();
                            stepData.setStepName("Step " + stepCount);
                            float[] coordCopy = new float[coordinates.length];
                            System.arraycopy(coordinates, 0, coordCopy, 0, coordinates.length);
                            stepData.setCoords(coordCopy);

                            view.insertStep(stepData);
                        }

                        view.updateStepCount(stepCount);
                        break;
                    default:
                        break;

                }
            }
        });

        indoorMeasurement.startSensors(Sensor.SENSOR_RATE_MEASUREMENT,
                                       SensorType.COMPASS_FUSION,
                                       SensorType.BAROMETER,
                                       SensorType.STEPCOUNTER);

        calibrate();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
        alertDialogBuilder.setMessage("Bitte warten");
        alertDialogBuilder.setTitle("Kalibrierung im gange");
        calibrationDialog = alertDialogBuilder.create();
        calibrationDialog.setCancelable(false);
        calibrationDialog.show();

    }

    @Override
    public void onStopClicked() {
        if (indoorMeasurement != null) {
            indoorMeasurement.stop();
        }
    }

    // just a temporary solution if we want to get coords without doing a step
    @Override
    public void onAddClicked() {
        stepCount++;
        float[] coordinates = indoorMeasurement.getCoordinates();
        if (coordinates != null) {
            view.updateCoordinates(coordinates[0], coordinates[1], coordinates[2]);
            StepData stepData = new StepData();
            stepData.setStepName("Step " + stepCount);
            float[] coordCopy = new float[coordinates.length];
            System.arraycopy(coordinates, 0, coordCopy, 0, coordinates.length);
            stepData.setCoords(coordCopy);

            view.insertStep(stepData);
        }

        view.updateStepCount(stepCount);
    }

    @Override
    public void onMeasurementTypeSelected(IndoorMeasurementType type) {
        measurementType = type;
    }

    @Override
    public void onNodeSelected(Node node, StepData step) {
        if (databaseHandler != null) {
            node.setCoordinates(step.getCoords()[0] + ";" + step.getCoords()[1] + ";" + step.getCoords()[2]);
            databaseHandler.updateNode(node, node.getId());
        }
    }

    @Override
    public void onPause() {
        if (indoorMeasurement != null) {
            indoorMeasurement.stop();
        }
    }

    private void calibrate() {
        timerHandler = new Handler(Looper.getMainLooper());
        pressureCalibration = new MeasureCalibration(sensorDataModel, measurementType);

        pressureCalibration.setListener(new MeasureCalibrationListener() {
            @Override
            public void onFinish(float airPressure, float azimuth) {
                calibrationDialog.dismiss();
                timerHandler.removeCallbacks(pressureCalibration);
                calibrated = true;
                // load steplength and stepperiod calibration
                CalibratePersistance calibratePersistance = new CalibratePersistanceImpl(view.getContext());
                CalibrationData calibrationData = calibratePersistance.load();
                if (calibrationData != null) {
                    // save new calibrated airpressure and azimuth
                    calibrationData.setAirPressure(airPressure);
                    calibrationData.setAzimuth(azimuth);
                    // calibrate the indoormeasurement
                    indoorMeasurement.calibrate(calibrationData);
                    // start measurement
                    //indoorMeasurement.start(IndoorMeasurementType.VARIANT_A);
                    indoorMeasurement.start(measurementType);
                }
            }
        });

        timerHandler.postDelayed(pressureCalibration, CALIBRATION_TIME);


    }
}
