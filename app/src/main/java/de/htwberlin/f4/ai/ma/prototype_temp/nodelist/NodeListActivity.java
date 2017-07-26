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

    List<Node> allNodes;
    NodeListAdapter nodeListAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nodelist);

        nodeListView = (ListView) findViewById(R.id.nodeListListview);

        nodeNames = new ArrayList<>();
        nodeDescriptions = new ArrayList<>();
        nodePicturePaths = new ArrayList<>();

        loadDbData();

        nodeListAdapter = new NodeListAdapter(this, nodeNames, nodeDescriptions, nodePicturePaths);
        nodeListView.setAdapter(nodeListAdapter);

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
        nodeListAdapter.notifyDataSetChanged();
    }

    private void loadDbData() {
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        allNodes = databaseHandler.getAllNodes();
        this.nodeNames.clear();

        for (Node n : allNodes) {
            nodeNames.add(n.getId().toString());
            nodeDescriptions.add(n.getDescription());
            nodePicturePaths.add(n.getPicturePath());
        }
        //Collections.sort(nodeNames);

    }
}
