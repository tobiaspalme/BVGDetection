package de.htwberlin.f4.ai.ma.prototype_temp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.carol.bvg.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;

import static de.htwberlin.f4.ai.ma.prototype_temp.RecordActivity.CAM_REQUEST;

/**
 * Created by Johann Winter
 */

public class NodeDetailActivity extends Activity {

    EditText idEditText;
    EditText wlanEditText;
    EditText descriptionEditText;
    EditText coordinatesEditText;
    ImageView cameraImageView;
    Button saveButton;
    Button deleteButton;
    Button changePictureButton;
    Context ctx = this;
    String oldNodeId;
    String picturePath;

    Node node;
    DatabaseHandler databaseHandler;

    File tempFile;
    final File sdCard = Environment.getExternalStorageDirectory();
    final File pictureFolder = new File(sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures");
    final File tempFolder = new File(sdCard.getAbsolutePath() + "/IndoorPositioning/.temp");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_detail);

        idEditText = (EditText) findViewById(R.id.detail_id_edittext);
        wlanEditText = (EditText) findViewById(R.id.wlan_edittext);
        descriptionEditText = (EditText) findViewById(R.id.description_edittext);
        coordinatesEditText = (EditText) findViewById(R.id.coordinates_edittext);
        cameraImageView = (ImageView) findViewById(R.id.camera_imageview);
        saveButton = (Button) findViewById(R.id.save_button);
        deleteButton = (Button) findViewById(R.id.delete_button);
        changePictureButton = (Button) findViewById(R.id.change_picture_button);

        final Intent intent = getIntent();
        final String nodeName = intent.getExtras().get("nodeName").toString();

        // Create picture folders
        if (!pictureFolder.exists()) {
            pictureFolder.mkdirs();
        }
        if (!tempFolder.exists()) {
            tempFolder.mkdirs();
        }


        databaseHandler = new DatabaseHandler(this);
        node = databaseHandler.getNode(nodeName);
        oldNodeId = node.getId();

        idEditText.setText(node.getId());
        //TODO wlan-name ermitteln
        //wlanEditText.setText(node.getSignalInformation());
        descriptionEditText.setText(node.getDescription());
        coordinatesEditText.setText(node.getCoordinates());


        picturePath = node.getPicturePath();
        if (picturePath != null) {
            Glide.with(this).load(node.getPicturePath()).into(cameraImageView);

            System.out.println("picturePath = " + picturePath);
        }


        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MaxPictureActivity.class);
                intent.putExtra("picturePath", node.getPicturePath());
                startActivity(intent);
            }
        });



        changePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                tempFile = new File(tempFolder, "Node_" + node.getId() + ".jpg");

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                startActivityForResult(cameraIntent, CAM_REQUEST);
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                node.setId(idEditText.getText().toString());
                node.setDescription(descriptionEditText.getText().toString());
                node.setCoordinates(coordinatesEditText.getText().toString());
                //node.setPicturePath(picturePath);

                if (tempFile != null) {
                    try {
                        copyFile(tempFile, new File(pictureFolder, "Node_" + idEditText.getText().toString() + ".jpg"));
                        node.setPicturePath(sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures/Node_" + idEditText.getText().toString() + ".jpg");
                    } catch (IOException ioException) {
                        System.out.println("IOException while copying image from temp folder to picture folder");
                    }
                }
                databaseHandler.updateNode(node, oldNodeId);

                finish();
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx)
                        .setTitle("Löschen?")
                        .setMessage("Soll der Node \"" + nodeName + "\" wirklich gelöscht werden?")
                        .setCancelable(false)
                        .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                databaseHandler.deleteNode(node);
                                File folder = new File(sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures");
                                File imageFile = new File(folder, "Node_" + node.getId() + ".jpg");
                                imageFile.delete();
                                finish();
                            }
                        })
                        .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Glide.with(this).load(tempFile).into(cameraImageView);
    }


    // Copy temporary image file to image folder
    public static void copyFile(File source, File destination) throws IOException {
        FileChannel inChannel = new FileInputStream(source).getChannel();
        FileChannel outChannel = new FileOutputStream(destination).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            outChannel.close();

            source.delete();
        }
    }
}
