package de.htwberlin.f4.ai.ma.nodelist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.carol.bvg.R;

import java.util.ArrayList;

import de.htwberlin.f4.ai.ba.coordinates.android.BaseActivity;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerImplementation;
import de.htwberlin.f4.ai.ma.prototype_temp.NodeEditActivity;


/**
 * Created by Johann Winter
 */

public class NodeListActivity extends BaseActivity {

    ListView nodeListView;
    ArrayList<String> nodeNames;
    ArrayList<String> nodeDescriptions;
    ArrayList<String> nodePicturePaths;

    ArrayList<Node> allNodes;
    NodeListAdapter nodeListAdapter;
    DatabaseHandler databaseHandler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_nodelist, contentFrameLayout);

        databaseHandler = new DatabaseHandlerImplementation(this);


        nodeListView = (ListView) findViewById(R.id.nodeListListview);

        allNodes = new ArrayList<>();
        nodeNames = new ArrayList<>();
        nodeDescriptions = new ArrayList<>();
        nodePicturePaths = new ArrayList<>();

        nodeListAdapter = new NodeListAdapter(this, nodeNames, nodeDescriptions, nodePicturePaths);
        nodeListView.setAdapter(nodeListAdapter);

        loadDbData();

        // Click on Item -> show Node in NodeEditActivity
        nodeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), NodeEditActivity.class);
                intent.putExtra("nodeName", nodeListView.getAdapter().getItem(position).toString());
                startActivity(intent);
            }
        });



        // Long click on Node -> delete dialog
        nodeListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Eintrag löschen?")
                        .setMessage("Soll der Node \"" + allNodes.get(position).getId() + "\" wirklich gelöscht werden?")
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

//                                databaseHandlerImplementation.deleteNode(allNodes.get(position));

                                databaseHandler.deleteNode(allNodes.get(position));
                                loadDbData();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadDbData();
    }


    private void loadDbData() {

        nodeDescriptions.clear();
        nodeNames.clear();
        nodePicturePaths.clear();
        allNodes.clear();

        allNodes.addAll(databaseHandler.getAllNodes());

        for (Node n : allNodes) {
            nodeNames.add(n.getId());
            nodeDescriptions.add(n.getDescription());
            nodePicturePaths.add(n.getPicturePath());
        }
        //Collections.sort(nodeNames);

        nodeListAdapter.notifyDataSetChanged();
    }
}
