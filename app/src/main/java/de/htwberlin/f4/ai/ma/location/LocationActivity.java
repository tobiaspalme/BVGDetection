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
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ma.node.fingerprint.FingerprintGenerator;
import de.htwberlin.f4.ai.ma.node.fingerprint.FingerprintGeneratorImpl;
import de.htwberlin.f4.ai.ma.android.BaseActivity;
import de.htwberlin.f4.ai.ma.node.NodeFactory;
import de.htwberlin.f4.ai.ma.node.fingerprint.FingerprintImpl;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerFactory;
import de.htwberlin.f4.ai.ma.persistence.calculations.FoundNode;
import de.htwberlin.f4.ai.ma.MaxPictureActivity;

/**
 * Created by Johann Winter
 *
 * This activity is for locating the user ("Standort ermitteln").
 */

public class LocationActivity extends BaseActivity {

    Button measure1sButton;
    Button measure10sButton;
    ImageButton detailedResultsImagebutton;
    ImageView locationImageview;
    ImageView refreshImageview;
    //TextView descriptionLabelTextview;
    TextView locationTextview;
    TextView descriptionTextview;
    // TextView coordinatesLabelTextview;
    // TextView coordinatesTextview;
    ProgressBar progressBar;

    Context context = this;

    private DatabaseHandler databaseHandler;
    private SharedPreferences sharedPreferences;
    private NodeFactory nodeFactory;
    private String settings;
    private Spinner wifiDropdown;
    private FoundNode foundNode;
    private WifiManager wifiManager;
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

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        measure1sButton = (Button) findViewById(R.id.start_measuring_1s_button);
        measure10sButton = (Button) findViewById(R.id.start_measurement_10s_button);
        detailedResultsImagebutton = (ImageButton) findViewById(R.id.location_detailed_results_imagebutton);
        locationImageview = (ImageView) findViewById(R.id.location_imageview);
        refreshImageview = (ImageView) findViewById(R.id.refresh_imageview_locationactivity);
        //descriptionLabelTextview = (TextView) findViewById(R.id.description_textview_label);
        locationTextview = (TextView) findViewById(R.id.location_textview);
        descriptionTextview = (TextView) findViewById(R.id.description_textview_location);
        //coordinatesLabelTextview = (TextView) findViewById(R.id.coordinates_textview_label);
        //coordinatesTextview = (TextView) findViewById(R.id.coordinates_textview_location);
        wifiDropdown = (Spinner) findViewById(R.id.wifi_names_dropdown_location);
        progressBar = (ProgressBar) findViewById(R.id.location_progressbar);


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
        progressBar.setVisibility(View.INVISIBLE);

        refreshWifiDropdown();

        measure1sButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    progressBar.setVisibility(View.INVISIBLE);
                    //progressBar.setVisibility(View.VISIBLE);
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


    /**
     * Scan for WiFi names (SSIDs) and add them to the dropdown
     */
    private void refreshWifiDropdown() {
        wifiManager.startScan();
        List<ScanResult> wifiScanList = wifiManager.getScanResults();

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
     * Check WiFi signal strengths and and save them to a multimap
     * @param seconds the time to measure in seconds
     */
    private void getMeasuredNode(final int seconds) {

        locationImageview.setVisibility(View.INVISIBLE);
        //descriptionLabelTextview.setVisibility(View.INVISIBLE);
       // coordinatesLabelTextview.setVisibility(View.INVISIBLE);

        descriptionTextview.setText("");
//        coordinatesTextview.setText("");
        //locationImageview.setEnabled(false);

        if (wifiDropdown.getAdapter().getCount() > 0) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            locationTextview.setText(getString(R.string.searching_node_text));

            //int progressVariable = 0;
            //new FingerprintGeneratorImpl(progressVariable).execute();

            FingerprintGenerator fingerprintGenerator = new FingerprintGeneratorImpl();
            FingerprintImpl fingerprintImpl = fingerprintGenerator.getFingerprint(wifiDropdown.getSelectedItem().toString(), seconds, wifiManager);

            getLocationResult(seconds, fingerprintImpl);
        }
    }


    /**
     * Try to calculate the position.
     * @param measuredTime the measured time
     * @param fingerprintImpl the fingerprintImpl measured before
     */
    private void getLocationResult(final int measuredTime, FingerprintImpl fingerprintImpl) {
        //List<SignalInformation> signalInformations = AverageSignalCalculator.calculateAverageSignal(multiMap);
        //foundNode = databaseHandler.calculateNodeId(signalInformations);
        foundNode = databaseHandler.calculateNodeId(fingerprintImpl);


        LocationResult locationResult;
        if (foundNode != null) {

            locationTextview.setText(foundNode.getId());
            locationImageview.setVisibility(View.VISIBLE);
            //descriptionLabelTextview.setVisibility(View.VISIBLE);
            //coordinatesLabelTextview.setVisibility(View.VISIBLE);

            descriptionTextview.setText(databaseHandler.getNode(foundNode.getId()).getDescription());
            //coordinatesTextview.setText(databaseHandler.getNode(foundNode.getId()).getCoordinates());

            locationResult = new LocationResultImpl(locationsCounter, settings, String.valueOf(measuredTime), foundNode.getId(), foundNode.getPercent());

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
            locationResult = new LocationResultImpl(locationsCounter, settings, String.valueOf(measuredTime), getString(R.string.no_node_found_text), 0);

        }

        locationsCounter++;
        Log.d("LocationActivity", "locationsCounter = " + locationsCounter);
        sharedPreferences.edit().putInt("locationsCounter", locationsCounter).apply();
        databaseHandler.insertLocationResult(locationResult);
    }

}
