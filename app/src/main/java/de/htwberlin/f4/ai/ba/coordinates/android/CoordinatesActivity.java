package de.htwberlin.f4.ai.ba.coordinates.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.carol.bvg.R;

public class CoordinatesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinates);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.coordinates, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.coordinates_nav_measurement) {
            Toast toast = Toast.makeText(getApplicationContext(), "Messen", Toast.LENGTH_SHORT);
            toast.show();
            loadMeasureFragment();
        } else if (id == R.id.coordinates_nav_calibrate) {
            Toast toast = Toast.makeText(getApplicationContext(), "Kalibrieren", Toast.LENGTH_SHORT);
            toast.show();
            loadCalibrateFragment();
        } else if (id == R.id.coordinates_nav_settings) {
            Toast toast = Toast.makeText(getApplicationContext(), "Einstellungen", Toast.LENGTH_SHORT);
            toast.show();
            loadSettingsFragment();
        } else if (id == R.id.coordinates_nav_import) {
            Toast toast = Toast.makeText(getApplicationContext(), "Import / Export", Toast.LENGTH_SHORT);
            toast.show();
            loadImportFragment();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadMeasureFragment() {
        removeAllFragments();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MeasureFragment measureFragment = new MeasureFragment();

        fragmentTransaction.add(R.id.coordinates_contentFrame, measureFragment);
        fragmentTransaction.commit();
    }

    public void loadCalibrateFragment() {
        removeAllFragments();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CalibrateFragment calibrateFragment = new CalibrateFragment();

        fragmentTransaction.add(R.id.coordinates_contentFrame, calibrateFragment);
        fragmentTransaction.commit();
    }

    public void loadSettingsFragment() {
        removeAllFragments();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SettingsFragment settingsFragment = new SettingsFragment();

        fragmentTransaction.add(R.id.coordinates_contentFrame, settingsFragment);
        fragmentTransaction.commit();
    }

    public void loadImportFragment() {
        removeAllFragments();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ImportFragment importFragment = new ImportFragment();

        fragmentTransaction.add(R.id.coordinates_contentFrame, importFragment);
        fragmentTransaction.commit();
    }

    private void removeAllFragments() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentManager.getFragments() != null) {
            for (Fragment fragment : fragmentManager.getFragments()) {
                fragmentTransaction.remove(fragment);
            }
        }
        fragmentTransaction.commit();
    }
}
