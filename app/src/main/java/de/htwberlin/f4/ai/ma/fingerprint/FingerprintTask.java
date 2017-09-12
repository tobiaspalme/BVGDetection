package de.htwberlin.f4.ai.ma.fingerprint;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.htwberlin.f4.ai.ma.fingerprint.accesspointsample.AccessPointSample;
import de.htwberlin.f4.ai.ma.fingerprint.accesspointsample.AccessPointSampleFactory;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * Created by Johann Winter
 *
 * This class generates a WiFi fingerprint by measuring signal strengths for a given WiFi name (SSID).
 * It takes as parameter: SSID ("wifiName"), measuring time in seconds ("seconds"), a WifiManager,
 * a boolean if the result should be averaged, and a TextView and ProgressBar for displaying Progress.
 * The TextView and ProgressBar can be null.
 */


public class FingerprintTask extends AsyncTask<Void, Integer, Fingerprint> {

    private ProgressBar progressBar;
    private TextView progressTextview;
    private TextView verboseOutputTextview;
    private String wifiName;
    private int seconds;
    private WifiManager wifiManager;
    private Multimap<String, Integer> multiMap;
    private List<SignalInformation> signalInformationList;
    private List<AccessPointSample> accessPointSampleList;
    private boolean calculateAverage = false;
    private long timestampWifimanager = 0;
    public AsyncResponse delegate = null;

    // Normal mode
    public FingerprintTask(final String wifiName, final int seconds, final WifiManager wifiManager, final Boolean calculateAverage,
                           final @Nullable ProgressBar progressBar, final @Nullable TextView progressTextview) {
        this.wifiManager = wifiManager;
        this.wifiName = wifiName;
        this.seconds = seconds;
        this.calculateAverage = calculateAverage;
        this.progressBar = progressBar;
        this.progressTextview = progressTextview;
    }

    // Verbose mode
    public FingerprintTask(final String wifiName, final int seconds, final WifiManager wifiManager, final Boolean calculateAverage,
                           final @Nullable ProgressBar progressBar, final @Nullable TextView progressTextview, final TextView verboseOutputTextview) {
        this.wifiManager = wifiManager;
        this.wifiName = wifiName;
        this.seconds = seconds;
        this.calculateAverage = calculateAverage;
        this.progressBar = progressBar;
        this.progressTextview = progressTextview;
        this.verboseOutputTextview = verboseOutputTextview;
    }


    @Override
    protected Fingerprint doInBackground(Void... voids) {
            for (int i = 0; i < seconds; i++) {

                accessPointSampleList = new ArrayList<>();

                // If task gets aborted
                if (isCancelled()) {
                    return null;
                }

                wifiManager.startScan();
                List<ScanResult> wifiScanList = wifiManager.getScanResults();

                if (wifiScanList.get(0).timestamp == timestampWifimanager && seconds == 1) {
                    Log.d("FingerprintTask", "Same timestamp and 1s-scan. Please try again");
                    return null;
                }
                timestampWifimanager = wifiScanList.get(0).timestamp;

                Log.d("### scanresults:", String.valueOf(wifiScanList.size()));

                for (final ScanResult sr : wifiScanList) {
                    if (sr.SSID.equals(wifiName)) {
                        Log.d("Fingerprinting... ", "MAC: " +  sr.BSSID + "   Strength: " + sr.level + " dBm         timestamp: " + sr.timestamp );
                        AccessPointSample accessPointSample = AccessPointSampleFactory.createInstance(sr.BSSID, sr.level);
                        accessPointSampleList.add(accessPointSample);
                        multiMap.put(sr.BSSID, sr.level);
                    }
                }
                Log.d("--------", "-------------------------------------------------");

                publishProgress(i);

                wifiScanList.clear();

                SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy-hh.mm.ss", Locale.getDefault());
                String format = s.format(new Date());
                SignalInformation signalInformation = new SignalInformation(format, accessPointSampleList);
                signalInformationList.add(signalInformation);


                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (calculateAverage) {
                // Calculate average values
                List<SignalInformation> signalInformations = AverageSignalCalculator.calculateAverageSignal(multiMap);
                return FingerprintFactory.createInstance(wifiName, signalInformations);
            }

            else {
                return FingerprintFactory.createInstance(wifiName, signalInformationList);
            }

    }

    @Override
    protected void onCancelled() {
        delegate.processFinish(null, 0);
    }

    @Override
    protected void onPreExecute() {
        if (progressBar != null) {
            progressBar.setMax(seconds);
        }
        multiMap = ArrayListMultimap.create();
        signalInformationList = new ArrayList<>();
        accessPointSampleList = new ArrayList<>();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (progressBar != null) {
            progressBar.setProgress(values[0] + 1);
        }

        // Verbose-Mode (show measuring data in UI)
        if (verboseOutputTextview != null) {
            String textviewString = "";

            for (int i = 0; i < accessPointSampleList.size(); i++) {
                textviewString += accessPointSampleList.get(i).getMacAddress() + "  " + accessPointSampleList.get(i).getRSSI() + "         ";
            }
            verboseOutputTextview.setText(textviewString);
            if (progressTextview != null) {
                progressTextview.setText(String.valueOf(seconds - values[0]));
            }

        // Normal mode
        } else {
            if (progressTextview != null) {
                progressTextview.setText(String.valueOf(seconds - values[0]));
            }
        }
    }

    @Override
    protected void onPostExecute(Fingerprint fingerprint) {
        delegate.processFinish(fingerprint, seconds);
    }


}

