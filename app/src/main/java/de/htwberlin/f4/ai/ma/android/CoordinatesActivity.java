package de.htwberlin.f4.ai.ma.android;

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

import com.example.carol.bvg.R;

import de.htwberlin.f4.ai.ma.android.calibrate.CalibrateController;
import de.htwberlin.f4.ai.ma.android.calibrate.CalibrateControllerImpl;
import de.htwberlin.f4.ai.ma.android.calibrate.CalibratePersistance;
import de.htwberlin.f4.ai.ma.android.calibrate.CalibratePersistanceImpl;
import de.htwberlin.f4.ai.ma.android.calibrate.CalibrateView;
import de.htwberlin.f4.ai.ma.android.calibrate.CalibrateViewImpl;
import de.htwberlin.f4.ai.ma.android.measure.MeasureController;
import de.htwberlin.f4.ai.ma.android.measure.MeasureControllerImpl;
import de.htwberlin.f4.ai.ma.android.measure.MeasureView;
import de.htwberlin.f4.ai.ma.android.measure.MeasureViewImpl;
import de.htwberlin.f4.ai.ma.android.record.RecordController;
import de.htwberlin.f4.ai.ma.android.record.RecordControllerImpl;
import de.htwberlin.f4.ai.ma.android.record.RecordView;
import de.htwberlin.f4.ai.ma.android.record.RecordViewImpl;

public class CoordinatesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static CoordinatesActivity instance;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_coordinates);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // check if the user calibrated the device already
        if (!alreadyCalibrated()) {
            loadCalibrateFragment();
        } else {
            loadMeasureFragment();
        }

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
            loadMeasureFragment();
        } else if (id == R.id.coordinates_nav_calibrate) {
            loadCalibrateFragment();
        } else if (id == R.id.coordinates_nav_record) {
            loadRecordFragment();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadMeasureFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MeasureView view = new MeasureViewImpl();
        MeasureController controller = new MeasureControllerImpl();
        controller.setView(view);
        //view.setController(controller);

        fragmentTransaction.replace(R.id.coordinates_contentFrame, (Fragment) view);
        //fragmentTransaction.addToBackStack("measure fragment");
        fragmentTransaction.commit();

        toolbar.setTitle(getString(R.string.title_measurement));
    }

    public void loadCalibrateFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        CalibrateView view = new CalibrateViewImpl();
        // TODO: save controller instance in activity?
        CalibrateController controller = new CalibrateControllerImpl();
        controller.setView(view);
        view.setController(controller);

        fragmentTransaction.replace(R.id.coordinates_contentFrame, (Fragment) view);
        //fragmentTransaction.addToBackStack("calibrate fragment");
        fragmentTransaction.commit();

        toolbar.setTitle(getString(R.string.title_calibration));
    }

    /*
    public void loadSettingsFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SettingsFragment settingsFragment = new SettingsFragment();

        fragmentTransaction.replace(R.id.coordinates_contentFrame, settingsFragment);
        //fragmentTransaction.addToBackStack("settings fragment");
        fragmentTransaction.commit();
    }

    public void loadImportFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ImportFragment importFragment = new ImportFragment();

        fragmentTransaction.replace(R.id.coordinates_contentFrame, importFragment);
        //fragmentTransaction.addToBackStack("import fragment");
        fragmentTransaction.commit();
    }*/

    public void loadRecordFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        RecordView view = new RecordViewImpl();
        RecordController controller = new RecordControllerImpl();
        controller.setView(view);
        //view.setController(controller);

        fragmentTransaction.replace(R.id.coordinates_contentFrame, (Fragment) view);
        //fragmentTransaction.addToBackStack("record fragment");
        fragmentTransaction.commit();

        toolbar.setTitle(getString(R.string.title_record));
    }

    private boolean alreadyCalibrated() {
        CalibratePersistance calibratePersistance = new CalibratePersistanceImpl(this);
        return calibratePersistance.load() != null;
    }

    public static CoordinatesActivity getInstance() {
        return instance;
    }

}
