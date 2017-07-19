package de.htwberlin.f4.ai.ma.prototype_temp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

//import com.example.carol.bvg.JsonReader;
//import com.example.carol.bvg.JsonWriter;
//import com.example.carol.bvg.Node;
import com.example.carol.bvg.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;
import de.htwberlin.f4.ai.ma.fingerprint_generator.node.NodeFactory;
import de.htwberlin.f4.ai.ma.fingerprint_generator.node.SignalInformation;
import de.htwberlin.f4.ai.ma.fingerprint_generator.node.SignalStrengthInformation;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.JsonReader;
import de.htwberlin.f4.ai.ma.persistence.JsonWriter;

public class RecordActivity extends AppCompatActivity {

    String id;
    String wlanName;
    int recordTime;
    int mProgressStatus = 0;
    Handler mHandler;
    ProgressBar mProgress;
    JsonWriter jsonWriter;
    TextView progressText;
    ArrayAdapter<String> adapter;
    ArrayList<String> items;

    NodeFactory nodeFactory;
    DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        System.out.println("### RECORDACTIVITY");

        databaseHandler = new DatabaseHandler(this);


        jsonWriter = new JsonWriter(this);

        final EditText idName = (EditText) findViewById(R.id.edTx_id);
        final EditText recordTimeText = (EditText) findViewById(R.id.edTx_measureTime);
        final EditText wlanNameText = (EditText) findViewById(R.id.edTx_WLan);
        Button recordButton = (Button) findViewById(R.id.b_record);
        progressText = (TextView) findViewById(R.id.tx_progress);

        mProgress = (ProgressBar) findViewById(R.id.progress_bar);

        mHandler = new Handler();

        if (recordButton != null) {
            recordButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    id = idName.getText().toString();
                    wlanName = wlanNameText.getText().toString();
                    recordTime = Integer.parseInt(recordTimeText.getText().toString());

                    measureNode();

                }
            });
        }

        //create list of nodes with jsonReader
        final JsonReader jsonReader = new JsonReader();
        List<de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node> allNodes = jsonReader.initializeNodeFromJson(this);

        final ListView listView = (ListView) findViewById(R.id.LV_POIList);
        items = new ArrayList<>();
        for (de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node id : allNodes) {
            items.add(id.getId().toString());
        }
        Collections.sort(items);

        //fill list with adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                idName.setText(listView.getAdapter().getItem(position).toString());
            }
        });
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
    }

    /**
     * make measurement with given record time
     */
    private void measureNode() {

        mProgress.setMax(60 * recordTime);
        mProgress.setProgress(0);


        new Thread(new Runnable() {
            public void run() {
                List<SignalInformation> signalInformationList = new ArrayList<>();
                while (mProgressStatus < 60 * recordTime) {
                    List<SignalStrengthInformation> signalStrenghtList = new ArrayList<>();

                    WifiManager mainWifiObj;
                    mainWifiObj = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    mainWifiObj.startScan();
                    List<ScanResult> wifiScanList = mainWifiObj.getScanResults();

                    for (ScanResult sr : wifiScanList) {

                        if (sr.SSID.equals(wlanName)) {
                            SignalStrengthInformation signal = new SignalStrengthInformation(sr.BSSID, sr.level);
                            signalStrenghtList.add(signal);
                        }
                    }
                    SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy-hh.mm.ss");
                    String format = s.format(new Date());
                    SignalInformation signalInformation = new SignalInformation(format, signalStrenghtList);
                    signalInformationList.add(signalInformation);

                    mProgressStatus += 1;

                    mHandler.post(new Runnable() {
                        public void run() {

                            mProgress.setProgress(mProgressStatus);
                            progressText.setText(mProgress.getProgress() + "/" + mProgress.getMax());
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                Node node = nodeFactory.getInstance(id, 0, signalInformationList);
                //de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node node = new Node(id, 0, signalInformationList);
                jsonWriter.writeJSON(node);

                databaseHandler.insertNode(node);

                class UpdateAdapter implements Runnable {
                    de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node node;

                    UpdateAdapter(de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node node) {
                        this.node = node;
                    }

                    public void run() {
                        adapter.add(node.getId().toString());
                        adapter.notifyDataSetChanged();
//                        for (int i = 0; i < adapter.getCount(); i++) {
//                            if (!node.getId().equals(adapter.getItem(i))) {
//                                adapter.add(node.getId().toString());
//                                adapter.notifyDataSetChanged();
//                                break;
//                            }
//                        }

                    }
                }

                runOnUiThread(new UpdateAdapter(node));

                mProgressStatus = 0;

            }
        }).start();
    }


}
