package de.htwberlin.f4.ai.ma.indoor_graph;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.carol.bvg.R;

import java.util.ArrayList;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;

/**
 * Created by Johann Winter
 */

public class EdgesManagerActivity extends Activity {

    private Spinner spinnerA;
    private Spinner spinnerB;
    private Button connectButton;
    private ListView edgesListView;
    private ArrayList<Node> allNodes;
    private ArrayList<String> itemsSpinnerA;
    private ArrayList<String> itemsSpinnerB;
    private ArrayList<String> itemsEdgesList;
    private DatabaseHandler databaseHandler;
    private CheckBox accessiblyCheckbox;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edges_manager);

        spinnerA = (Spinner) findViewById(R.id.nodeA_spinner);
        spinnerB = (Spinner) findViewById(R.id.nodeB_spinner);
        connectButton = (Button) findViewById(R.id.connect_nodes_button);
        edgesListView = (ListView) findViewById(R.id.edges_listview);
        accessiblyCheckbox = (CheckBox) findViewById(R.id.accessibly_checkbox);

        databaseHandler = new DatabaseHandler(this);

        itemsSpinnerA = new ArrayList<>();
        itemsSpinnerB = new ArrayList<>();
        itemsEdgesList = new ArrayList<>();



        // TEST
        //itemsEdgesList.add("asdf -> öljkl");
        //itemsEdgesList.add("ziuop -> a");




        allNodes = databaseHandler.getAllNodes();

        for (de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node node : allNodes) {
            itemsSpinnerA.add(node.getId().toString());
            itemsSpinnerB.add(node.getId().toString());
        }

        final ArrayAdapter<String> adapterA = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsSpinnerA);
        spinnerA.setAdapter(adapterA);

        ArrayAdapter<String> adapterB = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsSpinnerB);
        spinnerB.setAdapter(adapterB);

        final ArrayAdapter<String> edgesListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemsEdgesList);
        edgesListView.setAdapter(edgesListAdapter);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean accessibly = false;
                if (accessiblyCheckbox.isChecked()) { accessibly = true; }
                //else if (!accessiblyCheckbox.isChecked()) { accessibly = false; }


                Edge edge = new EdgeImplementation(1, spinnerA.getSelectedItem().toString(), spinnerB.getSelectedItem().toString(), accessibly );

                System.out.println("new Edge #### :" + edge.getNodeA() +" " + edge.getNodeB() );
                databaseHandler.insertEdge(edge);
                edgesListAdapter.notifyDataSetChanged();
            }
        });





        for (Edge e : databaseHandler.getAllEdges()) {
            itemsEdgesList.add(new String(e.getNodeA() + " ---> " + e.getNodeB()));
        }



    }
}
