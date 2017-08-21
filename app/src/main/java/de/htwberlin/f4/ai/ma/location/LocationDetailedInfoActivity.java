package de.htwberlin.f4.ai.ma.location;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.carol.bvg.R;


import java.util.ArrayList;

import de.htwberlin.f4.ai.ba.coordinates.android.BaseActivity;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerFactory;

/**
 * Created by Johann Winter
 */

public class LocationDetailedInfoActivity extends BaseActivity{

    private ListView resultsListview;
    private DatabaseHandler databaseHandler;
    private LocationResultAdapter locationResultAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_location_detailed_info, contentFrameLayout);


        resultsListview = (ListView) findViewById(R.id.results_listview);
        databaseHandler = DatabaseHandlerFactory.getInstance(this);



        final ArrayList<LocationResultImplementation> allResults = databaseHandler.getAllLocationResults();

        locationResultAdapter = new LocationResultAdapter(this, allResults);
        resultsListview.setAdapter(locationResultAdapter);



        //delete entry with long click
        resultsListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Eintrag löschen")
                        .setMessage("Möchten sie den Eintrag löschen?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                databaseHandler.deleteLocationResult(allResults.get(position));
                                locationResultAdapter.remove(allResults.get(position));
                                locationResultAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return false;
            }
        });



    }
}
