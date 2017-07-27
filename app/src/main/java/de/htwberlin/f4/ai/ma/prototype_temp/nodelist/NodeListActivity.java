package de.htwberlin.f4.ai.ma.prototype_temp.nodelist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.carol.bvg.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.prototype_temp.NodeDetailActivity;


/**
 * Created by Johann Winter
 */

public class NodeListActivity extends Activity {

    ListView nodeListView;
    ArrayList<String> nodeNames;
    ArrayList<String> nodeDescriptions;
    ArrayList<String> nodePicturePaths;

    ArrayList<Node> allNodes;
    NodeListAdapter nodeListAdapter;
    DatabaseHandler databaseHandler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nodelist);

        databaseHandler = new DatabaseHandler(this);

        nodeListView = (ListView) findViewById(R.id.nodeListListview);

        allNodes = new ArrayList<>();
        nodeNames = new ArrayList<>();
        nodeDescriptions = new ArrayList<>();
        nodePicturePaths = new ArrayList<>();

        nodeListAdapter = new NodeListAdapter(this, nodeNames, nodeDescriptions, nodePicturePaths);
        nodeListView.setAdapter(nodeListAdapter);

        loadDbData();

        nodeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), NodeDetailActivity.class);
                intent.putExtra("nodeName", nodeListView.getAdapter().getItem(position).toString());
                startActivity(intent);
            }
        });
    }


    // TODO update List when item deleted
    @Override
    protected void onResume() {
        super.onResume();
        loadDbData();
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadDbData();
        System.out.println("### Onactivityresult");
    }*/

    private void loadDbData() {

        nodeDescriptions.clear();
        nodeNames.clear();
        nodePicturePaths.clear();
        allNodes.clear();

        allNodes.addAll(databaseHandler.getAllNodes());

        for (Node n : allNodes) {
            nodeNames.add(n.getId().toString());
            nodeDescriptions.add(n.getDescription());
            nodePicturePaths.add(n.getPicturePath());
        }
        //Collections.sort(nodeNames);

        nodeListAdapter.notifyDataSetChanged();
    }
}
