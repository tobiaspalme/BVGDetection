package de.htwberlin.f4.ai.ma.prototype_temp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.carol.bvg.R;


//import de.htwberlin.f4.ai.ma.indoor_graph.IndoorGraphFactory;
import de.htwberlin.f4.ai.ma.edge.EdgesManagerActivity;
import de.htwberlin.f4.ai.ma.navigation.NavigationActivity;
import de.htwberlin.f4.ai.ma.location.LocationActivity;
import de.htwberlin.f4.ai.ma.nodelist.NodeListActivity;

public class MainActivity extends AppCompatActivity {

    String[] permissions;
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 3;

    Button recordButton;
    Button nodeListButton;
    Button edgesManagerButton;
    Button calculateButton;
    Button navigateButton;
    Button importExportButton;
    Button settingsButton;


    // Test
    Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(!hasPermissions(MainActivity.this, permissions)){
            ActivityCompat.requestPermissions(MainActivity.this, permissions, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        }
        else {
            //TODO: Warnmeldung
        }

        recordButton = (Button) findViewById(R.id.record_button);
        nodeListButton = (Button) findViewById(R.id.nodelist_button);
        edgesManagerButton = (Button) findViewById(R.id.edges_manager_button);
        calculateButton = (Button) findViewById(R.id.calculate_button);
        navigateButton = (Button) findViewById(R.id.navigation_button);
        importExportButton = (Button) findViewById(R.id.import_export_button);
        settingsButton = (Button) findViewById(R.id.settings_button);


        // TODO: if clause sinnvoll?
        if (recordButton != null) {
            recordButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
                    //TODO: richtige Startmethode? Betrifft auch andere Buttons unten
                    startActivities(new Intent[]{intent});
                }
            });
        }


        if (nodeListButton != null) {
            nodeListButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), NodeListActivity.class);
                    startActivities(new Intent[]{intent});
                }
            });
        }

        if (edgesManagerButton != null) {
            edgesManagerButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), EdgesManagerActivity.class);
                    startActivities(new Intent[]{intent});
                }
            });
        }


        if (calculateButton != null) {
            calculateButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                    startActivities(new Intent[]{intent});
                }
            });
        }

        if (navigateButton != null) {
            navigateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (importExportButton != null) {
            importExportButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ImportExportActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (settingsButton != null) {
            settingsButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivities(new Intent[]{intent});
                }
            });
        }


/*
        IndoorGraphFactory indoorGraphFactory = new IndoorGraphFactory();
        IndoorGraph indoorGraph = indoorGraphFactory.createInstance();
        System.out.println(indoorGraph.getIndoorGraphDB().getAllNodes().get(1));
*/

       // IndoorGraph indoorGraph = new IndoorGraphImplementation();
       // indoorGraph.getIndoorGraphDB();


    }

    /**
     * check permission
     * @param context
     * @param permissions
     * @return true or false
     */
    private boolean hasPermissions(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
