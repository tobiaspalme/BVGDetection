package de.htwberlin.f4.ai.ma;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carol.bvg.R;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.htwberlin.f4.ai.ma.android.BaseActivity;
import de.htwberlin.f4.ai.ma.node.fingerprint.Fingerprint;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.node.NodeFactory;
import de.htwberlin.f4.ai.ma.node.fingerprint.SignalInformation;
import de.htwberlin.f4.ai.ma.node.fingerprint.SignalStrengthInformation;
import de.htwberlin.f4.ai.ma.nodelist.NodeListActivity;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerFactory;
import de.htwberlin.f4.ai.ma.persistence.JSON.JsonWriter;


public class NodeRecordAndEditActivity extends BaseActivity {

    private String wlanName;
    private int recordTime;
    private int progressStatus = 0;
    private Handler mHandler;
    private ProgressBar progressBar;
    private JsonWriter jsonWriter;
    private TextView progressText;
    private TextView initialWifiTextview;
    private TextView initialWifiLabelTextview;
    private TextView coordinatesLabelTextview;
    private Button recordButton;
    Button captureButton;
    Button saveNodeButton;
    Button deleteNodeButton;
    private ImageView cameraImageview;
    ImageView refreshImageview;
    private EditText nodeIdEdittext;
    private EditText recordTimeText;
    private EditText descriptionEdittext;
    private EditText coordinatesEdittext;
    private Spinner wifiNamesDropdown;
    private DatabaseHandler databaseHandler;

    private boolean pictureTaken;
    private boolean fingerprintTaken;
    private boolean takingPictureAtTheMoment;



    private boolean updateMode = false;
    private String oldNodeId = null;
    private Node nodeToUpdate;



    private List<SignalInformation> signalInformationList;

    private String picturePath;
    private boolean abortRecording;

    private static final int CAM_REQUEST = 1;

    //TODO permissions auf Startscreen auslagern
    String[] permissions;
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 3;

    private File sdCard = Environment.getExternalStorageDirectory();
    private Context context = this;
    private WifiManager mainWifiObj;

    private Timestamp timestamp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_node_record_and_edit, contentFrameLayout);

        permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION};


        if(!hasPermissions(this, permissions)){
            ActivityCompat.requestPermissions(this, permissions, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        }
        else {
            //TODO: Warnmeldung
        }

        databaseHandler = DatabaseHandlerFactory.getInstance(this);
        jsonWriter = new JsonWriter(this);

        recordButton = (Button) findViewById(R.id.record_button);
        captureButton  = (Button) findViewById(R.id.capture_button);
        saveNodeButton = (Button) findViewById(R.id.save_node_button);
        deleteNodeButton = (Button) findViewById(R.id.delete_node_button);
        cameraImageview = (ImageView) findViewById(R.id.camera_imageview);
        refreshImageview = (ImageView) findViewById(R.id.refresh_imageview_recordactivity);
        descriptionEdittext = (EditText) findViewById(R.id.description_edittext);
        nodeIdEdittext = (EditText) findViewById(R.id.record_id_edittext);
        recordTimeText = (EditText) findViewById(R.id.measure_time_edittext_popup);
        coordinatesEdittext = (EditText) findViewById(R.id.coordinates_edittext);
        wifiNamesDropdown = (Spinner) findViewById(R.id.wifi_names_dropdown);
        progressText = (TextView) findViewById(R.id.progress_textview);
        initialWifiTextview = (TextView) findViewById(R.id.initial_wifi_textview);
        initialWifiLabelTextview = (TextView) findViewById(R.id.initial_wifi_label_textview);
        coordinatesLabelTextview = (TextView) findViewById(R.id.coordinates_label_textview_editmode);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mHandler = new Handler();

        recordTimeText.setText("3");
        picturePath = null;

        pictureTaken = false;
        fingerprintTaken = false;
        abortRecording = false;
        takingPictureAtTheMoment = false;


        if (hasPermissions(this, permissions)) {
            refreshWifiDropdown();
        }


        initialWifiLabelTextview.setVisibility(View.INVISIBLE);
        initialWifiTextview.setVisibility(View.INVISIBLE);
        deleteNodeButton.setVisibility(View.INVISIBLE);
        coordinatesLabelTextview.setVisibility(View.INVISIBLE);
        coordinatesEdittext.setVisibility(View.INVISIBLE);

        // Check if Update-Mode is enabled
        Intent intent = getIntent();
        if (intent.hasExtra("nodeId")) {
            updateMode = true;
            deleteNodeButton.setVisibility(View.VISIBLE);
            initialWifiLabelTextview.setVisibility(View.VISIBLE);
            initialWifiTextview.setVisibility(View.VISIBLE);
            coordinatesEdittext.setVisibility(View.VISIBLE);
            coordinatesLabelTextview.setVisibility(View.VISIBLE);

            oldNodeId = (String) intent.getExtras().get("nodeId");
            nodeToUpdate = databaseHandler.getNode(oldNodeId);
            nodeIdEdittext.setText(nodeToUpdate.getId());
            descriptionEdittext.setText(nodeToUpdate.getDescription());
            coordinatesEdittext.setText(nodeToUpdate.getCoordinates());
            picturePath = nodeToUpdate.getPicturePath();


            if (nodeToUpdate.getFingerprint() != null) {
                initialWifiTextview.setText(nodeToUpdate.getFingerprint().getWifiName());
            }

            if (picturePath == null) {
                Glide.with(this).load(R.drawable.unknown).into(cameraImageview);
            } else {
                Glide.with(this).load(picturePath).into(cameraImageview);
            }

            System.out.println("______UPDATE-MODE____");

        }


        refreshImageview.setImageResource(R.drawable.refresh);
        refreshImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshWifiDropdown();
            }
        });

        recordButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                if (nodeIdEdittext.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), getString(R.string.please_enter_node_name), Toast.LENGTH_SHORT).show();
                } else if (databaseHandler.checkIfNodeExists(nodeIdEdittext.getText().toString()) && !updateMode) {
                    Toast.makeText(getApplicationContext(), getString(R.string.node_already_exists_toast), Toast.LENGTH_SHORT).show();
                } else {

                    recordButton.setEnabled(false);
                    recordTimeText.setEnabled(false);
                    //nodeIdEdittext.setEnabled(false);
                    wifiNamesDropdown.setEnabled(false);
                    //descriptionEdittext.setEnabled(false);

                    wlanName = wifiNamesDropdown.getSelectedItem().toString();
                    recordTime = Integer.parseInt(recordTimeText.getText().toString());
                    measureNode();
                }
                abortRecording = false;
                }
        });


        captureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                if (nodeIdEdittext.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), getString(R.string.please_enter_node_name), Toast.LENGTH_SHORT).show();
                } else {
                    takingPictureAtTheMoment = true;

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = getFile();
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    startActivityForResult(cameraIntent, CAM_REQUEST);
                }
                }
        });


        cameraImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fingerprintTaken) {
                    Intent intent = new Intent(getApplicationContext(), MaxPictureActivity.class);
                    intent.putExtra("picturePath", picturePath);
                    startActivity(intent);
                }
            }
        });

        saveNodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (updateMode) {
                    checkUpdatedNodeID();
                } else {
                    saveNewNode();
                }
            }
        });

        deleteNodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle(getString(R.string.nodelist_delete_entry_title_question))
                        .setMessage("Soll der Node \"" + oldNodeId + "\" wirklich gelöscht werden?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                if (nodeToUpdate.getPicturePath() != null) {
                                    File imageFile = new File(nodeToUpdate.getPicturePath());
                                    imageFile.delete();
                                }
                                databaseHandler.deleteNode(nodeToUpdate);

                                finish();
                                Intent intent = new Intent(context, NodeListActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });


    }


    /**
     * Scan for WiFi names (SSIDs) and add them to the dropdown
     */
    private void refreshWifiDropdown() {
        mainWifiObj = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mainWifiObj.startScan();
        List<ScanResult> wifiScanList = mainWifiObj.getScanResults();

        ArrayList<String> wifiNamesList = new ArrayList<>();
        for (ScanResult sr : wifiScanList) {
            if (!wifiNamesList.contains(sr.SSID) && !sr.SSID.equals("")) {
                wifiNamesList.add(sr.SSID);
            }
        }
        final ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, wifiNamesList);
        wifiNamesDropdown.setAdapter(dropdownAdapter);
        Toast.makeText(getApplicationContext(), getString(R.string.refreshed_toast), Toast.LENGTH_SHORT).show();
    }


    /**
     * make measurement with given record time
     */
    private void measureNode() {
        progressBar.setMax(60 * recordTime);
        progressBar.setProgress(0);

        new Thread(new Runnable() {
            public void run() {
                signalInformationList = new ArrayList<>();
                while (progressStatus < 60 * recordTime && !abortRecording) {
                    List<SignalStrengthInformation> signalStrenghtList = new ArrayList<>();

                    mainWifiObj = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    mainWifiObj.startScan();
                    List<ScanResult> wifiScanList = mainWifiObj.getScanResults();

                    for (ScanResult sr : wifiScanList) {
                        if (sr.SSID.equals(wlanName)) {
                            SignalStrengthInformation signal = new SignalStrengthInformation(sr.BSSID, sr.level);
                            Log.d("NRecordAndEditActivity", "BSSID = " + sr.BSSID + " LVL = " + sr.level);
                            signalStrenghtList.add(signal);
                        }
                    }
                    Log.d("------------------", "--------------------------------------------");

                    SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy-hh.mm.ss", Locale.getDefault());
                    String format = s.format(new Date());
                    SignalInformation signalInformation = new SignalInformation(format, signalStrenghtList);
                    signalInformationList.add(signalInformation);

                    progressStatus += 1;

                    mHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            String progressString = progressBar.getMax() - progressBar.getProgress() + "s";
                            progressText.setText(progressString);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //progressStatus = 0;
                //progressText.setText("Messung beendet.");
                fingerprintTaken = true;
            }
        }).start();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            pictureTaken = true;
            takingPictureAtTheMoment = false;

            long realTimestamp = timestamp.getTime();

            picturePath = sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures/" + nodeIdEdittext.getText() + "_" + realTimestamp +".jpg";
            Glide.with(this).load(picturePath).into(cameraImageview);
        }
    }


    /**
     * Creates the folder /IndoorPositioning/Pictures in SDCARD path,
     * then creates a new Imagefile named by the nodeIdEdittext
     * @return File Object of the Imagefile
     */
    private File getFile() {
        File folder = new File(sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures");

        timestamp = new Timestamp(System.currentTimeMillis());
        long realTimestamp = timestamp.getTime();

        if (!folder.exists()) {
            boolean success = folder.mkdirs();

            if (!success) {
                Log.d("NRecordAndEditActivity", "Die Datei konnte nicht angelegt werden");
            }
        }
        return new File(folder, nodeIdEdittext.getText() + "_" + realTimestamp + ".jpg");
    }


    /**
     * Create and persist the new Node.
     */
    private void saveNewNode() {
        if (nodeIdEdittext.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.please_enter_node_name), Toast.LENGTH_SHORT).show();
        } else {

                final String picPathToSave;
                if (pictureTaken) {
                    long realTimestamp = timestamp.getTime();
                    picPathToSave = sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures/" + nodeIdEdittext.getText() + "_" + realTimestamp + ".jpg";
                } else {
                    picPathToSave = null;
                }


                final String nodeID = nodeIdEdittext.getText().toString();
                final String nodeDescription = descriptionEdittext.getText().toString();

                if (databaseHandler.checkIfNodeExists(nodeIdEdittext.getText().toString())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.node_already_exists_toast), Toast.LENGTH_LONG).show();
                } else {

                    // If no fingerprint has been captured...
                    if (!fingerprintTaken) {
                        new AlertDialog.Builder(this)
                                .setTitle(getString(R.string.no_fingerprint_title_text))
                                .setMessage("Soll der Ort \"" + nodeIdEdittext.getText().toString() + "\" wirklich ohne Fingerprint erstellt werden?")
                                .setCancelable(false)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        final Node node = NodeFactory.createInstance(nodeID, nodeDescription, null, "", picPathToSave, "");
                                        jsonWriter.writeJSON(node);
                                        databaseHandler.insertNode(node);
                                        Toast.makeText(context, getString(R.string.node_saved_toast), Toast.LENGTH_LONG).show();

                                        // Reset progressBar and progress and set the inputs enabled
                                        abortRecording = true;
                                        progressStatus = 0;
                                        progressText.setText(String.valueOf(progressStatus));
                                        progressBar.setProgress(progressStatus);
                                        recordButton.setEnabled(true);
                                        recordTimeText.setEnabled(true);
                                        nodeIdEdittext.setEnabled(true);
                                        wifiNamesDropdown.setEnabled(true);
                                        descriptionEdittext.setEnabled(true);
                                        //captureButton.setEnabled(true);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    // If a fingerprint has been captured...
                    } else {

                        final Node node = NodeFactory.createInstance(nodeID, nodeDescription, new Fingerprint(wlanName, signalInformationList), "", picPathToSave, "");

                        jsonWriter.writeJSON(node);
                        databaseHandler.insertNode(node);
                        progressStatus = 0;
                        progressText.setText(String.valueOf(progressStatus));
                        progressBar.setProgress(progressStatus);
                        Toast.makeText(context, getString(R.string.node_saved_toast), Toast.LENGTH_LONG).show();
                        askForNewNode();

                }
            }
        }
    }


    /**
     * Check if the given nodeID is valid
     */
    private void checkUpdatedNodeID() {
        if (nodeIdEdittext.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), getString(R.string.please_enter_node_name), Toast.LENGTH_SHORT).show();
        } else {
            if (oldNodeId.equals(nodeIdEdittext.getText().toString())) {
                // old id == new id -> update.
               saveUpdatedNode();

            } else {
                if (databaseHandler.checkIfNodeExists(nodeIdEdittext.getText().toString())) {
                    Toast.makeText(getApplicationContext(), getString(R.string.node_already_exists_toast), Toast.LENGTH_LONG).show();
                } else {
                    saveUpdatedNode();
                }
            }
        }
    }


    /**
     * Save an updated Node to database
     */
    private void saveUpdatedNode() {
        final String picPathToSave;
        final String nodeID = nodeIdEdittext.getText().toString();
        final String nodeDescription = descriptionEdittext.getText().toString();
        final String coordinates = coordinatesEdittext.getText().toString();

        if (pictureTaken) {
            long realTimestamp = timestamp.getTime();
            picPathToSave = sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures/" + nodeIdEdittext.getText() + "_" + realTimestamp + ".jpg";
        } else {
            if (picturePath == null) {
                picPathToSave = null;
            } else {
                picPathToSave = picturePath;
            }
        }


        // If no new fingerprint was taken
        if (!fingerprintTaken) {
            // If an old fingerprint exists
            if (nodeToUpdate.getFingerprint() != null) {
                final Node node = NodeFactory.createInstance(nodeID, nodeDescription, nodeToUpdate.getFingerprint(), coordinates, picPathToSave, "");
                jsonWriter.writeJSON(node);
                databaseHandler.updateNode(node, oldNodeId);
                Toast.makeText(context, getString(R.string.node_saved_toast), Toast.LENGTH_LONG).show();

                finish();
                Intent intent = new Intent(context, NodeListActivity.class);
                startActivity(intent);

            // If no new fingerprint was taken and no old exists
            } else {

            //if (nodeToUpdate.getFingerprint() == null) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.no_fingerprint_title_text))
                        .setMessage("Soll der Ort \"" + nodeIdEdittext.getText() + "\" wirklich ohne Fingerprint gespeichert werden?")
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                final Node node = NodeFactory.createInstance(nodeID, nodeDescription, null, coordinates, picPathToSave, "");

                                jsonWriter.writeJSON(node);
                                databaseHandler.updateNode(node, oldNodeId);
                                Toast.makeText(context, getString(R.string.node_saved_toast), Toast.LENGTH_LONG).show();

                                // Reset progressBar and progress
                                abortRecording = true;
                                progressStatus = 0;
                                progressText.setText(String.valueOf(progressStatus));
                                progressBar.setProgress(progressStatus);

                                recordButton.setEnabled(true);
                                recordTimeText.setEnabled(true);
                                nodeIdEdittext.setEnabled(true);
                                wifiNamesDropdown.setEnabled(true);
                                descriptionEdittext.setEnabled(true);
                                //captureButton.setEnabled(true);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

        // If a new fingerprint was taken
        } else {

            final Node node = NodeFactory.createInstance(nodeID, nodeDescription, new Fingerprint(wlanName, signalInformationList), coordinates, picPathToSave, "");
            jsonWriter.writeJSON(node);
            databaseHandler.updateNode(node, oldNodeId);
            Toast.makeText(context, getString(R.string.node_saved_toast), Toast.LENGTH_LONG).show();

            finish();
            Intent intent = new Intent(context, NodeListActivity.class);
            startActivity(intent);
        }
    }



    /**
     * Ask, if new node should be created. If not, finish() and go to NodeListActivity to show all nodes
     */
    private void askForNewNode() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.record_another_node_title_text))
                .setMessage(getString(R.string.record_another_node_question))
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(getIntent());
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent intent = new Intent(context, NodeListActivity.class);
                        startActivity(intent);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    /**
     * Check for permissions (in a loop)
     *
     * @param context the context
     * @return boolean, if all permissions are given
     */
    private boolean hasPermissions(Context context, String[] permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Stop recording thread if the Activity is stopped,
     * except the user left the activity for taking a picture.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (!takingPictureAtTheMoment) {
            abortRecording = true;
        }
    }

}