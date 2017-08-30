package de.htwberlin.f4.ai.ma.android.measure;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.carol.bvg.R;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.htwberlin.f4.ai.ma.android.BaseActivity;
import de.htwberlin.f4.ai.ma.android.calibrate.CalibratePersistance;
import de.htwberlin.f4.ai.ma.android.calibrate.CalibratePersistanceImpl;
import de.htwberlin.f4.ai.ma.android.measure.barcode.BarcodeCaptureActivity;
import de.htwberlin.f4.ai.ma.android.sensors.Sensor;
import de.htwberlin.f4.ai.ma.android.sensors.SensorChecker;
import de.htwberlin.f4.ai.ma.android.sensors.SensorCheckerImpl;
import de.htwberlin.f4.ai.ma.android.sensors.SensorData;
import de.htwberlin.f4.ai.ma.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ma.android.sensors.SensorDataModelImpl;
import de.htwberlin.f4.ai.ma.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ma.android.sensors.SensorType;
import de.htwberlin.f4.ai.ma.measurement.IndoorMeasurement;
import de.htwberlin.f4.ai.ma.measurement.IndoorMeasurementFactory;
import de.htwberlin.f4.ai.ma.measurement.IndoorMeasurementType;

import de.htwberlin.f4.ai.ma.measurement.WKT;
import de.htwberlin.f4.ai.ma.measurement.modules.stepdirection.StepDirection;
import de.htwberlin.f4.ai.ma.measurement.modules.stepdirection.StepDirectionDetectListener;
import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.edge.EdgeImpl;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.node.NodeFactory;
import de.htwberlin.f4.ai.ma.node.fingerprint.FingerprintImpl;
import de.htwberlin.f4.ai.ma.node.fingerprint.SignalInformation;
import de.htwberlin.f4.ai.ma.node.fingerprint.SignalStrengthInformation;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerFactory;
import de.htwberlin.f4.ai.ma.MaxPictureActivity;
import de.htwberlin.f4.ai.ma.persistence.calculations.FoundNode;

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
    private float lowpassFilterValue;
    private boolean useStepDirectionDetect;
    private float barometerThreshold;

    private Node startNode;
    private Node targetNode;
    private Node measuredNode; // node retrieved from wifi

    private List<StepData> stepList;
    private float[] coords = new float[3];
    private boolean handycapFriendly;


    private boolean measurementRunning; // to deactivate wifi / qr code localization during measurement


    @Override
    public void setView(MeasureView view) {
        this.view = view;
    }


    @Override
    public void onStartClicked() {
        measurementRunning = true;

        // check for calibration
        if (!alreadyCalibrated()) {
            showStepCalibrationRequiredDialog();
            return;
        }

        // check if every sensors required is available
        if (!sensorsAvailable(measurementType)) {
            showSensorsNotAvailableDialog();
            return;
        }

        stepList = new ArrayList<>();
        stepCount = 0;
        edgeDistance = 0f;
        coords = new float[3];
        calibrated = false;
        sensorDataModel = new SensorDataModelImpl();
        indoorMeasurement = IndoorMeasurementFactory.getIndoorMeasurement(view.getContext());

        view.updateStepCount(stepCount);
        view.updateDistance(edgeDistance);

        // check if the startnode already got coordinates
        if (startNode.getCoordinates().length() > 0) {
            coords = WKT.strToCoord(startNode.getCoordinates());
            // update coordinates in view
            view.updateCoordinates(coords[0], coords[1], coords[2]);
        } else {
            view.updateCoordinates(0.0f, 0.0f, 0.0f);
        }



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
                        handleNewStep();
                        // fix first step bug...
                        // first step won't get recognized by sensor so we just fake the step
                        // and assume the user walks atleast for the two first steps in  the
                        // same direction
                        if (stepCount == 1) {
                            handleNewStep();
                        }
                        break;
                    default:
                        break;

                }
            }
        });

        indoorMeasurement.setStepDirectionListener(new StepDirectionDetectListener() {
            @Override
            public void onDirectionDetect(final StepDirection stepDirection) {
                showStepDirectionDialog(stepDirection);
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
                                       SensorType.BAROMETER);

        // start compass with ui delay
        indoorMeasurement.startSensors(Sensor.SENSOR_RATE_UI, compassType);

        calibrate();
        showWaitForCalibrationDialoag();

    }

    private boolean sensorsAvailable(IndoorMeasurementType indoorMeasurementType) {
        SensorChecker sensorChecker = new SensorCheckerImpl(view.getContext());
        boolean result = sensorChecker.checkSensor(indoorMeasurementType);

        return result;
    }

    private void showSensorsNotAvailableDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
        alertDialogBuilder.setTitle("Ungültige Messvariante");
        alertDialogBuilder.setMessage("Ein oder mehrere Sensor(en) werden von ihrem Gerät nicht unterstützt. Bitte wählen Sie eine andere Messvariante!");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setIcon(R.drawable.error);

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showStepCalibrationRequiredDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
        alertDialogBuilder.setTitle("Kalibrierung notwendig");
        alertDialogBuilder.setMessage("Bitte führen Sie zuerst eine Schrittkalibrierung durch!");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setIcon(R.drawable.error);

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                BaseActivity activity = (BaseActivity) view;
                activity.loadCalibrate();
                dialog.dismiss();
            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showWaitForCalibrationDialoag() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
        alertDialogBuilder.setMessage("Bitte warten");
        alertDialogBuilder.setTitle("Kalibrierung im gange");
        calibrationDialog = alertDialogBuilder.create();
        calibrationDialog.setCancelable(false);
        calibrationDialog.show();
    }

    private void showMeasureFinishDialog() {
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

    private void showStepDirectionDialog(StepDirection stepDirection) {
        if (stepDirection != StepDirection.FORWARD) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
            alertDialogBuilder.setTitle("Falsche Schrittrichtung");
            alertDialogBuilder.setMessage("Es wurde ein " + stepDirection + "schritt festgestellt. Falls dies so ist, starten Sie die Messung bitte neu");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setIcon(R.drawable.error);

            alertDialogBuilder.setPositiveButton("Ja, das ist korrekt", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // reset
                    indoorMeasurement.stop();
                    stepList = new ArrayList<>();
                    stepCount = 0;
                    edgeDistance = 0f;

                    view.updateStepCount(stepCount);
                    view.updateDistance(edgeDistance);
                    view.disableStop();
                    view.disableAdd();
                    view.enableStart();


                    // check if the startnode already got coordinates
                    if (startNode.getCoordinates().length() > 0) {
                        coords = WKT.strToCoord(startNode.getCoordinates());
                        // update coordinates in view
                        view.updateCoordinates(coords[0], coords[1], coords[2]);
                    } else {
                        coords[0] = 0.0f;
                        coords[1] = 0.0f;
                        coords[2] = 0.0f;
                        view.updateCoordinates(0.0f, 0.0f, 0.0f);
                    }
                }
            });

            alertDialogBuilder.setNegativeButton("Nein, weiter", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    @Override
    public void onStopClicked() {
        if (indoorMeasurement != null) {
            indoorMeasurement.stop();
        }

        if (coords != null) {
            showMeasureFinishDialog();
        }

        measurementRunning = false;
    }

    private void saveMeasurementData() {
        // make sure the target isnt the nullpoint, nullpoint coordinate change isnt allowed!
        //if its the nullpoint, we just save the edge data and dont update coordinates
        if (!targetNode.getAdditionalInfo().contains("NULLPOINT")) {
            targetNode.setCoordinates(WKT.coordToStr(coords));
            view.updateTargetNodeCoordinates(coords[0], coords[1], coords[2]);
        }



        List<String> stepCoords = new ArrayList<>();
        // convert coordinates to string and save into list for edge
        for (StepData stepData : stepList) {
            stepCoords.add(WKT.coordToStr(stepData.getCoords()));
        }

        DatabaseHandler databaseHandler = DatabaseHandlerFactory.getInstance(view.getContext());

        Edge edge = databaseHandler.getEdge(startNode, targetNode);
        boolean edgeFound;

        // if there is no edge between start and targetnode yet, we create a new one
        if (edge == null) {
            edge = new EdgeImpl(startNode, targetNode, handycapFriendly, stepCoords, 0, "");
            edgeFound = false;
        } else {
            edge.setAccessibility(handycapFriendly);
            edge.getStepCoordsList().clear();
            edge.getStepCoordsList().addAll(stepCoords);
            edgeFound = true;
        }

        // since the distance is calculated in meters we need to convert it into cm for edge weight
        //int weightCm = Math.round(edgeDistance*100);
        edge.setWeight(edgeDistance);
        // insert edge when there is no existing edge yet
        if (!edgeFound) {
            databaseHandler.insertEdge(edge);
        }
        // update edge if there was an existing edge
        else {
            databaseHandler.updateEdge(edge);
        }
        // update our targetnode
        databaseHandler.updateNode(targetNode, targetNode.getId());
        // update view with new edge data
        view.updateEdge(edge);
    }


    // just a temporary solution if we want to get coords without doing a step
    @Override
    public void onAddClicked() {
        handleNewStep();
    }


    @Override
    public void onPause() {
        if (indoorMeasurement != null) {
            indoorMeasurement.stop();
        }
        measurementRunning = false;
    }

    @Override
    public void onResume() {
        handleNodeSelection(startNode, targetNode);
        // get settings from sharedpreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        lowpassFilterValue = Float.valueOf(sharedPreferences.getString("pref_lowpass_value", "0.1"));
        String type = sharedPreferences.getString("pref_measurement_type", "Variante B");
        measurementType = IndoorMeasurementType.fromString(type);
        useStepDirectionDetect = sharedPreferences.getBoolean("pref_stepdirection", false);
        barometerThreshold = Float.valueOf(sharedPreferences.getString("pref_barometer_threshold", "0.1"));

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

    @Override
    public void onEdgeDetailsClicked() {
        DatabaseHandler databaseHandler = DatabaseHandlerFactory.getInstance(view.getContext());
        Edge edge = new EdgeImpl(startNode, targetNode, false, 0);
        if (databaseHandler.checkIfEdgeExists(edge)) {
            BaseActivity activity = (BaseActivity) view;
            activity.loadEdgeDetails(startNode.getId(), targetNode.getId());
        }
    }

    @Override
    public void onStartNodeImageClicked() {
        if (startNode != null && startNode.getPicturePath() != null) {
            // using activity from johann
            Intent intent = new Intent(view.getContext(), MaxPictureActivity.class);
            intent.putExtra("picturePath", startNode.getPicturePath());
            BaseActivity activity = (BaseActivity) view;
            activity.startActivity(intent);
        }
    }

    @Override
    public void onTargetNodeImageClicked() {
        if (targetNode != null && targetNode.getPicturePath() != null) {
            // using activity from johann
            Intent intent = new Intent(view.getContext(), MaxPictureActivity.class);
            intent.putExtra("picturePath", targetNode.getPicturePath());
            BaseActivity activity = (BaseActivity) view;
            activity.startActivity(intent);
        }
    }

    @Override
    public void onLocateWifiClicked() {
        if (!measurementRunning) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
            alertDialogBuilder.setTitle("Startposition");
            alertDialogBuilder.setMessage("Wollen Sie die Startposition per WLAN ermitteln?");
            alertDialogBuilder.setCancelable(true);
            alertDialogBuilder.setIcon(R.drawable.locate_wifi);

            alertDialogBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    WifiManager wifiManager = (WifiManager) view.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifiManager.startScan();
                    List<ScanResult> wifiScanList = wifiManager.getScanResults();

                    final ArrayList<String> wifiNamesList = new ArrayList<>();
                    for (ScanResult sr : wifiScanList) {
                        if (!wifiNamesList.contains(sr.SSID) && !sr.SSID.equals("")) {
                            wifiNamesList.add(sr.SSID);
                        }
                    }

                    final CharSequence wifiArray[] = new CharSequence[wifiNamesList.size()-1];
                    for (int i = 0; i < wifiArray.length; i++) {
                        wifiArray[i] = wifiNamesList.get(i);
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("WLAN wählen");
                    builder.setItems(wifiArray, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            getMeasuredNode(wifiNamesList.get(which), 3);
                        }
                    });
                    builder.show();
                }
            });

            alertDialogBuilder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }



    }


    @Override
    public void onLocateQrClicked() {
        if (!measurementRunning) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
            alertDialogBuilder.setTitle("Startposition");
            alertDialogBuilder.setMessage("Wollen Sie die Startposition per QR-Code ermitteln?");
            alertDialogBuilder.setCancelable(true);
            alertDialogBuilder.setIcon(R.drawable.locate_qr);

            alertDialogBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(view.getContext().getApplicationContext(), BarcodeCaptureActivity.class);
                    Activity activity = (Activity) view;
                    activity.startActivityForResult(intent, 1);
                }
            });

            alertDialogBuilder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }


    //json string: {"id": "nodeid","coordinates": "POINT Z(0.1 0.2 0.3)"}
    @Override
    public void onQrResult(String qr) {

        try {
            JSONObject jsonObject = new JSONObject(qr);
            String id = jsonObject.getString("id");
            String coordinates = jsonObject.getString("coordinates");

            DatabaseHandler databaseHandler = DatabaseHandlerFactory.getInstance(view.getContext());

            // check if node from qr code already exists
            Node node = databaseHandler.getNode(id);

            // node exists
            if (node != null) {
                // update existing node with coords from qr code
                node.setCoordinates(coordinates);
                databaseHandler.updateNode(node, node.getId());
                // update ui
                view.setStartNode(node);
            }
            // new node
            else {
                node = NodeFactory.createInstance(id, null, null, coordinates, null, null);
                databaseHandler.insertNode(node);
                view.setStartNode(node);
            }

        } catch (JSONException e) {
            Toast toast = Toast.makeText(view.getContext(), "Ungültiger QR-Code", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    @Override
    public void onNullpointCheckedStartNode(boolean checked) {

        DatabaseHandler databaseHandler = DatabaseHandlerFactory.getInstance(view.getContext());
        if (startNode != null) {
            if (checked) {
                startNode.setAdditionalInfo("NULLPOINT");
                //float[] coord = new float[]{0.0f, 0.0f, 0.0f};
                //startNode.setCoordinates(WKT.coordToStr(coord));
            } else {
                startNode.setAdditionalInfo("");
            }
            databaseHandler.updateNode(startNode, startNode.getId());
        }
    }




    // from johann, modified
    private void getMeasuredNode(final String wlanName, final int times) {

        WifiManager wifiManager = (WifiManager) view.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        Multimap<String, Integer> multiMap = ArrayListMultimap.create();
        for (int i = 0; i < times; i++) {

            wifiManager.startScan();

            List<ScanResult> wifiScanList = wifiManager.getScanResults();

            if(wifiScanList.get(0).timestamp == 0 && times == 1) {
                return;
            }


            for (final ScanResult sr : wifiScanList) {
                if (sr.SSID.equals(wlanName)) {
                    multiMap.put(sr.BSSID, sr.level);
                }
            }

            wifiScanList.clear();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // find node
        Node node = makeFingerprint(multiMap);
        if (node != null) {
            measuredNode = node;
            Toast toast = Toast.makeText(view.getContext(), "Node: " + node.getId(), Toast.LENGTH_SHORT);
            toast.show();
            view.setStartNode(measuredNode);
        } else {
            Toast toast = Toast.makeText(view.getContext(), "Es wurde kein Ort gefunden", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    // from johann, modified
    private Node makeFingerprint(Multimap<String, Integer> multiMap) {


        Set<String> bssid = multiMap.keySet();
        DatabaseHandler databaseHandler = DatabaseHandlerFactory.getInstance(view.getContext());
        final List<Node> actuallyNode = new ArrayList<>();
        final List<SignalInformation> signalInformationList = new ArrayList<>();



        for (String s : bssid) {
            int value = 0;
            int counter = 0;

            for (int test : multiMap.get(s)) {
                counter++;
                value += test;
            }
            value = value / counter;

            //List<de.htwberlin.f4.ai.ma.fingerprint.Node.SignalInformation> signalInformationList = new ArrayList<>();
            List<SignalStrengthInformation> SsiList = new ArrayList<>();
            SignalStrengthInformation signal = new SignalStrengthInformation(s, value);
            SsiList.add(signal);
            SignalInformation signalInformation = new SignalInformation("", SsiList);
            signalInformationList.add(signalInformation);

        }


        FoundNode foundNode = databaseHandler.calculateNodeId(new FingerprintImpl("", signalInformationList));
        Node result = null;
        if (foundNode != null) {
            result = databaseHandler.getNode(foundNode.getId());
        }

        return result;
    }


    private void handleNewStep() {
        // with each step we get the new position
        stepCount++;
        // convert WKT String back to float[]
        float[] newStepCoords = WKT.strToCoord(indoorMeasurement.getCoordinates());
        if (newStepCoords != null) {
            // calculate distance from previous step to new step
            float stepDistance = calcDistance(coords[0], coords[1], coords[2], newStepCoords[0], newStepCoords[1], newStepCoords[2]);
            edgeDistance += stepDistance;

            view.updateDistance(edgeDistance);
            view.updateCoordinates(newStepCoords[0], newStepCoords[1], newStepCoords[2]);
            StepData stepData = new StepData();
            stepData.setStepName("Step " + stepCount);
            coords = new float[newStepCoords.length];
            System.arraycopy(newStepCoords, 0, coords, 0, newStepCoords.length);
            stepData.setCoords(newStepCoords);
            stepList.add(stepData);

        }

        view.updateStepCount(stepCount);
    }

    private void handleNodeSelection(Node start, Node target) {
        // check if startnode and targetnode was selected
        if (start != null && target != null) {
            // update coordinates of startnode
            if (start.getCoordinates().length() > 0) {
                float[] coordinates = WKT.strToCoord(start.getCoordinates());

                view.updateStartNodeCoordinates(coordinates[0], coordinates[1], coordinates[2]);
            } else {
                view.updateStartNodeCoordinates(0f, 0f, 0f);
            }
            // update coordinates of targetnode
            if (target.getCoordinates().length() > 0) {
                float[] coordinates = WKT.strToCoord(target.getCoordinates());

                view.updateTargetNodeCoordinates(coordinates[0], coordinates[1], coordinates[2]);
            } else {
                view.updateTargetNodeCoordinates(0f, 0f, 0f);
            }

            // check if start and targetnode are different, if yes we have to update edge information
            // and enable measurement start
            boolean different = checkNodesDifferent(start, target);
            if (different) {
                // make sure that both nodes aren't nullpoints, because its not allowed to measure between two nullpoints
                if ((start.getAdditionalInfo().contains("NULLPOINT") && !target.getAdditionalInfo().contains("NULLPOINT")) ||
                        (!start.getAdditionalInfo().contains("NULLPOINT") && target.getAdditionalInfo().contains("NULLPOINT"))) {
                    view.enableStart();

                    DatabaseHandler databaseHandler = DatabaseHandlerFactory.getInstance(view.getContext());

                    // check if edge already exists
                    Edge existingEdge = databaseHandler.getEdge(startNode, targetNode);
                    if (existingEdge != null) {
                        // if we found the correct edge update view with correct data
                        view.updateEdge(existingEdge);

                    }
                    // if there isn't an edge, update view with placeholder data
                    else {
                        Edge placeHolderEdge = new EdgeImpl(null, null, true, 0);
                        view.updateEdge(placeHolderEdge);
                    }
                }
                // if both nodes are nullpoints
                else {
                    view.disableStart();
                }

            }
            // if start and targetnode are equal just update the edge with placeholder
            else {
                view.disableStart();
                Edge placeHolderEdge = new EdgeImpl(null, null, true, 0);
                view.updateEdge(placeHolderEdge);
            }
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
        pressureCalibration = new MeasureCalibration(sensorDataModel);

        pressureCalibration.setListener(new MeasureCalibrationListener() {
            @Override
            public void onFinish(float airPressure) {
                calibrationDialog.dismiss();
                timerHandler.removeCallbacks(pressureCalibration);
                calibrated = true;
                // load steplength and stepperiod calibration
                CalibratePersistance calibratePersistance = new CalibratePersistanceImpl(view.getContext());
                CalibrationData calibrationData = calibratePersistance.load();

                if (calibrationData != null) {
                    // get coordinates from startnode and initialize measurement with those.
                    // if the node doesn't have any coordinates we initialize with 0,0,0
                    String startCoordinatesStr = startNode.getCoordinates();
                    if (startCoordinatesStr != null && startCoordinatesStr.length() > 0) {
                        float[] startCoordinates = WKT.strToCoord(startCoordinatesStr);

                        calibrationData.setCoordinates(startCoordinates);
                    }

                    calibrationData.setIndoorMeasurementType(measurementType);
                    calibrationData.setLowpassFilterValue(lowpassFilterValue);
                    calibrationData.setUseStepDirection(useStepDirectionDetect);
                    calibrationData.setBarometerThreshold(barometerThreshold);

                    // save new calibrated airpressure and azimuth
                    calibrationData.setAirPressure(airPressure);
                    // calibrate the indoormeasurement
                    indoorMeasurement.calibrate(calibrationData);
                    // start step detector with 0 delay
                    indoorMeasurement.startSensors(Sensor.SENSOR_RATE_FASTEST,
                            SensorType.STEPCOUNTER);
                    // start measurement
                    indoorMeasurement.start();
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

    // calculate the distance from 2 points in 3 dimensions
    private float calcDistance(float x1, float y1, float z1, float x2, float y2, float z2) {
        return (float) Math.sqrt(Math.pow((x1-x2), 2) + Math.pow((y1-y2), 2) + Math.pow((z1-z2),2));
    }


}
