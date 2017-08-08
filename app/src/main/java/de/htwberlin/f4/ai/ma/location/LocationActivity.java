package de.htwberlin.f4.ai.ma.location;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.carol.bvg.R;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

//import de.htwberlin.f4.ai.ma.persistence.fingerprint.Fingerprint;
//import de.htwberlin.f4.ai.ma.persistence.fingerprint.FingerprintFactory;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.node.NodeFactory;
import de.htwberlin.f4.ai.ma.node.SignalInformation;
import de.htwberlin.f4.ai.ma.node.SignalStrengthInformation;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerImplementation;


public class LocationActivity extends AppCompatActivity {

    //private List<String> macAdresses = new ArrayList<>();
    //private int count = 0;
    Button measurementButton;
    Button measurementButtonMoreTomes;

    //String[] permissions;
    //private Fingerprint fingerprint = FingerprintFactory.createInstance();
    private DatabaseHandler databaseHandler;
    private SharedPreferences sharedPreferences;
    private NodeFactory nodeFactory;
    ListView listView;
    private String settings;
    private Spinner dropdown;
    private LocationResultAdapter resultAdapterdapter;
    private String foundNodeName;
    private WifiManager mainWifiObj;
    private Multimap<String, Integer> multiMap;
    private long timestampWifiManager = 0;

    private int locationsCounter;


    boolean movingAverage;
    boolean kalmanFilter;
    boolean euclideanDistance;
    boolean knnAlgorithm;

    private int knnValue;
    private int movingAverageOrder;
    private int kalmanValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

       /* permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION};
*/
        mainWifiObj= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        measurementButton = (Button) findViewById(R.id.b_measurement);
        measurementButtonMoreTomes = (Button) findViewById(R.id.b_measurementMoreTimes);
        listView = (ListView) findViewById(R.id.LV_results);

        //check Preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        movingAverage = sharedPreferences.getBoolean("pref_movingAverage", true);
        kalmanFilter = sharedPreferences.getBoolean("pref_kalman", false);
        euclideanDistance = sharedPreferences.getBoolean("pref_euclideanDistance", false);
        knnAlgorithm = sharedPreferences.getBoolean("pref_knnAlgorithm", true);

        locationsCounter = sharedPreferences.getInt("locationsCounter", -1);
        Log.d("LocationActivity", "locationsCounter onCreate= " + locationsCounter);
        if (locationsCounter == -1) {
            locationsCounter = 0;
        }

        movingAverageOrder = Integer.parseInt(sharedPreferences.getString("pref_movivngAverageOrder", "3"));
        knnValue = Integer.parseInt(sharedPreferences.getString("pref_knnNeighbours", "3"));
        kalmanValue = Integer.parseInt(sharedPreferences.getString("pref_kalmanValue","2"));



        settings = "Mittelwert: " + movingAverage + "\r\nOrdnung: " + sharedPreferences.getString("pref_movivngAverageOrder", "3")
                + "\r\nKalman Filter: " + kalmanFilter +"\r\nKalman Wert: "+ sharedPreferences.getString("pref_kalmanValue","2")
                + "\r\nEuclidische Distanz: " + euclideanDistance
                + "\r\nKNN: " + knnAlgorithm+ "\r\nKNN Wert: "+ sharedPreferences.getString("pref_knnNeighbours", "3") ;

        //read Json file
        //JsonReader jsonReader = new JsonReader();
        //final List<Node> allNodes = jsonReader.initializeNodeFromJson(this);

        databaseHandler = new DatabaseHandlerImplementation(this);
        final List<Node> allNodes = databaseHandler.getAllNodes();


        //a dropdown list with name of all existing nodes
        dropdown = (Spinner) findViewById(R.id.spinner);
        final ArrayList<String> nodeItems = new ArrayList<>();
        for (de.htwberlin.f4.ai.ma.node.Node node : allNodes) {
            nodeItems.add(node.getId());
        }
        Collections.sort(nodeItems);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nodeItems);
        dropdown.setAdapter(adapter);




        //fill result list with content
        //final ArrayList<LocationResultImplementation> arrayOfResults = loadJson();

        final ArrayList<LocationResultImplementation> allResults = databaseHandler.getAllLocationResults();

        resultAdapterdapter = new LocationResultAdapter(this, allResults);
        listView.setAdapter(resultAdapterdapter);


        //delete entry with long click
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Eintrag löschen")
                        .setMessage("Möchten sie den Eintrag löschen?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                databaseHandler.deleteLocationResult(allResults.get(position));
                                resultAdapterdapter.remove(allResults.get(position));
                                resultAdapterdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return false;
            }
        });


        /*
        //set all preferences
        fingerprint.setMovingAverage(movingAverage);
        fingerprint.setKalman(kalmanFilter);
        fingerprint.setEuclideanDistance(euclideanDistance);
        fingerprint.setKNN(knnAlgorithm);
*/

        /*fingerprint.setAverageOrder(Integer.parseInt(sharedPreferences.getString("pref_movivngAverageOrder", "3")));
        fingerprint.setKNNValue(Integer.parseInt(sharedPreferences.getString("pref_knnNeighbours", "3")));
        fingerprint.setKalmanValue(Integer.parseInt(sharedPreferences.getString("pref_kalmanValue","2")));

        fingerprint.setAllNodes(allNodes);
*/

        if (measurementButton != null) {
            measurementButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getMeasuredNode(1);
                }
            });
        }

        if (measurementButtonMoreTomes != null) {
            measurementButtonMoreTomes.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getMeasuredNode(10);
                }
            });
        }

    }


    /**
     * check wlan signal strength and and save to multimap
     * @param times the measured time one or ten seconds
     */
    private void getMeasuredNode(final int times) {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        //registerReceiver(mWifiScanReceiver, intentFilter);

        final TextView textView = (TextView) findViewById(R.id.tx_Location);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.proBar_location);

        textView.setText("POI wird gesucht");

        new Thread(new Runnable() {
            public void run() {
                multiMap = ArrayListMultimap.create();
                for (int i = 0; i < times; i++) {
                    progressBar.setMax(times);
                    progressBar.setProgress(i + 1);


                    mainWifiObj.startScan();

                    final TextView testTimestampTextview = (TextView) findViewById(R.id.tx_test);
                    EditText editText = (EditText) findViewById(R.id.edTx_WlanNameLocation);
                    String wlanName = editText.getText().toString();

                    List<ScanResult> wifiScanList = mainWifiObj.getScanResults();

                    //check if there is a new measurement
                     if(wifiScanList.get(0).timestamp == timestampWifiManager && times == 1)
                        {
                            LocationActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    textView.setText("Bitte neu versuchen");
                                }
                            });

                            return;
                        }

                        timestampWifiManager = wifiScanList.get(0).timestamp;
                        Log.d("timestamp", String.valueOf(timestampWifiManager));

                    for (final ScanResult sr : wifiScanList) {
                        LocationActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                testTimestampTextview.setText(String.valueOf(sr.timestamp));
                            }
                        });

                        if (sr.SSID.equals(wlanName)) {
                            multiMap.put(sr.BSSID, sr.level);
                            long timestamp = sr.timestamp;
                            Log.d("timestamp Sunshine", String.valueOf(timestamp));
                        }
                    }

                    wifiScanList.clear();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                makeFingerprint(times);
            }
        }).start();
        //return actuallyNode;
    }

    /**
     * if times measurement is more than one second make a average of values. Try to start a fingerprint and calculate position.
     * @param measuredTime the measured time
     */
    private void makeFingerprint(final int measuredTime) {
        final TextView textView = (TextView) findViewById(R.id.tx_Location);

        Set<String> bssid = multiMap.keySet();

        final List<Node> actuallyNode = new ArrayList<>();

        final List<SignalInformation> signalInformationList = new ArrayList<>();

        for (String blub : bssid) {
            int value = 0;
            int counter = 0;

            for (int test : multiMap.get(blub)) {
                counter++;
                value += test;
            }
            value = value / counter;

            //List<de.htwberlin.f4.ai.ma.fingerprint.Node.SignalInformation> signalInformationList = new ArrayList<>();
            List<SignalStrengthInformation> signalStrenghtList = new ArrayList<>();
            SignalStrengthInformation signal = new SignalStrengthInformation(blub, value);
            signalStrenghtList.add(signal);
            SignalInformation signalInformation = new SignalInformation("", signalStrenghtList);
            signalInformationList.add(signalInformation);

        }

        Node node = nodeFactory.createInstance(null, 0, "", signalInformationList, "", "", "");

        foundNodeName = databaseHandler.calculateNodeId(node);


/*
        fingerprint.setMovingAverage(movingAverage);
        fingerprint.setKalman(kalmanFilter);
        fingerprint.setEuclideanDistance(euclideanDistance);
        fingerprint.setKNN(knnAlgorithm);

        //actuallyNode.add(node);
        //fingerprint.setActuallyNode(actuallyNode);
        //foundNodeName = fingerprint.getCalculatedNode();
        */

        LocationActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                LocationResultImplementation locationResult;
                if (foundNodeName != null) {
                    textView.setText(foundNodeName);

                    // TODO percentage einfügen
                    //locationResult = new LocationResultImplementation(locationsCounter, settings, String.valueOf(measuredTime), dropdown.getSelectedItem().toString(), foundNodeName + " "+fingerprint.getPercentage() +"%");
                    locationResult = new LocationResultImplementation(locationsCounter, settings, String.valueOf(measuredTime), dropdown.getSelectedItem().toString(), foundNodeName);
                } else {
                    textView.setText("kein Node gefunden");
                    locationResult = new LocationResultImplementation(locationsCounter, settings, String.valueOf(measuredTime), dropdown.getSelectedItem().toString(), "kein Node gefunden");
                }
                //makeJson(locationResult);

                locationsCounter++;
                Log.d("LocationActivity", "locationsCounter = " + locationsCounter);
                sharedPreferences.edit().putInt("locationsCounter", locationsCounter).apply();
                databaseHandler.insertLocationResult(locationResult);

                resultAdapterdapter.add(locationResult);
                resultAdapterdapter.notifyDataSetChanged();
            }
        });

    }

}