package de.htwberlin.f4.ai.ma.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.carol.bvg.R;

import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ba.coordinates.android.BaseActivity;
import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.edge.EdgeImplementation;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.navigation.dijkstra.DijkstraAlgorithm;
import de.htwberlin.f4.ai.ma.node.NodeFactory;
import de.htwberlin.f4.ai.ma.node.fingerprint.Fingerprint;
import de.htwberlin.f4.ai.ma.nodelist.NodeListAdapter;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerFactory;
import de.htwberlin.f4.ai.ma.NodeShowActivity;

/**
 * Created by Johann Winter
 */

public class NavigationActivity extends BaseActivity {


    private Spinner startNodeSpinner;
    Spinner destinationNodeSpinner;
    Button startNavigationButton;
    ListView navigationResultListview;
    ArrayList<String> itemsStartNodeSpinner;
    private ArrayList<String> itemsDestNodeSpinner;
    CheckBox accessibilityCheckbox;
    private TextView totalDistanceTextview;

    ArrayList<String> navigationResultsList;
    ArrayList<Node> allNodes;
    DatabaseHandler databaseHandler;
    private String selectedStartNode;
    private String lastSelectedStartNode;
    NodeListAdapter resultListAdapter;
    ArrayList<String> nodeNames;
    ArrayList<String> nodeDescriptions;
    ArrayList<String> nodePicturePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_navigation);


        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_navigation, contentFrameLayout);

        startNodeSpinner = (Spinner) findViewById(R.id.start_node_spinner);
        destinationNodeSpinner = (Spinner) findViewById(R.id.destination_node_spinner);
        startNavigationButton = (Button) findViewById(R.id.start_navigation_button);
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

        databaseHandler = DatabaseHandlerFactory.getInstance(this);


//---------- TEST -------------------
        Node n1 = NodeFactory.createInstance("n1","", new Fingerprint("", null), "", "", "");
        Node n2 = NodeFactory.createInstance("n2","", new Fingerprint("", null), "", "", "");
        Node n3 = NodeFactory.createInstance("n3","", new Fingerprint("", null), "", "", "");
        Node n4 = NodeFactory.createInstance("n4","", new Fingerprint("", null), "", "", "");
        Node n5 = NodeFactory.createInstance("n5","", new Fingerprint("", null), "", "", "");

        Edge e1 = new EdgeImplementation(n1, n2, false, 4);
        Edge e2 = new EdgeImplementation(n2, n3, false, 5);
        Edge e3 = new EdgeImplementation(n1, n3, true, 11);
        Edge e4 = new EdgeImplementation(n1, n4, true, 8);
        Edge e5 = new EdgeImplementation(n4, n5, true, 1);
        Edge e6 = new EdgeImplementation(n5, n3, true, 1);

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



        allNodes = databaseHandler.getAllNodes();


        for (de.htwberlin.f4.ai.ma.node.Node node : allNodes) {
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

        //final ArrayAdapter<String> resultListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, navigationResultsList);
        // navigationResultListview.setAdapter(resultListAdapter);
        resultListAdapter = new NodeListAdapter(this, nodeNames, nodeDescriptions, nodePicturePaths);
        navigationResultListview.setAdapter(resultListAdapter);


        // Exclude on spinnerA selected item on spinnerB
        startNodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {

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
            public void onNothingSelected(AdapterView<?> arg0) {}
        });


        startNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nodeNames.clear();
                nodeDescriptions.clear();
                nodePicturePaths.clear();

                boolean accessible = accessibilityCheckbox.isChecked();

                DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(getApplicationContext(), accessible);
                dijkstraAlgorithm.execute(selectedStartNode);
                List<Node> route = dijkstraAlgorithm.getPath(destinationNodeSpinner.getSelectedItem().toString());

                if (route == null) {
                    nodeNames.add(getString(R.string.no_route_found));
                    nodeDescriptions.add("");
                    nodePicturePaths.add("");
                    resultListAdapter.notifyDataSetChanged();

                } else {
                    /*
                    for (Node n : route) {
                        nodeNames.add(n.getId());
                        nodeDescriptions.add(databaseHandler.getNode(n.getId()).getDescription());
                        nodePicturePaths.add(databaseHandler.getNode(n.getId()).getPicturePath());
                    }
                    */

                    float totalDistance = 0;

                    for (int i = 0; i < route.size(); i++) {
                        nodeNames.add(route.get(i).getId());
                        nodeDescriptions.add(databaseHandler.getNode(route.get(i).getId()).getDescription());
                        nodePicturePaths.add(databaseHandler.getNode(route.get(i).getId()).getPicturePath());

                        // Add distance (weight) to the results list
                        if (i+1 < route.size()) {
                            Edge e = databaseHandler.getEdge(route.get(i), route.get(i + 1));
                            nodeNames.add("             " + String.valueOf(e.getWeight()) + " m");
                            nodeDescriptions.add("");
                            nodePicturePaths.add("");
                            totalDistance += e.getWeight();
                        }
                    }

                    resultListAdapter.notifyDataSetChanged();
                    totalDistanceTextview.setText("Strecke: " + String.valueOf(totalDistance) + " m");

                    // Click on Item -> show Node in NodeEditActivity
                    navigationResultListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getApplicationContext(), NodeShowActivity.class);
                            intent.putExtra("nodeName", navigationResultListview.getAdapter().getItem(position).toString());
                            startActivity(intent);
                        }
                    });
                }

            }
        });



    }
}
