package de.htwberlin.f4.ai.ma.android;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.carol.bvg.R;

import de.htwberlin.f4.ai.ma.android.calibrate.CalibrateViewImpl;
import de.htwberlin.f4.ai.ma.android.measure.MeasureViewImpl;
import de.htwberlin.f4.ai.ma.android.measure.edges.EdgeDetailsViewImpl;
import de.htwberlin.f4.ai.ma.android.record.RecordViewImpl;
import de.htwberlin.f4.ai.ma.edge.EdgesManagerActivity;
import de.htwberlin.f4.ai.ma.location.LocationActivity;
import de.htwberlin.f4.ai.ma.routefinder.RouteFinderActivity;
import de.htwberlin.f4.ai.ma.nodelist.NodeListActivity;
import de.htwberlin.f4.ai.ma.ImportExportActivity;
//import de.htwberlin.f4.ai.ma.NodeRecordActivity;
import de.htwberlin.f4.ai.ma.node.NodeRecordEditActivity;
import de.htwberlin.f4.ai.ma.settings.SettingsActivity;

public class BaseActivity extends AppCompatActivity {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;

    // for passing start and targetnode to our edgedetails activity
    protected static final String STARTNODE_KEY = "startnode";
    protected static final String TARGETNODE_KEY = "targetnode";
    protected static final String EDGE_DETAILS_BUNDLE = "nodeinfos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {

                    case R.id.nav_location_record:
                        loadNodeRecordEdit();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_location_manager:
                        loadNodelist();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_edges_manager:
                        loadEdgesManager();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_location_detect:
                        loadLocation();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_route_finder:
                        loadRouteFinder();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_import:
                        loadImportExport();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_settings:
                        loadSettings();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_measurement:
                        loadMeasurement();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_calibrate:
                        loadCalibrate();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_record:
                        loadRecord();
                        drawerLayout.closeDrawers();
                        break;


                    default:
                        break;

                }
                return false;
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    public void loadNodeRecordEdit() {
        Intent intent = new Intent(getApplicationContext(), NodeRecordEditActivity.class);
        finish();
        startActivity(intent);
    }

    public void loadNodelist() {
        Intent intent = new Intent(getApplicationContext(), NodeListActivity.class);
        finish();
        startActivity(intent);
    }

    public void loadEdgesManager() {
        Intent intent = new Intent(getApplicationContext(), EdgesManagerActivity.class);
        finish();
        startActivity(intent);
    }

    public void loadLocation() {
        Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
        finish();
        startActivity(intent);
    }

    public void loadRouteFinder() {
        Intent intent = new Intent(getApplicationContext(), RouteFinderActivity.class);
        finish();
        startActivity(intent);
    }

    public void loadImportExport() {
        Intent intent = new Intent(getApplicationContext(), ImportExportActivity.class);
        finish();
        startActivity(intent);
    }

    public void loadSettings() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        finish();
        startActivity(intent);
    }







    public void loadMeasurement() {
        Intent intent = new Intent(getApplicationContext(), MeasureViewImpl.class);
        finish();
        startActivity(intent);
    }

    public void loadCalibrate() {
        Intent intent = new Intent(getApplicationContext(), CalibrateViewImpl.class);
        finish();
        startActivity(intent);
    }

    public void loadRecord() {
        Intent intent = new Intent(getApplicationContext(), RecordViewImpl.class);
        finish();
        startActivity(intent);
    }

    public void loadEdgeDetails(String startNodeID, String targetNodeID) {
        Bundle bundle = new Bundle();
        bundle.putString(STARTNODE_KEY, startNodeID);
        bundle.putString(TARGETNODE_KEY, targetNodeID);

        Intent intent = new Intent(getApplicationContext(), EdgeDetailsViewImpl.class);
        intent.putExtra(EDGE_DETAILS_BUNDLE, bundle);
        startActivity(intent);
    }
}
