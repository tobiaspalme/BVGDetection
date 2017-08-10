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

import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.edge.EdgeImplementation;
import de.htwberlin.f4.ai.ma.node.Node;
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

    private Node startNode;
    private Node targetNode;

    private List<StepData> stepList;
    private float[] coords;

    @Override
    public void setView(MeasureView view) {
        this.view = view;
    }


    @Override
    public void onStartClicked() {
        databaseHandler = new DatabaseHandlerImplementation(view.getContext());
        //List<Node> nodeList = databaseHandler.getAllNodes();

        stepList = new ArrayList<>();
        stepCount = 0;

        view.updateStepCount(stepCount);
        //TODO: set coordinates to startnode coordinates
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

                        // store compass data in model, while calibration
                        // isn't finished
                        if (!calibrated) {
                            sensorDataModel.insertData(sensorData);
                        }
                        break;
                    case COMPASS_SIMPLE:
                        view.updateAzimuth(sensorData.getValues()[0]);

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
                            coords = new float[coordinates.length];
                            System.arraycopy(coordinates, 0, coords, 0, coordinates.length);
                            stepData.setCoords(coords);
                            stepList.add(stepData);

                        }

                        view.updateStepCount(stepCount);
                        break;
                    default:
                        break;

                }
            }
        });

        SensorType compassType = null;
        // VARIANT_A and VARIANT_B are using COMPASS_FUSION
        if (measurementType == IndoorMeasurementType.VARIANT_A || measurementType == IndoorMeasurementType.VARIANT_B) {
            compassType = SensorType.COMPASS_FUSION;
        }
        // VARIANT C is using COMPASS_SIMPLE
        else if (measurementType == IndoorMeasurementType.VARIANT_C) {
            compassType = SensorType.COMPASS_SIMPLE;
        }

        indoorMeasurement.startSensors(Sensor.SENSOR_RATE_MEASUREMENT,
                                       compassType,
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

        if (coords != null) {
            view.updateTargetNodeCoordinates(coords[0], coords[1], coords[2]);
            targetNode.setCoordinates(coords[0] + ";" + coords[1] + ";" + coords[2]);
            List<String> stepCoords = new ArrayList<>();
            // convert coordinates to string and save into list for edge
            for (StepData stepData : stepList) {
                stepCoords.add(stepData.getCoords()[0] + ";" + stepData.getCoords()[1] + ";" + stepData.getCoords()[2]);
            }

            Edge edge = new EdgeImplementation(startNode, targetNode, true, stepCoords, 0);
            databaseHandler.insertEdge(edge);
            databaseHandler.updateNode(targetNode, targetNode.getId());

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
            coords = new float[coordinates.length];
            System.arraycopy(coordinates, 0, coords, 0, coordinates.length);
            stepData.setCoords(coords);
            stepList.add(stepData);
            //view.insertStep(stepData);
        }

        view.updateStepCount(stepCount);
    }

    @Override
    public void onMeasurementTypeSelected(IndoorMeasurementType type) {
        measurementType = type;
    }


    @Override
    public void onPause() {
        if (indoorMeasurement != null) {
            indoorMeasurement.stop();
        }
    }

    @Override
    public void onStartNodeSelected(Node node) {
        startNode = node;

        // check if targetnode was selected
        if (targetNode != null) {
            boolean different = checkNodesDifferent(startNode, targetNode);
            if (different) {
                view.enableStart();
            } else {
                view.disableStart();
            }
        }

        if (startNode.getCoordinates().length() > 0) {
            String[] coordSplitted = startNode.getCoordinates().split(";");
            float x = Float.valueOf(coordSplitted[0]);
            float y = Float.valueOf(coordSplitted[1]);
            float z = Float.valueOf(coordSplitted[2]);

            view.updateStartNodeCoordinates(x, y, z);
        } else {
            view.updateStartNodeCoordinates(0f, 0f, 0f);
        }
    }

    @Override
    public void onTargetNodeSelected(Node node) {
        targetNode = node;

        // check if startnode was selected
        if (startNode != null) {
            boolean different = checkNodesDifferent(startNode, targetNode);
            if (different) {
                view.enableStart();
            } else {
                view.disableStart();
            }
        }

        if (targetNode.getCoordinates().length() > 0) {
            String[] coordSplitted = targetNode.getCoordinates().split(";");
            float x = Float.valueOf(coordSplitted[0]);
            float y = Float.valueOf(coordSplitted[1]);
            float z = Float.valueOf(coordSplitted[2]);

            view.updateTargetNodeCoordinates(x, y, z);
        } else {
            view.updateTargetNodeCoordinates(0f, 0f, 0f);
        }
    }

    private boolean checkNodesDifferent(Node node1, Node node2) {
        // since id is unique, we just check for id
        if (!node1.getId().equals(node2.getId())) {
            return true;
        }
        return false;
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
