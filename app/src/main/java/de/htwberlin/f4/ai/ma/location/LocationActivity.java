package de.htwberlin.f4.ai.ma.location;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
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

import java.util.List;

import de.htwberlin.f4.ai.ma.wifi_scanner.WifiScanner;
import de.htwberlin.f4.ai.ma.wifi_scanner.WifiScannerFactory;
import de.htwberlin.f4.ai.ma.location.location_calculator.LocationCalculator;
import de.htwberlin.f4.ai.ma.fingerprint.AsyncResponse;
import de.htwberlin.f4.ai.ma.fingerprint.Fingerprint;
import de.htwberlin.f4.ai.ma.android.BaseActivity;
import de.htwberlin.f4.ai.ma.fingerprint.FingerprintTask;
import de.htwberlin.f4.ai.ma.location.location_calculator.LocationCalculatorFactory;
import de.htwberlin.f4.ai.ma.location.locationresult.LocationResult;
import de.htwberlin.f4.ai.ma.location.locationresult.LocationResultFactory;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerFactory;
import de.htwberlin.f4.ai.ma.location.calculations.FoundNode;
import de.htwberlin.f4.ai.ma.node.MaxPictureActivity;

/**
 * Created by Johann Winter
 *
 * This activity is for locating the user ("Standort ermitteln").
 */

public class LocationActivity extends BaseActivity implements AsyncResponse{

    Button measure1sButton;
    Button measure10sButton;
    ImageButton detailedResultsImagebutton;
    ImageView locationImageview;
    TextView locationTextview;
    TextView descriptionTextview;
    TextView infobox;
    ProgressBar progressBar;

    Context context;

    private DatabaseHandler databaseHandler;
    private SharedPreferences sharedPreferences;
    private String settingsString;
    private WifiManager wifiManager;

    private int locationsCounter;

    boolean movingAverage;
    boolean kalmanFilter;
    boolean euclideanDistance;
    boolean knnAlgorithm;
    private boolean verboseMode;
    private boolean useSSIDfilter;

    int knnValue;
    int movingAverageOrder;
    int kalmanValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_location, contentFrameLayout);

        context = this;

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        databaseHandler = DatabaseHandlerFactory.getInstance(this);

        measure1sButton = (Button) findViewById(R.id.start_measuring_1s_button);
        measure10sButton = (Button) findViewById(R.id.start_measurement_10s_button);
        detailedResultsImagebutton = (ImageButton) findViewById(R.id.location_detailed_results_imagebutton);
        locationImageview = (ImageView) findViewById(R.id.location_imageview);
        locationTextview = (TextView) findViewById(R.id.location_textview);
        descriptionTextview = (TextView) findViewById(R.id.description_textview_location);
        infobox = (TextView) findViewById(R.id.infobox_location);
        progressBar = (ProgressBar) findViewById(R.id.location_progressbar);


        // Get preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        movingAverage = sharedPreferences.getBoolean("pref_movingAverage", true);
        kalmanFilter = sharedPreferences.getBoolean("pref_kalman", false);
        euclideanDistance = sharedPreferences.getBoolean("pref_euclideanDistance", false);
        knnAlgorithm = sharedPreferences.getBoolean("pref_knnAlgorithm", true);
        verboseMode = sharedPreferences.getBoolean("verbose_mode", false);
        useSSIDfilter = sharedPreferences.getBoolean("use_ssid_filter", false);
        locationsCounter = sharedPreferences.getInt("locationsCounter", -1);

        Log.d("LocationActivity", "locationsCounter onCreate= " + locationsCounter);
        if (locationsCounter == -1) {
            locationsCounter = 0;
        }

        movingAverageOrder = Integer.parseInt(sharedPreferences.getString("pref_movivngAverageOrder", "3"));
        knnValue = Integer.parseInt(sharedPreferences.getString("pref_knnNeighbours", "3"));
        kalmanValue = Integer.parseInt(sharedPreferences.getString("pref_kalmanValue","2"));

        settingsString = "Mittelwert: " + movingAverage + "\r\nOrdnung: " + sharedPreferences.getString("pref_movivngAverageOrder", "3")
                + "\r\nKalman Filter: " + kalmanFilter +"\r\nKalman Wert: "+ sharedPreferences.getString("pref_kalmanValue","2")
                + "\r\nEuclidische Distanz: " + euclideanDistance
                + "\r\nKNN: " + knnAlgorithm+ "\r\nKNN Wert: "+ sharedPreferences.getString("pref_knnNeighbours", "3") ;


        detailedResultsImagebutton.setImageResource(R.drawable.info);

        progressBar.setVisibility(View.INVISIBLE);

        measure1sButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    measure1sButton.setEnabled(false);
                    measure10sButton.setEnabled(false);
                    findLocation(1);
                }
            });

        measure10sButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    measure1sButton.setEnabled(false);
                    measure10sButton.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    findLocation(10);
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
     * Create a fingerprint
     * @param seconds the time to measure in seconds
     */
    private void findLocation(final int seconds) {

        locationImageview.setVisibility(View.INVISIBLE);
        locationTextview.setText(getString(R.string.searching_node_text));
        descriptionTextview.setText("");

        FingerprintTask fingerprintTask;

        // If a SSID filter is set, scan for this SSID
        if (useSSIDfilter) {
            String defaultWifiString = sharedPreferences.getString("default_wifi_network", null);

            if (verboseMode) {
                fingerprintTask = new FingerprintTask(defaultWifiString, seconds, wifiManager, true, progressBar, null, infobox);
            } else {
                fingerprintTask = new FingerprintTask(defaultWifiString, seconds, wifiManager, true, progressBar, null);
            }
            fingerprintTask.delegate = this;
            fingerprintTask.execute();

            // If no SSID filter is set, scan for all SSIDS
        } else if (!useSSIDfilter) {
            if (verboseMode) {
                fingerprintTask = new FingerprintTask(null, seconds, wifiManager, true, progressBar, null, infobox);
            } else {
                fingerprintTask = new FingerprintTask(null, seconds, wifiManager, true, progressBar, null);
            }
            fingerprintTask.delegate = this;
            fingerprintTask.execute();
        }
    }



    /**
     * If the background FingerprintTask is finished, display results
     * and save the LocationResult.
     * @param seconds the measured time
     * @param fingerprint the fingerprint measured before
     */
    @Override
    public void processFinish(Fingerprint fingerprint, int seconds) {
        if (fingerprint != null) {

            LocationCalculator locationCalculator = LocationCalculatorFactory.createInstance(this);
            FoundNode foundNode = locationCalculator.calculateNodeId(fingerprint);

            LocationResult locationResult;
            if (foundNode != null) {

                locationTextview.setText(foundNode.getId());
                locationImageview.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);

                descriptionTextview.setText(databaseHandler.getNode(foundNode.getId()).getDescription());

                locationResult = LocationResultFactory.createInstance(locationsCounter, settingsString, String.valueOf(seconds), foundNode.getId(), foundNode.getPercent());

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
                locationResult = LocationResultFactory.createInstance(locationsCounter, settingsString, String.valueOf(seconds), getString(R.string.no_node_found_text), 0);

            }

            locationsCounter++;
            sharedPreferences.edit().putInt("locationsCounter", locationsCounter).apply();
            databaseHandler.insertLocationResult(locationResult);
        }
        measure1sButton.setEnabled(true);
        measure10sButton.setEnabled(true);
    }


}
