package de.htwberlin.f4.ai.ma.location;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carol.bvg.R;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//import de.htwberlin.f4.ai.ma.persistence.fingerprint.Fingerprint;
//import de.htwberlin.f4.ai.ma.persistence.fingerprint.FingerprintFactory;
import de.htwberlin.f4.ai.ma.android.BaseActivity;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.node.NodeFactory;
import de.htwberlin.f4.ai.ma.node.fingerprint.SignalInformation;
import de.htwberlin.f4.ai.ma.node.fingerprint.SignalStrengthInformation;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerFactory;
import de.htwberlin.f4.ai.ma.persistence.calculations.FoundNode;
import de.htwberlin.f4.ai.ma.MaxPictureActivity;


public class LocationActivity extends BaseActivity {

    Button measure1sButton;
    Button measure10sButton;
    ImageButton detailedResultsImagebutton;
    ImageView locationImageview;
    ImageView refreshImageview;
    //TextView descriptionLabelTextview;
    TextView descriptionTextview;
   // TextView coordinatesLabelTextview;
   // TextView coordinatesTextview;
    TextView percentLabelTextview;
    TextView percentTextview;
    ProgressBar progressBar;

    Context context = this;

    //String[] permissions;
    private DatabaseHandler databaseHandler;
    private SharedPreferences sharedPreferences;
    private NodeFactory nodeFactory;
    //ListView listView;
    private String settings;
    //private Spinner nodesDropdown;
    private Spinner wifiDropdown;
    //private LocationResultAdapter resultAdapterdapter;
    //private String foundNodeName;
    private FoundNode foundNode;
    private WifiManager mainWifiObj;
    private Multimap<String, Integer> multiMap;
    private long timestampWifiManager = 0;

    private int locationsCounter;


    boolean movingAverage;
    boolean kalmanFilter;
    boolean euclideanDistance;
    boolean knnAlgorithm;

    int knnValue;
    int movingAverageOrder;
    int kalmanValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_location, contentFrameLayout);

       /* permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION};
*/
        mainWifiObj= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        measure1sButton = (Button) findViewById(R.id.start_measuring_1s_button);
        measure10sButton = (Button) findViewById(R.id.start_measurement_10s_button);
        detailedResultsImagebutton = (ImageButton) findViewById(R.id.location_detailed_results_imagebutton);
        locationImageview = (ImageView) findViewById(R.id.location_imageview);
        refreshImageview = (ImageView) findViewById(R.id.refresh_imageview_locationactivity);
        //descriptionLabelTextview = (TextView) findViewById(R.id.description_textview_label);
        descriptionTextview = (TextView) findViewById(R.id.description_textview_location);
        //coordinatesLabelTextview = (TextView) findViewById(R.id.coordinates_textview_label);
        //coordinatesTextview = (TextView) findViewById(R.id.coordinates_textview_location);
        percentLabelTextview = (TextView) findViewById(R.id.percent_label_textview);
        percentTextview = (TextView) findViewById(R.id.percent_textview);
        wifiDropdown = (Spinner) findViewById(R.id.wifi_names_dropdown_location);
        progressBar = (ProgressBar) findViewById(R.id.location_progressbar);

        //listView = (ListView) findViewById(R.id.results_listview);

        // Get preferences
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


        databaseHandler = DatabaseHandlerFactory.getInstance(this);
        final List<Node> allNodes = databaseHandler.getAllNodes();

        detailedResultsImagebutton.setImageResource(R.drawable.info);
        refreshImageview.setImageResource(R.drawable.refresh);
        refreshImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshWifiDropdown();
            }
        });


        //descriptionLabelTextview.setVisibility(View.INVISIBLE);
        //coordinatesLabelTextview.setVisibility(View.INVISIBLE);
        percentLabelTextview.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        refreshWifiDropdown();


        measure1sButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    progressBar.setVisibility(View.INVISIBLE);
                    getMeasuredNode(1);
                }
            });

        measure10sButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    getMeasuredNode(10);
                }
            });

        detailedResultsImagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LocationDetailedInfoActivity.class);
                startActivity(intent);
            }
        });

    }



    // Scan for WiFi names (SSIDs) and add them to the dropdown
    private void refreshWifiDropdown() {
        mainWifiObj.startScan();
        List<ScanResult> wifiScanList = mainWifiObj.getScanResults();

        ArrayList<String> wifiNamesList = new ArrayList<>();
        for (ScanResult sr : wifiScanList) {
            if (!wifiNamesList.contains(sr.SSID) && !sr.SSID.equals("")) {
                wifiNamesList.add(sr.SSID);
            }
        }
        final ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, wifiNamesList);
        wifiDropdown.setAdapter(dropdownAdapter);
        Toast.makeText(getApplicationContext(), getString(R.string.refreshed_toast), Toast.LENGTH_SHORT).show();

    }

    /**
     * Check WiFi signal strengths and and save to multimap
     * @param times the measured time: one or ten seconds
     */
    private void getMeasuredNode(final int times) {

        locationImageview.setVisibility(View.INVISIBLE);
        //descriptionLabelTextview.setVisibility(View.INVISIBLE);
       // coordinatesLabelTextview.setVisibility(View.INVISIBLE);
        percentLabelTextview.setVisibility(View.INVISIBLE);

        descriptionTextview.setText("");
//        coordinatesTextview.setText("");
        percentTextview.setText("");
        //locationImageview.setEnabled(false);

        if (wifiDropdown.getAdapter().getCount() > 0) {
        // if (nodesDropdown.getAdapter().getCount() > 0 && wifiDropdown.getAdapter().getCount() > 0) {

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            //registerReceiver(mWifiScanReceiver, intentFilter);

            final TextView locationTextview = (TextView) findViewById(R.id.location_textview);


            locationTextview.setText(getString(R.string.searching_node_text));

            new Thread(new Runnable() {
                public void run() {
                    multiMap = ArrayListMultimap.create();
                    for (int i = 0; i < times; i++) {
                        progressBar.setMax(times);
                        progressBar.setProgress(i + 1);

                        mainWifiObj.startScan();
                        String wlanName = wifiDropdown.getSelectedItem().toString();
                        List<ScanResult> wifiScanList = mainWifiObj.getScanResults();

                        //check if there is a new measurement
                         if(wifiScanList.get(0).timestamp == timestampWifiManager && times == 1)
                            {
                                LocationActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        locationTextview.setText("Bitte neu versuchen");
                                    }
                                });
                                return;
                            }

                            timestampWifiManager = wifiScanList.get(0).timestamp;
                            Log.d("timestamp", String.valueOf(timestampWifiManager));

                        for (final ScanResult sr : wifiScanList) {
                            //LocationActivity.this.runOnUiThread(new Runnable() {
                              //  public void run() {
                           //         testTimestampTextview.setText(String.valueOf(sr.timestamp));
                               // }
                            //});

                            if (sr.SSID.equals(wlanName)) {
                                multiMap.put(sr.BSSID, sr.level);
                                Log.d("LocationActivity", "Messung, SSID stimmt mit Dropdown überein:        BSSID = " + sr.BSSID + " LVL = " + sr.level);
                                long timestamp = sr.timestamp;
                                Log.d("timestamp", String.valueOf(timestamp));
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
        }
    }


    /**
     * if times measurement is more than one second make a average of values. Try to start a fingerprint and calculate position.
     * @param measuredTime the measured time
     */
    private void makeFingerprint(final int measuredTime) {
        final TextView locationTextview = (TextView) findViewById(R.id.location_textview);

        Set<String> bssid = multiMap.keySet();

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

        //Node node = nodeFactory.createInstance(null, "", new Fingerprint("", signalInformationList), "", "", "");

        //foundNodeName = databaseHandler.calculateNodeId(node);
        //foundNode = databaseHandler.calculateNodeId(node);
        foundNode = databaseHandler.calculateNodeId(signalInformationList);


        LocationActivity.this.runOnUiThread(new Runnable() {
            public void run() {

                LocationResultImplementation locationResult;
                if (foundNode != null) {

                    //if (foundNodeName != null) {
                    locationTextview.setText(foundNode.getId());
                    //locationImageview.setEnabled(true);

                    locationImageview.setVisibility(View.VISIBLE);
                    //descriptionLabelTextview.setVisibility(View.VISIBLE);
                    //coordinatesLabelTextview.setVisibility(View.VISIBLE);
                    percentLabelTextview.setVisibility(View.VISIBLE);

                    descriptionTextview.setText(databaseHandler.getNode(foundNode.getId()).getDescription());
                    //coordinatesTextview.setText(databaseHandler.getNode(foundNode.getId()).getCoordinates());
                    percentTextview.setText(String.valueOf(foundNode.getPercent()));

                    // TODO percentage einfügen
                    locationResult = new LocationResultImplementation(locationsCounter, settings, String.valueOf(measuredTime), foundNode.getId(), Double.valueOf(foundNode.getPercent()));

                    final String picturePath = databaseHandler.getNode(foundNode.getId()).getPicturePath();

                    if (picturePath != null) {
                        Glide.with(context).load(picturePath).into(locationImageview);
                    } else {
                        Glide.with(context).load(R.drawable.unknown).into(locationImageview);
                    }

                    locationImageview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), MaxPictureActivity.class);
                            intent.putExtra("picturePath", picturePath);
                            startActivity(intent);
                        }
                    });


                } else {
                    locationTextview.setText(getString(R.string.no_node_found_text));
                    //locationImageview.setEnabled(false);
                    locationResult = new LocationResultImplementation(locationsCounter, settings, String.valueOf(measuredTime), getString(R.string.no_node_found_text), 0);

                }
                //makeJson(locationResult);

                locationsCounter++;
                Log.d("LocationActivity", "locationsCounter = " + locationsCounter);
                sharedPreferences.edit().putInt("locationsCounter", locationsCounter).apply();
                databaseHandler.insertLocationResult(locationResult);

                //resultAdapterdapter.add(locationResult);
                //resultAdapterdapter.notifyDataSetChanged();
            }
        });
    }

}
