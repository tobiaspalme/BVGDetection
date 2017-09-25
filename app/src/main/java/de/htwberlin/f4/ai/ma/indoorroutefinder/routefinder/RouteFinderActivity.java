package de.htwberlin.f4.ai.ma.indoorroutefinder.routefinder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import de.htwberlin.f4.ai.ma.indoorroutefinder.R;

import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ma.indoorroutefinder.wifi_scanner.WifiScanner;
import de.htwberlin.f4.ai.ma.indoorroutefinder.wifi_scanner.WifiScannerFactory;
import de.htwberlin.f4.ai.ma.indoorroutefinder.android.BaseActivity;
import de.htwberlin.f4.ai.ma.indoorroutefinder.edge.Edge;
import de.htwberlin.f4.ai.ma.indoorroutefinder.location.location_calculator.LocationCalculator;
import de.htwberlin.f4.ai.ma.indoorroutefinder.location.location_calculator.LocationCalculatorFactory;
import de.htwberlin.f4.ai.ma.indoorroutefinder.routefinder.dijkstra.DijkstraAlgorithm;
import de.htwberlin.f4.ai.ma.indoorroutefinder.node.Node;
import de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint.AsyncResponse;
import de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint.Fingerprint;
import de.htwberlin.f4.ai.ma.indoorroutefinder.fingerprint.FingerprintTask;
import de.htwberlin.f4.ai.ma.indoorroutefinder.nodelist.NodeListAdapter;
import de.htwberlin.f4.ai.ma.indoorroutefinder.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.indoorroutefinder.persistence.DatabaseHandlerFactory;
import de.htwberlin.f4.ai.ma.indoorroutefinder.node.NodeShowActivity;
import de.htwberlin.f4.ai.ma.indoorroutefinder.routefinder.dijkstra.DijkstraAlgorithmFactory;

/**
 * Created by Johann Winter
 *
 * This activity provides the find route functionality ("Route finden").
 */

public class RouteFinderActivity extends BaseActivity implements AsyncResponse {

    private Spinner startNodeSpinner;
    Spinner destinationNodeSpinner;
    ImageButton locateButton;
    ImageButton findRouteButton;
    ListView navigationResultListview;
    List<String> itemsStartNodeSpinner;
    private ArrayList<String> itemsDestNodeSpinner;
    CheckBox accessibilityCheckbox;
    private TextView totalDistanceTextview;
    private TextView infobox;

    List<String> navigationResultsList;
    List<Node> allNodes;
    DatabaseHandler databaseHandler;
    private SharedPreferences sharedPreferences;
    private String selectedStartNode;
    private String lastSelectedStartNode;
    private String defaultWifi;
    NodeListAdapter resultListAdapter;
    List<String> nodeNames;
    List<String> nodeDescriptions;
    List<String> nodePicturePaths;
    boolean verboseMode;
    private boolean useSSIDfilter;

    WifiManager wifiManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_routefinder, contentFrameLayout);

        startNodeSpinner = (Spinner) findViewById(R.id.start_node_spinner);
        destinationNodeSpinner = (Spinner) findViewById(R.id.destination_node_spinner);
        locateButton = (ImageButton) findViewById(R.id.locate_button);
        findRouteButton = (ImageButton) findViewById(R.id.find_route_button);
        navigationResultListview = (ListView) findViewById(R.id.navigation_result_listview);
        accessibilityCheckbox = (CheckBox) findViewById(R.id.accessibility_checkbox_navi);
        totalDistanceTextview = (TextView) findViewById(R.id.total_distance_textview);
        infobox = (TextView) findViewById(R.id.infobox_routefinder);

        itemsStartNodeSpinner = new ArrayList<>();
        itemsDestNodeSpinner = new ArrayList<>();
        navigationResultsList = new ArrayList<>();
        nodeNames = new ArrayList<>();
        nodeDescriptions = new ArrayList<>();
        nodePicturePaths = new ArrayList<>();

        lastSelectedStartNode = "";
        findRouteButton.setImageResource(R.drawable.find_route_button);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        databaseHandler = DatabaseHandlerFactory.getInstance(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        allNodes = databaseHandler.getAllNodes();

        useSSIDfilter = sharedPreferences.getBoolean("use_ssid_filter", false);
        defaultWifi = sharedPreferences.getString("default_wifi_network", null);

/*---------- TEST -------------------
        Node n1 = NodeFactory.createInstance("n1", "", FingerprintFactory.createInstance("", null), "", "", "");
        Node n2 = NodeFactory.createInstance("n2", "", FingerprintFactory.createInstance("", null), "", "", "");
        Node n3 = NodeFactory.createInstance("n3", "", FingerprintFactory.createInstance("", null), "", "", "");
        Node n4 = NodeFactory.createInstance("n4", "", FingerprintFactory.createInstance("", null), "", "", "");
        Node n5 = NodeFactory.createInstance("n5", "", FingerprintFactory.createInstance("", null), "", "", "");

        Edge e1 = EdgeFactory.createInstance(n1, n2, true, 4);
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

        *///----------------------
/*
        Node k1 = NodeFactory.createInstance("k1", "", FingerprintFactory.createInstance("", null), "", "", "");
        Node k2 = NodeFactory.createInstance("k2", "", FingerprintFactory.createInstance("", null), "", "", "");
        Node k3 = NodeFactory.createInstance("k3", "", FingerprintFactory.createInstance("", null), "", "", "");
        Node k4 = NodeFactory.createInstance("k4", "", FingerprintFactory.createInstance("", null), "", "", "");
        Node k5 = NodeFactory.createInstance("k5", "", FingerprintFactory.createInstance("", null), "", "", "");
        Node k6 = NodeFactory.createInstance("k6", "", FingerprintFactory.createInstance("", null), "", "", "");

        Edge l1 = EdgeFactory.createInstance(k1, k2, true, 3);
        Edge l2 = EdgeFactory.createInstance(k2, k3, true, 1);
        Edge l3 = EdgeFactory.createInstance(k1, k4, false, 3);
        Edge l4 = EdgeFactory.createInstance(k1, k5, false, 2);
        Edge l5 = EdgeFactory.createInstance(k4, k3, false, 1);
        Edge l6 = EdgeFactory.createInstance(k5, k6, false, 1);
        Edge l7 = EdgeFactory.createInstance(k6, k3, false, 1);


        databaseHandler.deleteNode(k1);
        databaseHandler.deleteNode(k2);
        databaseHandler.deleteNode(k3);
        databaseHandler.deleteNode(k4);
        databaseHandler.deleteNode(k5);
        databaseHandler.deleteNode(k6);

        databaseHandler.deleteEdge(l1);
        databaseHandler.deleteEdge(l2);
        databaseHandler.deleteEdge(l3);
        databaseHandler.deleteEdge(l4);
        databaseHandler.deleteEdge(l5);
        databaseHandler.deleteEdge(l6);
        databaseHandler.deleteEdge(l7);


        databaseHandler.insertNode(k1);
        databaseHandler.insertNode(k2);
        databaseHandler.insertNode(k3);
        databaseHandler.insertNode(k4);
        databaseHandler.insertNode(k5);
        databaseHandler.insertNode(k6);

        databaseHandler.insertEdge(l1);
        databaseHandler.insertEdge(l2);
        databaseHandler.insertEdge(l3);
        databaseHandler.insertEdge(l4);
        databaseHandler.insertEdge(l5);
        databaseHandler.insertEdge(l6);
        databaseHandler.insertEdge(l7);


//-----------------------------------*/
        //navigationResultListview.setOverScrollMode(ListView.OVER_SCROLL_NEVER);


        locateButton.setImageResource(R.drawable.locate);

        // Fill the spinners with Nodes
        for (Node node : allNodes) {
            itemsStartNodeSpinner.add(node.getId());
            itemsDestNodeSpinner.add(node.getId());
        }

        // Disable connect-button if spinnerB has no elements (spinnerA has one or less elements)
        if (itemsStartNodeSpinner.size() < 2) {
            findRouteButton.setEnabled(false);
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

                if (useSSIDfilter) {
                    // If default WiFi is not set in preferences
                    if (defaultWifi == null) {
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
                        // If default WiFi is set in preferences
                    } else {
                        findLocation(defaultWifi, 1);
                    }
                } else {
                    findLocation(null, 1);
                }
            }
        });



        // Start the find_route
        findRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nodeNames.clear();
                nodeDescriptions.clear();
                nodePicturePaths.clear();

                boolean accessible = accessibilityCheckbox.isChecked();

                DijkstraAlgorithm dijkstraAlgorithm = DijkstraAlgorithmFactory.createInstance(getApplicationContext(), accessible);
                dijkstraAlgorithm.execute(selectedStartNode);
                List<String> route = dijkstraAlgorithm.getPath(destinationNodeSpinner.getSelectedItem().toString());

                if (route == null) {
                    nodeNames.add(getString(R.string.no_route_found));
                    nodeDescriptions.add("");
                    nodePicturePaths.add("");
                    resultListAdapter.notifyDataSetChanged();
                    totalDistanceTextview.setText("");
                } else {
                    float totalDistance = 0;
                    for (int i = 0; i < route.size(); i++) {
                        nodeNames.add(route.get(i));
                        nodeDescriptions.add(databaseHandler.getNode(route.get(i)).getDescription());
                        nodePicturePaths.add(databaseHandler.getNode(route.get(i)).getPicturePath());

                        // Add distance (weight) to the results list
                        if (i+1 < route.size()) {
                            Node nodeA = databaseHandler.getNode(route.get(i));
                            Node nodeB = databaseHandler.getNode(route.get(i + 1));

                            Edge e = databaseHandler.getEdge(nodeA, nodeB);
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
        verboseMode = sharedPreferences.getBoolean("verbose_mode", false);
        FingerprintTask fingerprintTask;
        if (verboseMode) {
            fingerprintTask = new FingerprintTask(wifiName, seconds, wifiManager, true, null, null, infobox);
        } else {
            fingerprintTask = new FingerprintTask(wifiName, seconds, wifiManager, true, null, null);
        }
        fingerprintTask.delegate = this;
        fingerprintTask.execute();
    }

    /**
     * When the fingerprinting background task finished
     * @param fingerprint the Fingerprint from the AsyncTask
     * @param seconds the time of measurement in seconds
     */
    @Override
    public void processFinish(Fingerprint fingerprint, int seconds) {
        if (fingerprint != null) {
            LocationCalculator locationCalculator = LocationCalculatorFactory.createInstance(this);
            String foundNode = locationCalculator.calculateNodeId(fingerprint);
            if (foundNode != null) {
                Toast toast = Toast.makeText(this, "Standort: " + foundNode, Toast.LENGTH_SHORT);
                toast.show();
                int index = itemsStartNodeSpinner.indexOf(foundNode);
                startNodeSpinner.setSelection(index);
            } else {
                Toast toast = Toast.makeText(this, getString(R.string.no_location_found), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        locateButton.setEnabled(true);
    }
}
