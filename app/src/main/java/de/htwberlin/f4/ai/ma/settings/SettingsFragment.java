package de.htwberlin.f4.ai.ma.settings;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.carol.bvg.R;

import java.util.List;

import de.htwberlin.f4.ai.ma.wifi_scanner.WifiScanner;
import de.htwberlin.f4.ai.ma.wifi_scanner.WifiScannerFactory;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiScanner wifiScanner = WifiScannerFactory.createInstance();

        List<String> wifiList = wifiScanner.getAvailableNetworks(wifiManager, true);
        CharSequence[] wifis = wifiList.toArray(new CharSequence[wifiList.size()]);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference);

        ListPreference defaultWifiListpreference = (ListPreference) findPreference("default_wifi_network");
        defaultWifiListpreference.setEntries(wifis);
        defaultWifiListpreference.setEntryValues(wifis);
    }
}
