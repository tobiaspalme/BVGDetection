package de.htwberlin.f4.ai.ma.routefinder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carol.bvg.R;

import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ma.WifiScanner.WifiScanner;
import de.htwberlin.f4.ai.ma.WifiScanner.WifiScannerFactory;
import de.htwberlin.f4.ai.ma.android.BaseActivity;
import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.edge.EdgeFactory;
import de.htwberlin.f4.ai.ma.fingerprint.FingerprintFactory;
import de.htwberlin.f4.ai.ma.location.location_calculator.LocationCalculator;
import de.htwberlin.f4.ai.ma.location.location_calculator.LocationCalculatorFactory;
import de.htwberlin.f4.ai.ma.node.NodeFactory;
import de.htwberlin.f4.ai.ma.routefinder.dijkstra.DijkstraAlgorithm;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.fingerprint.AsyncResponse;
import de.htwberlin.f4.ai.ma.fingerprint.Fingerprint;
import de.htwberlin.f4.ai.ma.fingerprint.FingerprintTask;
import de.htwberlin.f4.ai.ma.nodelist.NodeListAdapter;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerFactory;
import de.htwberlin.f4.ai.ma.node.NodeShowActivity;
import de.htwberlin.f4.ai.ma.location.calculations.FoundNode;
import de.htwberlin.f4.ai.ma.routefinder.dijkstra.DijkstraAlgorithmFactory;

/**
 * Created by Johann Winter
 *
 * This activity provides the find_route functionality ("Navigation").
 */

public class RouteFinderActivity extends BaseActivity implements AsyncResponse {

    private Spinner startNodeSpinner;
    Spinner destinationNodeSpinner;
    Button startRouteFinder;
    ImageButton locateButton;
    ListView navigationResultListview;
    List<String> itemsStartNodeSpinner;
    private ArrayList<String> itemsDestNodeSpinner;
    CheckBox accessibilityCheckbox;
    private TextView totalDistanceTextview;

    List<String> navigationResultsList;
    List<Node> allNodes;
    DatabaseHandler databaseHandler;
    private String selectedStartNode;
    private String lastSelectedStartNode;
    NodeListAdapter resultListAdapter;
    List<String> nodeNames;
    List<String> nodeDescriptions;
    List<String> nodePicturePaths;

    WifiManager wifiManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_navigation, contentFrameLayout);

        startNodeSpinner = (Spinner) findViewById(R.id.start_node_spinner);
        destinationNodeSpinner = (Spinner) findViewById(R.id.destination_node_spinner);
        startRouteFinder = (Button) findViewById(R.id.start_navigation_button);
        locateButton = (ImageButton) findViewById(R.id.locate_button);
        navigationResultListview = (ListView) findViewById(R.id.navigation_result_listview);
        accessibilityCheckbox = (CheckBox) findViewById(R.id.accessibility_checkbox_navi);
        totalDistanceTextview = (TextView) findViewById(R.id.total_distance_textview);

        itemsStartNodeSpinner = new ArrayList<>();
        itemsDestNodeSpinner = new ArrayList<>();
        navigationResultsList = new ArrayList<>();
        nodeNames = new ArrayList<>();
        nodeDescriptions = new ArrayList<>();
        nodePicturePaths = new ArrayList<>();

        lastSelectedStartNode = "";

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        databaseHandler = DatabaseHandlerFactory.getInstance(this);
        allNodes = databaseHandler.getAllNodes();


//---------- TEST -------------------
        Node n1 = NodeFactory.createInstance("n1", "", FingerprintFactory.createInstance("", null), "", "", "");
        Node n2 = NodeFactory.createInstance("n2", "", FingerprintFactory.createInstance("", null), "", "", "");
        Node n3 = NodeFactory.createInstance("n3", "", FingerprintFactory.createInstance("", null), "", "", "");
        Node n4 = NodeFactory.createInstance("n4", "", FingerprintFactory.createInstance("", null), "", "", "");
        Node n5 = NodeFactory.createInstance("n5", "", FingerprintFactory.createInstance("", null), "", "", "");

        Edge e1 = EdgeFactory.createInstance(n1, n2, false, 4);
        Edge e2 = EdgeFactory.createInstance(n2, n3, false, 5);
        Edge e3 = EdgeFactory.createInstance(n1, n3, true, 11);
        Edge e4 = EdgeFactory.createInstance(n1, n4, true, 8);
        Edge e5 = EdgeFactory.createInstance(n4, n5, true, 1);
        Edge e6 = EdgeFactory.createInstance(n5, n3, true, 1);

        databaseHandler.deleteNode(n1);
        databaseHandler.deleteNode(n2);
        databaseHandler.deleteNode(n3);
        databaseHandler.deleteNode(n4);
        databaseHandler.deleteNode(n5);

        databaseHandler.deleteEdge(e1);
        databaseHandler.deleteEdge(e2);
        databaseHandler.deleteEdge(e3);
        databaseHandler.deleteEdge(e4);
        databaseHandler.deleteEdge(e5);
        databaseHandler.deleteEdge(e6);


        databaseHandler.insertNode(n1);
        databaseHandler.insertNode(n2);
        databaseHandler.insertNode(n3);
        databaseHandler.insertNode(n4);
        databaseHandler.insertNode(n5);

        databaseHandler.insertEdge(e1);
        databaseHandler.insertEdge(e2);
        databaseHandler.insertEdge(e3);
        databaseHandler.insertEdge(e4);
        databaseHandler.insertEdge(e5);
        databaseHandler.insertEdge(e6);
//-----------------------------------
        //navigationResultListview.setOverScrollMode(ListView.OVER_SCROLL_NEVER);


        locateButton.setImageResource(R.drawable.locate);

        // Fill the spinners with Nodes
        for (de.htwberlin.f4.ai.ma.node.Node node : allNodes) {
            itemsStartNodeSpinner.add(node.getId());
            itemsDestNodeSpinner.add(node.getId());
        }

        // Disable connect-button if spinnerB has no elements (spinnerA has one or less elements)
        if (itemsStartNodeSpinner.size() < 2) {
            startRouteFinder.setEnabled(false);
        }


        // Set adapters and attach the lists
        final ArrayAdapter<String> adapterA = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsStartNodeSpinner);
        startNodeSpinner.setAdapter(adapterA);
        final ArrayAdapter<String> adapterB = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsDestNodeSpinner);
        destinationNodeSpinner.setAdapter(adapterB);
        resultListAdapter = new NodeListAdapter(this, nodeNames, nodeDescriptions, nodePicturePaths);
        navigationResultListview.setAdapter(resultListAdapter);


        // Exclude on spinnerA selected item on spinnerB
        startNodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedStartNode = startNodeSpinner.getSelectedItem().toString();

                if (!selectedStartNode.equals(lastSelectedStartNode)) {
                    if (!lastSelectedStartNode.equals("")) {
                        itemsDestNodeSpinner.add(lastSelectedStartNode);
                    }
                    itemsDestNodeSpinner.remove(selectedStartNode);
                    adapterB.notifyDataSetChanged();
                    lastSelectedStartNode = selectedStartNode;
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        // Get WiFis around and ask the user which to use
        locateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                locateButton.setEnabled(false);

                WifiScanner wifiScanner = WifiScannerFactory.createInstance();
                final List<String> wifiNamesList = wifiScanner.getAvailableNetworks(wifiManager, true);

                final CharSequence wifiArray[] = new CharSequence[wifiNamesList.size()];
                for (int i = 0; i < wifiArray.length; i++) {
                    wifiArray[i] = wifiNamesList.get(i);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(getString(R.string.select_wifi));
                builder.setItems(wifiArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        findLocation(wifiNamesList.get(which), 1);
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        });



        // Start the find_route
        startRouteFinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nodeNames.clear();
                nodeDescriptions.clear();
                nodePicturePaths.clear();

                boolean accessible = accessibilityCheckbox.isChecked();

                DijkstraAlgorithm dijkstraAlgorithm = DijkstraAlgorithmFactory.createInstance(getApplicationContext(), accessible);
                dijkstraAlgorithm.execute(selectedStartNode);
                List<Node> route = dijkstraAlgorithm.getPath(destinationNodeSpinner.getSelectedItem().toString());

                if (route == null) {
                    nodeNames.add(getString(R.string.no_route_found));
                    nodeDescriptions.add("");
                    nodePicturePaths.add("");
                    resultListAdapter.notifyDataSetChanged();
                    totalDistanceTextview.setText("");
                } else {
                    float totalDistance = 0;
                    for (int i = 0; i < route.size(); i++) {
                        nodeNames.add(route.get(i).getId());
                        nodeDescriptions.add(databaseHandler.getNode(route.get(i).getId()).getDescription());
                        nodePicturePaths.add(databaseHandler.getNode(route.get(i).getId()).getPicturePath());

                        // Add distance (weight) to the results list
                        if (i+1 < route.size()) {
                            Edge e = databaseHandler.getEdge(route.get(i), route.get(i + 1));
                            nodeNames.add("\t" + String.valueOf(e.getWeight()) + " m");
                            nodeDescriptions.add("");
                            nodePicturePaths.add("");
                            totalDistance += e.getWeight();
                        }
                    }

                    resultListAdapter.notifyDataSetChanged();
                    totalDistanceTextview.setText("Gesamtstrecke: " + String.valueOf(totalDistance) + " m");

                    // Click on Item -> show Node in NodeEditActivity
                    navigationResultListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // Only nodes will be clickable, not the separators
                            if (position % 2 == 0) {
                                Intent intent = new Intent(getApplicationContext(), NodeShowActivity.class);
                                intent.putExtra("nodeName", navigationResultListview.getAdapter().getItem(position).toString());
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }


    /**
     * Try to find the location and set it to the start-spinner
     * @param wifiName the WiFi to measure
     * @param seconds duration of the measurement in seconds
     */
    private void findLocation(String wifiName, int seconds) {

        FingerprintTask fingerprintTask = new FingerprintTask(wifiName, seconds, wifiManager, true, null, null);
        fingerprintTask.delegate = this;
        fingerprintTask.execute();
    }

    /**
     * When the fingerprinting background task finished
     * @param fingerprint the Fingerprint from the AsyncTask
     */
    @Override
    public void processFinish(Fingerprint fingerprint, int seconds) {
        if (fingerprint != null) {
            LocationCalculator locationCalculator = LocationCalculatorFactory.createInstance(this);
            FoundNode foundNode = locationCalculator.calculateNodeId(fingerprint);
            if (foundNode != null) {
                Toast toast = Toast.makeText(this, "Standort: " + foundNode.getId(), Toast.LENGTH_SHORT);
                toast.show();
                int index = itemsStartNodeSpinner.indexOf(foundNode.getId());
                startNodeSpinner.setSelection(index);
            } else {
                Toast toast = Toast.makeText(this, getString(R.string.no_location_found), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        locateButton.setEnabled(true);
    }
}
