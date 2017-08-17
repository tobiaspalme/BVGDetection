package de.htwberlin.f4.ai.ma.prototype_temp;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.htwberlin.f4.ai.ba.coordinates.android.BaseActivity;
import de.htwberlin.f4.ai.ma.node.Fingerprint;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.node.NodeFactory;
import de.htwberlin.f4.ai.ma.node.SignalInformation;
import de.htwberlin.f4.ai.ma.node.SignalStrengthInformation;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerFactory;
import de.htwberlin.f4.ai.ma.persistence.JSON.JsonWriter;


public class NodeRecordActivity extends BaseActivity {

    private String wlanName;
    private int recordTime;
    private int progressStatus = 0;
    private Handler mHandler;
    private ProgressBar progressBar;
    private JsonWriter jsonWriter;
    private TextView progressText;

    private Button recordButton;
    Button captureButton;
    Button saveNodeButton;
    private ImageView cameraImageView;
    private EditText nodeIdEdittext;
    private EditText recordTimeText;

    private Spinner wifiNamesDropdown;
    private EditText descriptionEdittext;
    private DatabaseHandler databaseHandler;

    private boolean pictureTaken;
    private boolean fingerprintTaken;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_node_record, contentFrameLayout);


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

        recordButton = (Button) findViewById(R.id.b_record);
        captureButton  = (Button) findViewById(R.id.capture_button);
        saveNodeButton = (Button) findViewById(R.id.save_node_button);
        cameraImageView = (ImageView) findViewById(R.id.camera_imageview);
        descriptionEdittext = (EditText) findViewById(R.id.description_edittext);

        nodeIdEdittext = (EditText) findViewById(R.id.record_id_edittext);
        recordTimeText = (EditText) findViewById(R.id.edTx_measureTime);
        wifiNamesDropdown = (Spinner) findViewById(R.id.wifi_names_dropdown);

        progressText = (TextView) findViewById(R.id.tx_progress);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mHandler = new Handler();

        nodeIdEdittext.setText(getString(R.string.nodeid_input_text));
        recordTimeText.setText("3");
        picturePath = null;

        pictureTaken = false;
        fingerprintTaken = false;
        abortRecording = false;


        if (hasPermissions(this, permissions)) {
            // Scan for WiFi names (SSIDs) and add them to the dropdown
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

        }


        // TODO: if Klausel notwendig?
        if (recordButton != null) {
            recordButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    if (databaseHandler.checkIfNodeExists(nodeIdEdittext.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Ort existiert bereits: Bitte neuen Namen wählen.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        recordButton.setEnabled(false);
                        //wlanName = wlanNameText.getText().toString();
                        wlanName = wifiNamesDropdown.getSelectedItem().toString();

                        recordTime = Integer.parseInt(recordTimeText.getText().toString());
                        measureNode();
                    }
                    abortRecording = false;
                }
            });
        }

        if (captureButton != null) {
            captureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = getFile();
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    startActivityForResult(cameraIntent, CAM_REQUEST);
                }
            });
        }

        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MaxPictureActivity.class);
                intent.putExtra("picturePath", picturePath);
                startActivity(intent);
            }
        });

        saveNodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNewNode();
            }
        });
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
                            Log.d("NodeRecordActivity", "BSSID = " + sr.BSSID + " LVL = " + sr.level);
                            signalStrenghtList.add(signal);
                        }
                    }
                    Log.d("------------------", "--------------------------------------------");

                    SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy-hh.mm.ss");
                    String format = s.format(new Date());
                    SignalInformation signalInformation = new SignalInformation(format, signalStrenghtList);
                    signalInformationList.add(signalInformation);

                    progressStatus += 1;

                    mHandler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            progressText.setText(progressBar.getMax() - progressBar.getProgress() + "s");
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                fingerprintTaken = true;
                progressStatus = 0;
            }

        }).start();


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        System.out.println("requestCode " + requestCode);
        System.out.println("resultCode " + resultCode);


        if (resultCode == -1) {
            pictureTaken = true;

            // TODO necessary?
            picturePath = sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures/Node_" + nodeIdEdittext.getText() + ".jpg";
            //node.setPicturePath(filePath);

            captureButton.setEnabled(false);
            Glide.with(this).load(picturePath).into(cameraImageView);
        }
    }


    private File getFile() {
        File folder = new File(sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures");

        if (!folder.exists()) {
            boolean test = folder.mkdirs();

            if (!test) {
                Log.d("NodeRecordActivity", "DATEI KONNTE NICHT ANGELEGT WERDEN");
            }
        }
        File imageFile = new File(folder, "Node_" + nodeIdEdittext.getText() + ".jpg");
        return imageFile;
    }


    // Create and persist the new Node
    private void saveNewNode() {
        // Determine if picture reference has to be added to Node
        String picPath;
        if (pictureTaken) {
            picPath = sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures/Node_" + nodeIdEdittext.getText() + ".jpg";
        } else {
            picPath = null;
        }

        String nodeID = nodeIdEdittext.getText().toString();
        String nodeDescription = descriptionEdittext.getText().toString();

        if (databaseHandler.checkIfNodeExists(nodeIdEdittext.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Ort existiert bereits: Bitte neuen Namen wählen.",
                    Toast.LENGTH_LONG).show();
        } else {
            final Node node = new NodeFactory().createInstance(nodeID, nodeDescription, new Fingerprint(wlanName, signalInformationList), "", picPath , "");

            if (!fingerprintTaken) {
                new AlertDialog.Builder(this)
                        .setTitle("Kein Fingerprint")
                        .setMessage("Soll der Ort \"" + node.getId() + "\" wirklich ohne Fingerprint erstellt werden?")
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                jsonWriter.writeJSON(node);
                                databaseHandler.insertNode(node);
                                Toast.makeText(context, "Ort gespeichert.", Toast.LENGTH_LONG).show();

                                // Reset progressBar and progress
                                abortRecording = true;
                                progressStatus = 0;
                                progressText.setText(String.valueOf(progressStatus));
                                progressBar.setProgress(progressStatus);
                                recordButton.setEnabled(true);
                                captureButton.setEnabled(true);

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {}
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else {
                jsonWriter.writeJSON(node);
                databaseHandler.insertNode(node);
                progressStatus = 0;
                progressText.setText(String.valueOf(progressStatus));
                progressBar.setProgress(progressStatus);
                Toast.makeText(context, "Ort gespeichert.", Toast.LENGTH_LONG).show();
            }
        }
    }

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


    @Override
    protected void onStop() {
        super.onStop();
        abortRecording = true;

    }
}
