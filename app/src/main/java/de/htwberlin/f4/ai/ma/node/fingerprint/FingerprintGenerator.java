package de.htwberlin.f4.ai.ma.node.fingerprint;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johann Winter
 */


//public class FingerprintGenerator extends AsyncTask<Void, Integer, Integer> {

public class FingerprintGenerator {
/*
    private ProgressBar progressBar;
    private TextView locationTextview;
    String wifiName;
    int seconds;
    WifiManager wifiManager;


    int progressVariable;

    public FingerprintGenerator(int progressVariable) {
        this.progressBar = progressBar;
        this.locationTextview = locationTextview;
        this.progressVariable = progressVariable;
    }
*/

    Multimap<String, Integer> multiMap;

    public Fingerprint getFingerprint(final String wifiName, final int seconds, final WifiManager wifiManager) {
//    public Fingerprint getFingerprint(final String wifiName, final int seconds, final WifiManager wifiManager, ProgressBar progressBar, TextView progressTextview) {

        multiMap = ArrayListMultimap.create();


        List<SignalStrengthInformation> signalStrengthList = new ArrayList<>();
        //List<SignalInformation> signalInformationList = new ArrayList<>();

       // new Thread(new Runnable() {
        //    @Override
         //   public void run() {

                long timestampWifiManager = 0;

                for (int i = 0; i < seconds; i++) {

                    //progressBar.setMax(seconds);
                    //progressBar.setProgress(i + 1);

                    wifiManager.startScan();
                    List<ScanResult> wifiScanList = wifiManager.getScanResults();

                    //check if there is a new measurement
                    if (wifiScanList.get(0).timestamp == timestampWifiManager && seconds == 1) {
                        //progressTextview.setText("Bitte erneut versuchen");
                    }

                    timestampWifiManager = wifiScanList.get(0).timestamp;
                    Log.d("timestamp", String.valueOf(timestampWifiManager));

                    for (final ScanResult sr : wifiScanList) {
                        if (sr.SSID.equals(wifiName)) {
                            multiMap.put(sr.BSSID, sr.level);
                            //SignalStrengthInformation signal = new SignalStrengthInformation(sr.BSSID, sr.level);
                            Log.d("LocationActivity", "Messung, SSID stimmt mit Dropdown überein:        BSSID = " + sr.BSSID + " LVL = " + sr.level);
                            //signalStrengthList.add(signal);
                        }

                    }
                    wifiScanList.clear();
                    //SignalInformation signalInformation = new SignalInformation("", signalStrengthList);
                    //signalInformationList.add(signalInformation);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                List<SignalInformation> signalInformations = AverageSignalCalculator.calculateAverageSignal(multiMap);
         //   }
        //}).start();
        return new Fingerprint(wifiName, signalInformations);
    }

/*
    @Override
    protected Integer doInBackground(Void... voids) {

        for (int i = 0; i<10; i++) {
            publishProgress(i);
            System.out.println("+++++++++++++++++ PROGRESS: " + i);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

        System.out.println("++++++++++++ JOB DONE +++++++++++++++");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setMax(10);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        progressBar.setProgress(values[0]);
    }*/
}

