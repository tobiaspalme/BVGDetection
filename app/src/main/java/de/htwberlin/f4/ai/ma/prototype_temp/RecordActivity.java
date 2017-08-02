package de.htwberlin.f4.ai.ma.prototype_temp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.carol.bvg.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;
import de.htwberlin.f4.ai.ma.fingerprint_generator.node.NodeFactory;
import de.htwberlin.f4.ai.ma.fingerprint_generator.node.SignalInformation;
import de.htwberlin.f4.ai.ma.fingerprint_generator.node.SignalStrengthInformation;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerImplementation;
import de.htwberlin.f4.ai.ma.persistence.JsonWriter;


public class RecordActivity extends AppCompatActivity {

    private String id;
    private String description;
    private String wlanName;
    private int recordTime;
    private int progressStatus = 0;
    private Handler mHandler;
    private ProgressBar progressBar;
    private JsonWriter jsonWriter;
    private TextView progressText;

    private Button recordButton;
    private Button captureButton;
    private Button saveNodeButton;
    private ImageView cameraImageView;
    private EditText idName;
    private EditText recordTimeText;
    private EditText wlanNameText;
    private EditText descriptionEdittext;
    private NodeFactory nodeFactory;
   // DatabaseHandlerImplementation databaseHandlerImplementation;
    private DatabaseHandler databaseHandler;

    private boolean pictureTaken;
    private boolean fingerprintTaken;
    private List<SignalInformation> signalInformationList;

    private String picturePath;

    private static final int CAM_REQUEST = 1;

    private File sdCard = Environment.getExternalStorageDirectory();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_record);

        //databaseHandlerImplementation = new DatabaseHandlerImplementation(this);
        databaseHandler = new DatabaseHandlerImplementation(this);

        jsonWriter = new JsonWriter(this);

        recordButton = (Button) findViewById(R.id.b_record);
        captureButton  = (Button) findViewById(R.id.capture_button);
        saveNodeButton = (Button) findViewById(R.id.save_node_button);
        cameraImageView = (ImageView) findViewById(R.id.camera_imageview);
        descriptionEdittext = (EditText) findViewById(R.id.description_edittext);

        idName = (EditText) findViewById(R.id.record_id_edittext);
        recordTimeText = (EditText) findViewById(R.id.edTx_measureTime);
        wlanNameText = (EditText) findViewById(R.id.edTx_WLan);

        progressText = (TextView) findViewById(R.id.tx_progress);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mHandler = new Handler();

        idName.setText("bitte eingeben");
        recordTimeText.setText("3");
        picturePath = null;

        pictureTaken = false;
        fingerprintTaken = false;



        // TODO: if Klausel notwendig?
        if (recordButton != null) {
            recordButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    id = idName.getText().toString();
                    description = descriptionEdittext.getText().toString();
                    wlanName = wlanNameText.getText().toString();
                    recordTime = Integer.parseInt(recordTimeText.getText().toString());
                    measureNode();
                    recordButton.setEnabled(false);
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
                while (progressStatus < 60 * recordTime) {
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
                System.out.println("aaaaaaaaaa");
                //TODO recordButton.setEnabled(true);
            }

        }).start();


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        pictureTaken = true;
        picturePath = sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures/Node_" + idName.getText() + ".jpg";
        //node.setPicturePath(filePath);
        Glide.with(this).load(picturePath).into(cameraImageView);
    }


    private File getFile() {
        File folder = new File(sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures");

        if (!folder.exists()) {
            folder.mkdirs();
        }

        File imageFile = new File(folder, "Node_" + idName.getText() + ".jpg");
        return imageFile;
    }

    // Persist the new Node
    private void saveNewNode() {
        // Determine if picture reference has to be added to Node
        String picPath;
        if (pictureTaken) {
            picPath = sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures/Node_" + idName.getText() + ".jpg";
        } else {
            picPath = null;
        }

        if (fingerprintTaken) {
            Node node = nodeFactory.getInstance(id, 0, description, signalInformationList, "", picPath , "");
            jsonWriter.writeJSON(node);
            //databaseHandlerImplementation.insertNode(node);
            databaseHandler.insertNode(node);

            finish();
        }
    }
}
