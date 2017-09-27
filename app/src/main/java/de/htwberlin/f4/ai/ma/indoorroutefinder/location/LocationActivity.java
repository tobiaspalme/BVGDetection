package de.htwberlin.f4.ai.ma.indoorroutefinder.location;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import de.htwberlin.f4.ai.ma.indoorroutefinder.R;
import de.htwberlin.f4.ai.ma.indoorroutefinder.location.location_calculator.LocationCalculator;
import de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint.AsyncResponse;
import de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint.Fingerprint;
import de.htwberlin.f4.ai.ma.indoorroutefinder.android.BaseActivity;
import de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint.FingerprintTask;
import de.htwberlin.f4.ai.ma.indoorroutefinder.location.location_calculator.LocationCalculatorFactory;
import de.htwberlin.f4.ai.ma.indoorroutefinder.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.indoorroutefinder.persistence.DatabaseHandlerFactory;
import de.htwberlin.f4.ai.ma.indoorroutefinder.node.MaxPictureActivity;

/**
 * Created by Johann Winter
 *
 * This activity is for locating the user ("Standort ermitteln").
 */

public class LocationActivity extends BaseActivity implements AsyncResponse{

    ImageButton locate1sButton;
    ImageButton locate10sButton;
    ImageView locationImageview;
    TextView locationTextview;
    TextView descriptionTextview;
    TextView infobox;
    ProgressBar progressBar;
    Context context;
    private DatabaseHandler databaseHandler;
    private SharedPreferences sharedPreferences;
    private WifiManager wifiManager;
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

        locate1sButton = (ImageButton) findViewById(R.id.locate_1s_button);
        locate10sButton = (ImageButton) findViewById(R.id.locate_10s_button);
        locationImageview = (ImageView) findViewById(R.id.location_imageview);
        locationTextview = (TextView) findViewById(R.id.location_textview);
        descriptionTextview = (TextView) findViewById(R.id.description_textview_location);
        infobox = (TextView) findViewById(R.id.infobox_location);
        progressBar = (ProgressBar) findViewById(R.id.location_progressbar);

        locate1sButton.setImageResource(R.drawable.locate_1s_button);
        locate10sButton.setImageResource(R.drawable.locate_10s_button);

        // Get preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        movingAverage = sharedPreferences.getBoolean("pref_movingAverage", true);
        kalmanFilter = sharedPreferences.getBoolean("pref_kalman", false);
        euclideanDistance = sharedPreferences.getBoolean("pref_euclideanDistance", false);
        knnAlgorithm = sharedPreferences.getBoolean("pref_knnAlgorithm", true);
        verboseMode = sharedPreferences.getBoolean("verbose_mode", false);
        useSSIDfilter = sharedPreferences.getBoolean("use_ssid_filter", false);

        movingAverageOrder = Integer.parseInt(sharedPreferences.getString("pref_movivngAverageOrder", "3"));
        knnValue = Integer.parseInt(sharedPreferences.getString("pref_knnNeighbours", "3"));
        kalmanValue = Integer.parseInt(sharedPreferences.getString("pref_kalmanValue","2"));

        progressBar.setVisibility(View.INVISIBLE);

        locate1sButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    findLocation(1);
                }
            });

        locate10sButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    findLocation(10);
                }
            });
    }

    /**
     * Create a fingerprint
     * @param seconds the time to measure in seconds
     */
    private void findLocation(final int seconds) {
        locate1sButton.setEnabled(false);
        locate10sButton.setEnabled(false);
        locate1sButton.setImageResource(R.drawable.locate_1s_button_inactive);
        locate10sButton.setImageResource(R.drawable.locate_10s_button_inactive);

        locationImageview.setVisibility(View.INVISIBLE);
        locationTextview.setText(getString(R.string.searching_node_text));
        descriptionTextview.setText("");

        FingerprintTask fingerprintTask;

        String ssidFilterString = null;

        if (verboseMode) {
            if (useSSIDfilter) {
                ssidFilterString = sharedPreferences.getString("default_wifi_network", null);
            }
            fingerprintTask = new FingerprintTask(ssidFilterString, seconds, wifiManager, true, progressBar, null, infobox);
        } else {
            if (useSSIDfilter) {
                ssidFilterString = sharedPreferences.getString("default_wifi_network", null);
            }
            fingerprintTask = new FingerprintTask(ssidFilterString, seconds, wifiManager, true, progressBar, null);
        }

        fingerprintTask.delegate = this;
        fingerprintTask.execute();
    }



    /**
     * If the background FingerprintTask is finished, display results
     * @param seconds the measured time
     * @param fingerprint the fingerprint measured before
     */
    @Override
    public void processFinish(Fingerprint fingerprint, int seconds) {
        if (fingerprint != null) {

            LocationCalculator locationCalculator = LocationCalculatorFactory.createInstance(this);
            String foundNode = locationCalculator.calculateNodeId(fingerprint);

            if (foundNode != null) {

                locationTextview.setText(foundNode);
                locationImageview.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);

                descriptionTextview.setText(databaseHandler.getNode(foundNode).getDescription());

                final String picturePath = databaseHandler.getNode(foundNode).getPicturePath();

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
            }
        }
        locate1sButton.setEnabled(true);
        locate10sButton.setEnabled(true);
        locate1sButton.setImageResource(R.drawable.locate_1s_button);
        locate10sButton.setImageResource(R.drawable.locate_10s_button);
    }


}
