package de.htwberlin.f4.ai.ba.coordinates.android.measure;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.example.carol.bvg.R;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
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
    private float edgeDistance; // edgedistance in m

    private IndoorMeasurementType measurementType;
    private DatabaseHandler databaseHandler;

    private Node startNode;
    private Node targetNode;

    private List<StepData> stepList;
    private float[] coords = new float[3];
    private boolean handycapFriendly;

    @Override
    public void setView(MeasureView view) {
        this.view = view;
    }


    @Override
    public void onStartClicked() {

        // check for calibration
        if (!alreadyCalibrated()) {
            view.showAlert("Bitte zuerst die Schrittkalibrierung durchführen!");
            return;
        }

        databaseHandler = new DatabaseHandlerImplementation(view.getContext());
        //List<Node> nodeList = databaseHandler.getAllNodes();

        stepList = new ArrayList<>();
        stepCount = 0;
        edgeDistance = 0f;

        view.updateStepCount(stepCount);
        view.updateDistance(edgeDistance);

        // check if the startnode already got coordinates
        if (startNode.getCoordinates().length() > 0) {
            String[] splitted = startNode.getCoordinates().split(";");
            // save the existing coordinates for distance calculation
            coords[0] = Float.valueOf(splitted[0]);
            coords[1] = Float.valueOf(splitted[1]);
            coords[2] = Float.valueOf(splitted[2]);
            // update coordinates in view
            view.updateCoordinates(coords[0], coords[1], coords[2]);
        } else {
            view.updateCoordinates(0.0f, 0.0f, 0.0f);
        }

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
                        float[] newStepCoords = indoorMeasurement.getCoordinates();
                        if (newStepCoords != null) {
                            // calculate distance from previous step to new step
                            float stepDistance = calcDistance(coords[0], coords[1], newStepCoords[0], newStepCoords[1]);
                            edgeDistance += stepDistance;

                            view.updateDistance(edgeDistance);
                            view.updateCoordinates(newStepCoords[0], newStepCoords[1], newStepCoords[2]);
                            StepData stepData = new StepData();
                            stepData.setStepName("Step " + stepCount);
                            coords = new float[newStepCoords.length];
                            System.arraycopy(newStepCoords, 0, coords, 0, newStepCoords.length);
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

        // determine which compasstype we are gonna use
        SensorType compassType = null;
        // VARIANT_A and VARIANT_B are using COMPASS_FUSION
        if (measurementType == IndoorMeasurementType.VARIANT_A || measurementType == IndoorMeasurementType.VARIANT_B) {
            compassType = SensorType.COMPASS_FUSION;
        }
        // VARIANT_C and VARIANT_D is using COMPASS_SIMPLE
        else if (measurementType == IndoorMeasurementType.VARIANT_C || measurementType == IndoorMeasurementType.VARIANT_D) {
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
            // ask the user if the way was handycap friendly and save measurement data afterwards
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
            alertDialogBuilder.setTitle("Barrierefreiheit");
            alertDialogBuilder.setMessage("War der zurückgelegte Weg barrierefrei?");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setIcon(R.drawable.barrierefrei);

            alertDialogBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            handycapFriendly = true;
                            saveMeasurementData();
                            dialog.dismiss();
                        }
                    });

            alertDialogBuilder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            handycapFriendly = false;
                            saveMeasurementData();
                            dialog.dismiss();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private void saveMeasurementData() {
        view.updateTargetNodeCoordinates(coords[0], coords[1], coords[2]);
        targetNode.setCoordinates(coords[0] + ";" + coords[1] + ";" + coords[2]);
        List<String> stepCoords = new ArrayList<>();
        // convert coordinates to string and save into list for edge
        for (StepData stepData : stepList) {
            stepCoords.add(stepData.getCoords()[0] + ";" + stepData.getCoords()[1] + ";" + stepData.getCoords()[2]);
        }

        //check if edge already exists
        List<Edge> edgeList = databaseHandler.getAllEdges();
        // create a temporary edge object to check if edge already exists
        Edge checkEdge = new EdgeImplementation(startNode, targetNode, true, null, 0, "");
        boolean foundEdge = databaseHandler.checkIfEdgeExists(checkEdge);

        // delete the old edge
        if (foundEdge) {
            databaseHandler.deleteEdge(checkEdge);
        }
        // create new edge
        Edge edge= new EdgeImplementation(startNode, targetNode, handycapFriendly, stepCoords, 0, "");
        // since the distance is calculated in meters we need to convert it into cm for edge weight
        int weightCm = Math.round(edgeDistance*100);
        edge.setWeight(weightCm);
        // insert edge into db and update our targetnode
        databaseHandler.insertEdge(edge);
        databaseHandler.updateNode(targetNode, targetNode.getId());
        // update view with new edge data
        view.updateEdge(edge);
    }


    // just a temporary solution if we want to get coords without doing a step
    @Override
    public void onAddClicked() {
        // with each step we get the new position
        stepCount++;
        float[] newStepCoords = indoorMeasurement.getCoordinates();
        if (newStepCoords != null) {
            // calculate distance from previous step to new step
            float stepDistance = calcDistance(coords[0], coords[1], newStepCoords[0], newStepCoords[1]);
            edgeDistance += stepDistance;

            view.updateDistance(edgeDistance);
            view.updateCoordinates(newStepCoords[0], newStepCoords[1], newStepCoords[2]);
            StepData stepData = new StepData();
            stepData.setStepName("Step " + stepCount);
            coords = new float[newStepCoords.length];
            System.arraycopy(newStepCoords, 0, coords, 0, newStepCoords.length);
            stepData.setCoords(coords);
            stepList.add(stepData);

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
        handleNodeSelection(startNode, targetNode);
    }

    @Override
    public void onTargetNodeSelected(Node node) {
        targetNode = node;
        handleNodeSelection(startNode, targetNode);
    }

    private void handleNodeSelection(Node start, Node target) {
        // check if startnode and targetnode was selected
        if (start != null && target != null) {
            // update coordinates of startnode
            if (start.getCoordinates().length() > 0) {
                String[] coordSplitted = start.getCoordinates().split(";");
                float x = Float.valueOf(coordSplitted[0]);
                float y = Float.valueOf(coordSplitted[1]);
                float z = Float.valueOf(coordSplitted[2]);

                view.updateStartNodeCoordinates(x, y, z);
            } else {
                view.updateStartNodeCoordinates(0f, 0f, 0f);
            }
            // update coordinates of targetnode
            if (target.getCoordinates().length() > 0) {
                String[] coordSplitted = target.getCoordinates().split(";");
                float x = Float.valueOf(coordSplitted[0]);
                float y = Float.valueOf(coordSplitted[1]);
                float z = Float.valueOf(coordSplitted[2]);

                view.updateTargetNodeCoordinates(x, y, z);
            } else {
                view.updateTargetNodeCoordinates(0f, 0f, 0f);
            }

            // check if start and targetnode are different, if yes we have to update edge information
            // and enable measurement start
            boolean different = checkNodesDifferent(start, target);
            if (different) {
                view.enableStart();

                // edge stuff
                if (databaseHandler == null) {
                    databaseHandler = new DatabaseHandlerImplementation(view.getContext());
                }
                // check if edge already exists
                Edge tmpEdge = new EdgeImplementation(start, target, true, 0);
                if (databaseHandler.checkIfEdgeExists(tmpEdge)) {
                    // edge exists, now we have to find the right one
                    Edge existingEdge = null;
                    for (Edge edge : databaseHandler.getAllEdges()) {
                        if ( (edge.getNodeA().getId().equals(start.getId()) && edge.getNodeB().getId().equals(target.getId())) ||
                                (edge.getNodeA().getId().equals(target.getId()) && edge.getNodeB().getId().equals(start.getId())) ) {
                            existingEdge = edge;
                            break;
                        }
                    }
                    // if we found an edge update view with correct data
                    if (existingEdge != null) {
                        view.updateEdge(existingEdge);
                    }
                    // if we didnt find edge, update view with placeholder data
                    else {
                        Edge placeHolderEdge = new EdgeImplementation(null, null, true, 0);
                        view.updateEdge(placeHolderEdge);
                    }
                }

            }
            // if start and targetnode are equal just update the edge with placeholder
            else {
                view.disableStart();
                Edge placeHolderEdge = new EdgeImplementation(null, null, true, 0);
                view.updateEdge(placeHolderEdge);
            }
        }
    }

    @Override
    public void onTestClicked() {
        startNode.setCoordinates("");
        targetNode.setCoordinates("");
        databaseHandler = new DatabaseHandlerImplementation(view.getContext());
        databaseHandler.updateNode(startNode, startNode.getId());
        databaseHandler.updateNode(targetNode, targetNode.getId());
        /*
        List<Edge> edgeList = databaseHandler.getAllEdges();
        for (Edge edge : edgeList) {
            databaseHandler.deleteEdge(edge);
        }*/
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
            public void onFinish(float airPressure) {
                calibrationDialog.dismiss();
                timerHandler.removeCallbacks(pressureCalibration);
                calibrated = true;
                // load steplength and stepperiod calibration
                CalibratePersistance calibratePersistance = new CalibratePersistanceImpl(view.getContext());
                CalibrationData calibrationData = calibratePersistance.load();

                // get coordinates from startnode and initialize measurement with those.
                // if the node doesn't have any coordinates we initialize with 0,0,0
                String startCoordinatesStr = startNode.getCoordinates();
                if (startCoordinatesStr.length() > 0) {
                    float[] startCoordinates = new float[3];
                    String[] splitted = startCoordinatesStr.split(";");
                    startCoordinates[0] = Float.valueOf(splitted[0]);
                    startCoordinates[1] = Float.valueOf(splitted[1]);
                    startCoordinates[2] = Float.valueOf(splitted[2]);
                    calibrationData.setCoordinates(startCoordinates);
                }


                if (calibrationData != null) {
                    // save new calibrated airpressure and azimuth
                    calibrationData.setAirPressure(airPressure);
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

    private boolean alreadyCalibrated() {
        CalibratePersistance calibratePersistance = new CalibratePersistanceImpl(view.getContext());
        return calibratePersistance.load() != null;
    }

    //todo: remove
    private void save() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/Coordinates/RecordData");
        if (!dir.exists()) {
            dir.mkdirs();
        }


        File file = new File(dir, "steps.txt");

        FileOutputStream outputStream;


        try {
            outputStream = new FileOutputStream(file);
            for (StepData stepData: stepList) {
                StringBuilder builder = new StringBuilder();
                builder.append(stepData.getCoords()[0] + ";" + stepData.getCoords()[1] + ";" + stepData.getCoords()[2]);
                outputStream.write(builder.toString().getBytes());
                outputStream.write(System.lineSeparator().getBytes());
            }



            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // calculate the distance from 2 points in 2 dimensions
    private float calcDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow((x1-x2), 2) + Math.pow((y1-y2), 2));
    }
}
