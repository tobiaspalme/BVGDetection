package de.htwberlin.f4.ai.ma.prototype_temp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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


/**
 * Created by Johann Winter
 */

public class NodeListActivity extends Activity {

    ListView nodeListView;
    ArrayList<String> items;
    ArrayAdapter<String> adapter;
    List<Node> allNodes;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nodelist);

        nodeListView = (ListView) findViewById(R.id.nodeListListview);
        items = new ArrayList<>();
        loadDbData();

        //fill list with adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        nodeListView.setAdapter(adapter);

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
        adapter.notifyDataSetChanged();
    }

    private void loadDbData() {
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        allNodes = databaseHandler.getAllNodes();
        this.items.clear();

        for (Node n : allNodes) {
            items.add(n.getId().toString());
        }
        Collections.sort(items);
    }
}
