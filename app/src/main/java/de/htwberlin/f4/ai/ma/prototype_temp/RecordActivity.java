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
import de.htwberlin.f4.ai.ma.persistence.JsonWriter;


public class RecordActivity extends AppCompatActivity {

    String id;
    String description;
    String wlanName;
    int recordTime;
    int mProgressStatus = 0;
    Handler mHandler;
    ProgressBar mProgress;
    JsonWriter jsonWriter;
    TextView progressText;

    Button recordButton;
    Button captureButton;
    ImageView cameraImageView;
    EditText idName;
    EditText recordTimeText;
    EditText wlanNameText;
    EditText descriptionEdittext;

    static final int CAM_REQUEST = 1;

    NodeFactory nodeFactory;
    DatabaseHandler databaseHandler;

    File sdCard = Environment.getExternalStorageDirectory();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_record);

        databaseHandler = new DatabaseHandler(this);
        jsonWriter = new JsonWriter(this);

        recordButton = (Button) findViewById(R.id.b_record);
        captureButton  = (Button) findViewById(R.id.capture_button);
        cameraImageView = (ImageView) findViewById(R.id.camera_imageview);
        descriptionEdittext = (EditText) findViewById(R.id.description_edittext);

        idName = (EditText) findViewById(R.id.record_id_edittext);
        recordTimeText = (EditText) findViewById(R.id.edTx_measureTime);
        wlanNameText = (EditText) findViewById(R.id.edTx_WLan);

        progressText = (TextView) findViewById(R.id.tx_progress);

        mProgress = (ProgressBar) findViewById(R.id.progress_bar);
        mHandler = new Handler();

        idName.setText("bitte eingeben");
        recordTimeText.setText("3");
        wlanNameText.setText("BVG Wi-Fi");

        if (recordButton != null) {
            recordButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    id = idName.getText().toString();
                    description = descriptionEdittext.getText().toString();
                    wlanName = wlanNameText.getText().toString();
                    recordTime = Integer.parseInt(recordTimeText.getText().toString());
                    measureNode();
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
                intent.putExtra("pictureName", idName.getText());
                startActivity(intent);
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


                Node node = nodeFactory.getInstance(id, 0, description, signalInformationList, "", "");
                //de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node node = new Node(id, 0, signalInformationList);
                jsonWriter.writeJSON(node);
                databaseHandler.insertNode(node);

                mProgressStatus = 0;

            }
        }).start();
    }



    // TODO!
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String filePath = sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures/Node_" + idName.getText() + ".jpg";
        Glide.with(this).load(filePath).into(cameraImageView);
    }


    private File getFile() {
        File folder = new File(sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures");

        if (!folder.exists()) {
            folder.mkdirs();
        }

        File imageFile = new File(folder, "Node_" + idName.getText() + ".jpg");
        return imageFile;
    }
}
