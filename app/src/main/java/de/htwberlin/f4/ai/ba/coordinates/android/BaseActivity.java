package de.htwberlin.f4.ai.ba.coordinates.android;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.carol.bvg.R;

import de.htwberlin.f4.ai.ba.coordinates.android.calibrate.CalibrateViewImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.measure.MeasureViewImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.measure.edges.EdgeDetailsViewImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.record.RecordViewImpl;
import de.htwberlin.f4.ai.ma.edge.EdgesManagerActivity;
import de.htwberlin.f4.ai.ma.location.LocationActivity;
import de.htwberlin.f4.ai.ma.navigation.NavigationActivity;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.nodelist.NodeListActivity;
import de.htwberlin.f4.ai.ma.prototype_temp.ImportExportActivity;
import de.htwberlin.f4.ai.ma.prototype_temp.NodeRecordActivity;
import de.htwberlin.f4.ai.ma.prototype_temp.SettingsActivity;

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
                        intent = new Intent(getApplicationContext(), NodeRecordActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_location_manager:
                        intent = new Intent(getApplicationContext(), NodeListActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_edges_manager:
                        intent = new Intent(getApplicationContext(), EdgesManagerActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_location_detect:
                        intent = new Intent(getApplicationContext(), LocationActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_navigation:
                        intent = new Intent(getApplicationContext(), NavigationActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_import:
                        intent = new Intent(getApplicationContext(), ImportExportActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_settings:
                        intent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent);
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

    protected void loadMeasurement() {
        Intent intent = new Intent(getApplicationContext(), MeasureViewImpl.class);
        startActivity(intent);
    }

    protected void loadCalibrate() {
        Intent intent = new Intent(getApplicationContext(), CalibrateViewImpl.class);
        startActivity(intent);
    }

    protected void loadRecord() {
        Intent intent = new Intent(getApplicationContext(), RecordViewImpl.class);
        startActivity(intent);
    }

    protected void loadEdgeDetails(String startNodeID, String targetNodeID) {
        Bundle bundle = new Bundle();
        bundle.putString(STARTNODE_KEY, startNodeID);
        bundle.putString(TARGETNODE_KEY, targetNodeID);

        Intent intent = new Intent(getApplicationContext(), EdgeDetailsViewImpl.class);
        intent.putExtra(EDGE_DETAILS_BUNDLE, bundle);
        startActivity(intent);
    }
}
