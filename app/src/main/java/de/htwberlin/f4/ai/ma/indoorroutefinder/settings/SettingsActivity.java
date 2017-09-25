package de.htwberlin.f4.ai.ma.indoorroutefinder.settings;

import android.os.Bundle;

import de.htwberlin.f4.ai.ma.indoorroutefinder.R;

import de.htwberlin.f4.ai.ma.indoorroutefinder.android.BaseActivity;

//import com.example.carol.bvg.SettingsFragment;

public class SettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        //getLayoutInflater().inflate(R.layout.activity_nodelist, contentFrameLayout);

        /*
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();*/

        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SettingsFragment())
                .commit();
    }
}
