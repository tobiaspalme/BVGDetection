package de.htwberlin.f4.ai.ma.prototype_temp;

import android.app.Activity;
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

    ListView nodeList;
    DatabaseHandler databaseHandler;
    ArrayList<String> items;
    ArrayAdapter<String> adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nodelist);

        nodeList = (ListView) findViewById(R.id.nodeListListview);
        databaseHandler = new DatabaseHandler(this);

        List<Node> allNodes = databaseHandler.getAllNodes();

        items = new ArrayList<>();
        for (Node id : allNodes) {
            items.add(id.getId().toString());
        }
        Collections.sort(items);

        //fill list with adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        nodeList.setAdapter(adapter);

        nodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println();
            }
        });


        /*
        //delete enty with long click
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Eintrag löschen")
                        .setMessage("möchten sie " + listView.getAdapter().getItem(position).toString() + " löschen?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    int index = 0;
                                    String jsonString = jsonReader.loadJSONFromAsset(getApplicationContext());
                                    JSONObject jsonObj = new JSONObject(jsonString);
                                    JSONArray jsonNode = jsonObj.getJSONArray("Node");
                                    for (int i = 0; i < jsonNode.length(); i++) {
                                        JSONObject jsonObjectNode = jsonNode.getJSONObject(i);
                                        if (jsonObjectNode.length() > 0) {
                                            String idString = jsonObjectNode.getString("id");
                                            if (idString.equals(listView.getAdapter().getItem(position).toString())) {
                                                index = i;
                                                jsonNode.remove(i);
                                                JsonWriter jsonWriter = new JsonWriter(getApplicationContext());
                                                jsonWriter.save(jsonObj);
                                                adapter.remove(items.get(position));
                                                adapter.notifyDataSetChanged();
                                                break;
                                            }
                                        }
                                    }
                                } catch (final JSONException e) {
                                    Log.e("JSON", "Json parsing error: " + e.getMessage());
                                }
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
    }*/



    }
}
