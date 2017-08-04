package de.htwberlin.f4.ai.ma.navigation;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.carol.bvg.R;

import java.util.ArrayList;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerImplementation;

/**
 * Created by Johann Winter
 */

public class NavigationActivity extends Activity {


    private Spinner startNodeSpinner;
    Spinner destinationNodeSpinner;
    Button startNavigationButton;
    ListView navigationResultListview;
    ArrayList<String> itemsStartNodeSpinner;
    private ArrayList<String> itemsDestNodeSpinner;
    ArrayList<String> navigationResultsList;
    ArrayList<Node> allNodes;
    DatabaseHandler databaseHandler;
    private String lastSelectedStartNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        startNodeSpinner = (Spinner) findViewById(R.id.start_node_spinner);
        destinationNodeSpinner = (Spinner) findViewById(R.id.destination_node_spinner);
        startNavigationButton = (Button) findViewById(R.id.start_navigation_button);
        navigationResultListview = (ListView) findViewById(R.id.navigation_result_listview);

        itemsStartNodeSpinner = new ArrayList<>();
        itemsDestNodeSpinner = new ArrayList<>();
        navigationResultsList = new ArrayList<>();

        lastSelectedStartNode = "";

        databaseHandler = new DatabaseHandlerImplementation(this);
        allNodes = databaseHandler.getAllNodes();



        for (de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node node : allNodes) {
            itemsStartNodeSpinner.add(node.getId());
            itemsDestNodeSpinner.add(node.getId());
        }

        // Disable connect-button if spinnerB has no elements (spinnerA has one or less elements)
        if (itemsStartNodeSpinner.size() < 2) {
            startNavigationButton.setEnabled(false);
        }


        final ArrayAdapter<String> adapterA = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsStartNodeSpinner);
        startNodeSpinner.setAdapter(adapterA);

        final ArrayAdapter<String> adapterB = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsDestNodeSpinner);
        destinationNodeSpinner.setAdapter(adapterB);

        final ArrayAdapter<String> edgesListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, navigationResultsList);
        navigationResultListview.setAdapter(edgesListAdapter);


        // Exclude on spinnerA selected item on spinnerB
        startNodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {

                String selectedStartNode = startNodeSpinner.getSelectedItem().toString();

                if (!selectedStartNode.equals(lastSelectedStartNode)) {
                    if (!lastSelectedStartNode.equals("")) {
                        itemsDestNodeSpinner.add(lastSelectedStartNode);
                    }
                    itemsDestNodeSpinner.remove(selectedStartNode);
                    adapterB.notifyDataSetChanged();
                    lastSelectedStartNode = selectedStartNode;
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {}
        });


    }
}
