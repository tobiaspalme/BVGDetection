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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carol.bvg.R;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ma.android.BaseActivity;
import de.htwberlin.f4.ai.ma.node.fingerprint.AsyncResponse;
import de.htwberlin.f4.ai.ma.node.fingerprint.Fingerprint;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.node.NodeFactory;
import de.htwberlin.f4.ai.ma.node.fingerprint.FingerprintTask;
import de.htwberlin.f4.ai.ma.nodelist.NodeListActivity;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerFactory;
import de.htwberlin.f4.ai.ma.persistence.FileUtilities;
import de.htwberlin.f4.ai.ma.persistence.JSON.JsonWriter;


public class NodeRecordAndEditActivity extends BaseActivity implements AsyncResponse {

    private String wlanName;
    private String picturePath;
    private String oldNodeId = null;
    String[] permissions;
    private int recordTime;
    private int progressStatus = 0;
    private Handler mHandler;
    private ProgressBar progressBar;
    private JsonWriter jsonWriter;
    private TextView progressTextview;
    TextView initialWifiTextview;
    TextView initialWifiLabelTextview;
    TextView coordinatesLabelTextview;
    private ImageButton recordButton;
    ImageButton captureButton;
    ImageButton saveNodeButton;
    ImageButton deleteNodeButton;
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
    private boolean abortRecording;
    private Node nodeToUpdate;
    private File sdCard = Environment.getExternalStorageDirectory();
    private Context context = this;
    private WifiManager wifiManager;
    private Timestamp timestamp;
    //private List<SignalInformation> signalInformationList;
    private Fingerprint fingerprint;
    private FingerprintTask fingerprintTask;
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 3;
    private static final int CAM_REQUEST = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_node_record_and_edit, contentFrameLayout);

        permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA
        };


        if(!hasPermissions(this, permissions)){
            ActivityCompat.requestPermissions(this, permissions, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        }
        else {
            //TODO: Warnmeldung
        }


        databaseHandler = DatabaseHandlerFactory.getInstance(this);
//        jsonWriter = new JsonWriter(this);
        jsonWriter = new JsonWriter();


        recordButton = (ImageButton) findViewById(R.id.record_button);
        captureButton  = (ImageButton) findViewById(R.id.capture_button);
        saveNodeButton = (ImageButton) findViewById(R.id.save_node_button);
        deleteNodeButton = (ImageButton) findViewById(R.id.delete_node_button);
        cameraImageview = (ImageView) findViewById(R.id.camera_imageview);
        refreshImageview = (ImageView) findViewById(R.id.refresh_imageview_recordactivity);
        descriptionEdittext = (EditText) findViewById(R.id.description_edittext);
        nodeIdEdittext = (EditText) findViewById(R.id.record_id_edittext);
        recordTimeText = (EditText) findViewById(R.id.measure_time_edittext);
        coordinatesEdittext = (EditText) findViewById(R.id.coordinates_edittext);
        wifiNamesDropdown = (Spinner) findViewById(R.id.wifi_names_dropdown);
        progressTextview = (TextView) findViewById(R.id.progress_textview);
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

        deleteNodeButton.setImageResource(R.drawable.trash_node);
        recordButton.setImageResource(R.drawable.fingerprint);
        captureButton.setImageResource(R.drawable.camera);

        progressBar.setVisibility(View.INVISIBLE);
        progressTextview.setVisibility(View.INVISIBLE);
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
            initialWifiTextview.setVisibility(View.VISIBLE);
            oldNodeId = (String) intent.getExtras().get("nodeId");
            nodeToUpdate = databaseHandler.getNode(oldNodeId);
            nodeIdEdittext.setText(nodeToUpdate.getId());
            descriptionEdittext.setText(nodeToUpdate.getDescription());
            picturePath = nodeToUpdate.getPicturePath();

            if (nodeToUpdate.getFingerprint() != null) {
                recordButton.setImageResource(R.drawable.fingerprint_done);
                initialWifiLabelTextview.setVisibility(View.VISIBLE);
                initialWifiTextview.setText(nodeToUpdate.getFingerprint().getWifiName());
            }

            if (nodeToUpdate.getCoordinates().length() > 0) {
                coordinatesEdittext.setVisibility(View.VISIBLE);
                coordinatesLabelTextview.setVisibility(View.VISIBLE);
                coordinatesEdittext.setText(nodeToUpdate.getCoordinates());
            }

            if (picturePath == null) {
                Glide.with(this).load(R.drawable.unknown).into(cameraImageview);
            } else {
                Glide.with(this).load(picturePath).into(cameraImageview);
            }
        }


        saveNodeButton.setImageResource(R.drawable.save);
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
                    wifiNamesDropdown.setEnabled(false);

                    progressBar.setVisibility(View.VISIBLE);
                    progressTextview.setVisibility(View.VISIBLE);
                    recordButton.setImageResource(R.drawable.fingerprint_low_contrast);

                    wlanName = wifiNamesDropdown.getSelectedItem().toString();
                    recordTime = Integer.parseInt(recordTimeText.getText().toString());

                    fingerprintTask = new FingerprintTask(wifiNamesDropdown.getSelectedItem().toString(), 60 * recordTime, wifiManager, false, progressBar, progressTextview);
                    fingerprintTask.delegate = NodeRecordAndEditActivity.this;
                    fingerprintTask.execute();
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

                    timestamp = new Timestamp(System.currentTimeMillis());
                    File file = FileUtilities.getFile(nodeIdEdittext.getText().toString(), timestamp);

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
     * If the fingerprinting background task finished
     * @param fp the Fingerprint from the AsyncTask
     */
    @Override
    public void processFinish(Fingerprint fp, int seconds) {
        fingerprint = fp;

        if (fp == null) {
            recordButton.setImageResource(R.drawable.fingerprint);
        } else {
            recordButton.setImageResource(R.drawable.fingerprint_done);
        }
        progressBar.setVisibility(View.INVISIBLE);
        progressTextview.setVisibility(View.INVISIBLE);
        fingerprintTaken = true;
    }


    /**
     * Scan for WiFi names (SSIDs) and add them to the dropdown
     */
    private void refreshWifiDropdown() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        List<ScanResult> wifiScanList = wifiManager.getScanResults();

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
     * When returning from Camera Activity after taking a picture
     * if a picture was taken (and not just returned),
     * save the path and show the picture in the imageview.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            pictureTaken = true;
            takingPictureAtTheMoment = false;
            long realTimestamp = timestamp.getTime();
            picturePath = sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures/" + nodeIdEdittext.getText() + "_" + realTimestamp + ".jpg";
            Glide.with(this).load(picturePath).into(cameraImageview);
        }
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
                                        resetUiElements();
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

                        final Node node = NodeFactory.createInstance(nodeID, nodeDescription, fingerprint, "", picPathToSave, "");

                        jsonWriter.writeJSON(node);
                        databaseHandler.insertNode(node);
                        progressStatus = 0;
                        progressTextview.setText(String.valueOf(progressStatus));
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
                final Node node = NodeFactory.createInstance(nodeID, nodeDescription, nodeToUpdate.getFingerprint(), coordinates, picPathToSave, nodeToUpdate.getAdditionalInfo());
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
                                resetUiElements();
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

            final Node node = NodeFactory.createInstance(nodeID, nodeDescription, fingerprint, coordinates, picPathToSave, nodeToUpdate.getAdditionalInfo());
            jsonWriter.writeJSON(node);
            databaseHandler.updateNode(node, oldNodeId);
            Toast.makeText(context, getString(R.string.node_saved_toast), Toast.LENGTH_LONG).show();

            finish();
            Intent intent = new Intent(context, NodeListActivity.class);
            startActivity(intent);
        }
    }


    /**
     * Reset buttons. textfields, and progress.
     * Cancel the fingerprinting backgroundtask.
     */
    private void resetUiElements() {

        if (fingerprintTask != null) {
            fingerprintTask.cancel(false);
        }

        abortRecording = true;
        progressStatus = 0;
        progressTextview.setText(String.valueOf(progressStatus));
        progressBar.setProgress(progressStatus);
        recordButton.setEnabled(true);
        recordTimeText.setEnabled(true);
        nodeIdEdittext.setEnabled(true);
        wifiNamesDropdown.setEnabled(true);
        descriptionEdittext.setEnabled(true);
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
            if (fingerprintTask != null) {
                fingerprintTask.cancel(false);
            }



            abortRecording = true;
        }
    }
}