package de.htwberlin.f4.ai.ma.prototype_temp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.htwberlin.f4.ai.ma.fingerprint_generator.fingerprint.Fingerprint;
import de.htwberlin.f4.ai.ma.fingerprint_generator.fingerprint.FingerprintFactory;
import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;
import de.htwberlin.f4.ai.ma.fingerprint_generator.node.NodeFactory;
import de.htwberlin.f4.ai.ma.fingerprint_generator.node.SignalInformation;
import de.htwberlin.f4.ai.ma.fingerprint_generator.node.SignalStrengthInformation;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.JsonReader;

public class LocationActivity extends AppCompatActivity {
    private List<String> macAdresses = new ArrayList<>();
    private int count = 0;
    String[] permissions;
    Fingerprint fingerprint = FingerprintFactory.getInstance();
    NodeFactory nodeFactory;
    DatabaseHandler databaseHandler;

    ListView listView;
    String settings;
    Spinner dropdown;
    LocationResultAdapter resultAdapterdapter;
    String actually;
    WifiManager mainWifiObj;
    Multimap<String, Integer> multiMap;
    long timestampWifiManager = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        mainWifiObj= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        Button measurementButton = (Button) findViewById(R.id.b_measurement);
        Button measurementButtonMoreTomes = (Button) findViewById(R.id.b_measurementMoreTimes);
        listView = (ListView) findViewById(R.id.LV_results);

        //check Preferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean movingAverage = sharedPrefs.getBoolean("pref_movingAverage", true);
        boolean kalmanFilter = sharedPrefs.getBoolean("pref_kalman", false);
        boolean euclideanDistance = sharedPrefs.getBoolean("pref_euclideanDistance", false);
        boolean knnAlgorithm = sharedPrefs.getBoolean("pref_knnAlgorithm", true);


        settings = "Mittelwert: " + movingAverage + "\r\nOrdnung: " + sharedPrefs.getString("pref_movivngAverageOrder", "3")
                + "\r\nKalmann Filter: " + kalmanFilter +"\r\nKalman Wert: "+ sharedPrefs.getString("pref_kalmanValue","2")
                + "\r\nEculidische Distanz: " + euclideanDistance
                + "\r\nKNN: " + knnAlgorithm+ "\r\nKNN Wert: "+ sharedPrefs.getString("pref_knnNeighbours", "3") ;

        //read Json file
        JsonReader jsonReader = new JsonReader();
        final List<Node> allNodes = jsonReader.initializeNodeFromJson(this);


        //a dropdown list with name of all existing nodes
        dropdown = (Spinner) findViewById(R.id.spinner);
        final ArrayList<String> items = new ArrayList<>();
        for (de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node id : allNodes) {
            items.add(id.getId().toString());
        }
        Collections.sort(items);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        //fill result list with content
        final ArrayList<LocationResult> arrayOfResults = loadJson();
        resultAdapterdapter = new LocationResultAdapter(this, arrayOfResults);
        listView.setAdapter(resultAdapterdapter);

        //delete entry with long click
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Eintrag löschen")
                        .setMessage("möchten sie den Eintrag löschen?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    String jsonString = loadJSON(getApplicationContext());
                                    JSONObject jsonObj = new JSONObject(jsonString);
                                    JSONArray jsonResult = jsonObj.getJSONArray("Results");
                                    jsonResult.remove(position);
                                    saveJsonObject(jsonObj);
                                    resultAdapterdapter.remove(arrayOfResults.get(position));
                                    resultAdapterdapter.notifyDataSetChanged();
                                } catch (final JSONException e) {
                                    Log.e("JSON", "Json parsing error: " + e.getMessage());
                                }
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


//        de.htwberlin.f4.ai.ma.fingerprint.Node actuallyNodeOne = new Node();
//        de.htwberlin.f4.ai.ma.fingerprint.Node actuallyNodeTwo = new Node();
//
//        List<Node.SignalInformation> signalInformationList = new ArrayList<>();
//        List<Node.SignalStrengthInformation> signalStrenghtList = new ArrayList<>();
//        Node.SignalStrengthInformation signal = new Node.SignalStrengthInformation("00:81:c4:9d:b3:60",-60);
//        signalStrenghtList.add(signal);
//        Node.SignalInformation signalInformation = new Node.SignalInformation("time1",signalStrenghtList);
//        signalInformationList.add(signalInformation);
//
//        List<Node.SignalInformation> signalInformationListTwo = new ArrayList<>();
//        List<Node.SignalStrengthInformation> signalStrenghtListTwo = new ArrayList<>();
//        Node.SignalStrengthInformation signalTwo = new Node.SignalStrengthInformation("00:81:c4:9d:b3:6f",-71);
//        signalStrenghtListTwo.add(signalTwo);
//        Node.SignalInformation signalInformationTwo = new Node.SignalInformation("time2",signalStrenghtListTwo);
//        signalInformationListTwo.add(signalInformationTwo);
//
//        actuallyNodeOne.setId("test");
//        actuallyNodeOne.setSignalInformationList(signalInformationList);
//
//        actuallyNodeTwo.setId("test2");
//        actuallyNodeTwo.setSignalInformationList(signalInformationListTwo);
//
//        actuallyNode.add(actuallyNodeOne);
//        actuallyNode.add(actuallyNodeTwo);


        //set all preferences
        fingerprint.setMovingAverage(movingAverage);
        fingerprint.setKalman(kalmanFilter);
        fingerprint.setEuclideanDistance(euclideanDistance);
        fingerprint.setKNN(knnAlgorithm);

        fingerprint.setAverageOrder(Integer.parseInt(sharedPrefs.getString("pref_movivngAverageOrder", "3")));
        fingerprint.setKNNValue(Integer.parseInt(sharedPrefs.getString("pref_knnNeighbours", "3")));
        fingerprint.setKalmanValue(Integer.parseInt(sharedPrefs.getString("pref_kalmanValue","2")));

        fingerprint.setAllNodes(allNodes);

        if (measurementButton != null) {
            measurementButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //List<de.htwberlin.f4.ai.ma.fingerprint.Node> nodeList = getMeasuredNode(1);
                    getMeasuredNode(1);

//                    fingerprint.setActuallyNode(nodeList);
//                    String actually = fingerprint.getCalculatedPOI();
//
//                    TextView textView =(TextView)findViewById(R.id.tx_Location);
//                    if(actually!=null){
//                        textView.setText(actually);
//                    }
//                    else {
//                        textView.setText("kein POI gefunden");
//                    }
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

//    private List<de.htwberlin.f4.ai.ma.fingerprint.Node> getMeasuredNode() {
//        if(hasPermissions(LocationActivity.this, permissions)){
//
//            EditText editText = (EditText)findViewById(R.id.edTx_WlanNameLocation);
//            String wlanName = editText.getText().toString();
//            WifiManager mainWifiObj;
//            mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//            List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
//            List<de.htwberlin.f4.ai.ma.fingerprint.Node> actuallyNode = new ArrayList<>();
//
//            for (ScanResult sr : wifiScanList) {
//
//                if (sr.SSID.equals(wlanName)) {
//                    List<de.htwberlin.f4.ai.ma.fingerprint.Node.SignalInformation> signalInformationList = new ArrayList<>();
//                    List<de.htwberlin.f4.ai.ma.fingerprint.Node.SignalStrengthInformation> signalStrenghtList = new ArrayList<>();
//                    de.htwberlin.f4.ai.ma.fingerprint.Node.SignalStrengthInformation signal = new de.htwberlin.f4.ai.ma.fingerprint.Node.SignalStrengthInformation(sr.BSSID,sr.level);
//                    signalStrenghtList.add(signal);
//                    de.htwberlin.f4.ai.ma.fingerprint.Node.SignalInformation signalInformation = new de.htwberlin.f4.ai.ma.fingerprint.Node.SignalInformation("",signalStrenghtList);
//                    signalInformationList.add(signalInformation);
//                    Node node = new Node(null,0,signalInformationList);
//                    actuallyNode.add(node);
//                }
//            }
//            return actuallyNode;
//        }
//        else
//        {
//            return null;
//        }
//
//    }

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

                    final TextView test = (TextView) findViewById(R.id.tx_test);
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
                                test.setText(String.valueOf(sr.timestamp));
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
     * @param time the measured time
     */
    private void makeFingerprint(final int time) {
        final TextView textView = (TextView) findViewById(R.id.tx_Location);

        Set<String> bssid = multiMap.keySet();

        final List<Node> actuallyNode = new ArrayList<>();

        List<SignalInformation> signalInformationList = new ArrayList<>();

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


        Node node = nodeFactory.getInstance(null, 0, "", signalInformationList, "", "");

       // de.htwberlin.f4.ai.ma.prototype_temp.Node node = new de.htwberlin.f4.ai.ma.prototype_temp.Node(null, 0, signalInformationList);
        actuallyNode.add(node);


        fingerprint.setActuallyNode(actuallyNode);
        actually = fingerprint.getCalculatedPOI();

        LocationActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                LocationResult locationResult;
                if (actually != null) {
                    textView.setText(actually);
                    locationResult = new LocationResult(settings, String.valueOf(time), dropdown.getSelectedItem().toString(), actually + " "+fingerprint.getPercentage() +"%");
                } else {
                    textView.setText("kein POI gefunden");
                    locationResult = new LocationResult(settings, String.valueOf(time), dropdown.getSelectedItem().toString(), "kein POI gefunden");
                }
                makeJson(locationResult);
                resultAdapterdapter.add(locationResult);
                resultAdapterdapter.notifyDataSetChanged();
            }
        });

    }

//    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context c, Intent intent) {
//            // This condition is not necessary if you listen to only one action
//            //if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
//                final TextView test = (TextView) findViewById(R.id.tx_test);
//                EditText editText = (EditText) findViewById(R.id.edTx_WlanNameLocation);
//                String wlanName = editText.getText().toString();
//
//                //mainWifiObj.startScan();
//                List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
//                for (final ScanResult sr : wifiScanList) {
//
//                    LocationActivity.this.runOnUiThread(new Runnable() {
//                        public void run() {
//                            test.setText(String.valueOf(sr.timestamp));
//                        }
//                    });
//
//                    if (sr.SSID.equals(wlanName)) {
//                        multiMap.put(sr.BSSID, sr.level);
//                        long timestamp = sr.timestamp;
//                        Log.d("timestamp", String.valueOf(timestamp));
//                    }
//                }
//
//                makeFingerprint(1);
//            }
//        //}
//    };

    /**
     * make json Object for results and save
     * @param locationResult
     */
    private void makeJson(LocationResult locationResult) {
        try {
            String jsonString = loadJSON(getApplicationContext());
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArrayOld = jsonObject.getJSONArray("Results");
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObjectSetting = new JSONObject();
            jsonObjectSetting.put("Setting", locationResult.settings);
            jsonArray.put(jsonObjectSetting);
            JSONObject jsonObjectTime = new JSONObject();
            jsonObjectTime.put("Time", locationResult.measuredTime);
            jsonArray.put(jsonObjectTime);
            JSONObject jsonObjectPoi = new JSONObject();
            jsonObjectPoi.put("Poi", locationResult.poi);
            jsonArray.put(jsonObjectPoi);
            JSONObject jsonObjectMeasuredPoi = new JSONObject();
            jsonObjectMeasuredPoi.put("MeasuredPoi", locationResult.measuredPoi);
            jsonArray.put(jsonObjectMeasuredPoi);

            jsonArrayOld.put(jsonArray);
            jsonObject.put("Results", jsonArrayOld);
            saveJsonObject(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * save json Object to file
     * @param jsonObject
     */
    private void saveJsonObject(JSONObject jsonObject) {
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/Files");
        dir.mkdirs();
        File file = new File(dir, "jsonResultFile.txt");
        //File file = new File(Environment.getExternalStorageDirectory(), "/Files/jsonResultFile.txt");
        FileOutputStream outputStream;

        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(jsonObject.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<LocationResult> loadJson() {
        ArrayList<LocationResult> locationResultArrayList = new ArrayList<>();
        String jsonString = loadJSON(getApplicationContext());
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("Results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray jsonArrayResult = jsonArray.getJSONArray(i);
                String setting = jsonArrayResult.getJSONObject(0).getString("Setting");
                String time = jsonArrayResult.getJSONObject(1).getString("Time");
                String poi = jsonArrayResult.getJSONObject(2).getString("Poi");
                String measuredPoi = jsonArrayResult.getJSONObject(3).getString("MeasuredPoi");

                LocationResult locationResult = new LocationResult(setting, time, poi, measuredPoi);
                locationResultArrayList.add(locationResult);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return locationResultArrayList;
    }

    /**
     * load and read .txt file
     * @param context
     * @return json string
     */
    private String loadJSON(Context context) {
        String json = null;
        try {
            //TODO Exception abfangen
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath() + "/Files");
            dir.mkdirs();
            File file = new File(dir, "jsonResultFile.txt");
            //File file = new File(Environment.getExternalStorageDirectory(), "/Files/jsonResultFile.txt");
            if (file.exists()) {
                FileInputStream is = new FileInputStream(file);
                //InputStream is = context.getAssets().open("ergebnisse.txt");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            } else {
                json = "{Results: []}";
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    private boolean hasPermissions(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
