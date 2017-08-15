package de.htwberlin.f4.ai.ma.edge;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.carol.bvg.R;

import java.util.ArrayList;

import de.htwberlin.f4.ai.ba.coordinates.android.BaseActivity;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerFactory;

/**
 * Created by Johann Winter
 */

public class EdgesManagerActivity extends BaseActivity {

    private Spinner spinnerA;
    private Spinner spinnerB;

    Button connectButton;
    ListView edgesListView;
    ArrayList<Node> allNodes;
    ArrayList<String> itemsSpinnerA;

    private ArrayList<String> itemsSpinnerB;
    private ArrayList<String> itemsEdgesList;
    private ArrayList<Edge> allEdges;
    DatabaseHandler databaseHandler;
    private CheckBox accessibilityCheckbox;
    private String lastSelectedItemA;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_edges_manager);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_edges_manager, contentFrameLayout);


        spinnerA = (Spinner) findViewById(R.id.nodeA_spinner);
        spinnerB = (Spinner) findViewById(R.id.nodeB_spinner);
        connectButton = (Button) findViewById(R.id.connect_nodes_button);
        edgesListView = (ListView) findViewById(R.id.edges_listview);
        accessibilityCheckbox = (CheckBox) findViewById(R.id.accessibility_checkbox);

        databaseHandler = DatabaseHandlerFactory.getInstance(this);

        itemsSpinnerA = new ArrayList<>();
        itemsSpinnerB = new ArrayList<>();
        itemsEdgesList = new ArrayList<>();
        allEdges = new ArrayList<>();
        lastSelectedItemA = "";


        allNodes = databaseHandler.getAllNodes();


        for (de.htwberlin.f4.ai.ma.node.Node node : allNodes) {
            itemsSpinnerA.add(node.getId());
            itemsSpinnerB.add(node.getId());
        }

        // Disable connect-button if spinnerB has no elements (spinnerA has one or less elements)
        if (itemsSpinnerA.size() < 2) {
            connectButton.setEnabled(false);
        }


        final ArrayAdapter<String> adapterA = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsSpinnerA);
        spinnerA.setAdapter(adapterA);

        final ArrayAdapter<String> adapterB = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsSpinnerB);
        spinnerB.setAdapter(adapterB);

        final ArrayAdapter<String> edgesListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemsEdgesList);
        edgesListView.setAdapter(edgesListAdapter);


        // Exclude on spinnerA selected item on spinnerB
        spinnerA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {

                String selectedA = spinnerA.getSelectedItem().toString();

                if (!selectedA.equals(lastSelectedItemA)) {
                    if (!lastSelectedItemA.equals("")) {
                        itemsSpinnerB.add(lastSelectedItemA);
                    }
                    itemsSpinnerB.remove(selectedA);
                    adapterB.notifyDataSetChanged();
                    lastSelectedItemA = selectedA;
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {}
        });


        // Load edges list
        for (Edge e : databaseHandler.getAllEdges()) {
            allEdges.add(e);
            if (e.getAccessibility()) {
                itemsEdgesList.add(e.getNodeA().getId() + " <---> " + e.getNodeB().getId() + ",        " + getString(R.string.accessibility_checkbox_text));
            } else {
                itemsEdgesList.add(e.getNodeA().getId() + " <---> " + e.getNodeB().getId());
            }
        }


        // Save new edge
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean accessible = false;
                if (accessibilityCheckbox.isChecked()) { accessible = true; }

                // TODO set expenditure...

                Node nodeA = databaseHandler.getNode(spinnerA.getSelectedItem().toString());
                Node nodeB = databaseHandler.getNode(spinnerB.getSelectedItem().toString());

                Edge edge = new EdgeImplementation(nodeA, nodeB, accessible, 0);

                if (databaseHandler.checkIfEdgeExists(edge)) {
                    Toast.makeText(getApplicationContext(), "Dieser Weg existiert bereits.",
                            Toast.LENGTH_LONG).show();
                } else {
                    databaseHandler.insertEdge(edge);
                    allEdges.add(edge);

                    if (accessible) {
                        itemsEdgesList.add(edge.getNodeA().getId() + " <---> " + edge.getNodeB().getId() + ",        " + getString(R.string.accessibility_checkbox_text));
                    } else {
                        itemsEdgesList.add(edge.getNodeA().getId() + " <---> " + edge.getNodeB().getId());
                    }
                    edgesListAdapter.notifyDataSetChanged();
                }

            }
        });





        // Long click on item -> delete item
        edgesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Eintrag löschen")
                        .setMessage("Möchten sie den Eintrag löschen?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                databaseHandler.deleteEdge(allEdges.get(position));
                                allEdges.remove(position);
                                itemsEdgesList.remove(position);
                                edgesListAdapter.notifyDataSetChanged();

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
