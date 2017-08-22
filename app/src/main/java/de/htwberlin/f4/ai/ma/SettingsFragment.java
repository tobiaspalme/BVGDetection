package de.htwberlin.f4.ai.ma;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.carol.bvg.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference);
    }
}
