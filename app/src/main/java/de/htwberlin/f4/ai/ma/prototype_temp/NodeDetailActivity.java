package de.htwberlin.f4.ai.ma.prototype_temp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.carol.bvg.R;

import java.io.File;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;

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
    Context ctx = this;
    String oldNodeId;

    Node node;
    DatabaseHandler databaseHandler;

    File sdCard = Environment.getExternalStorageDirectory();


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


        Intent intent = getIntent();
        final String nodeName = intent.getExtras().get("nodeName").toString();

        databaseHandler = new DatabaseHandler(this);
        node = databaseHandler.getNode(nodeName);
        oldNodeId = node.getId();

        idEditText.setText(node.getId());
        //TODO wlan-name ermitteln
        //wlanEditText.setText(node.getSignalInformation().hashCode());
        descriptionEditText.setText(node.getDescription());
        coordinatesEditText.setText(node.getCoordinates());

        String filePath = sdCard.getAbsolutePath() + "/IndoorPositioning/Pictures/Node_" + node.getId() + ".jpg";
        Glide.with(this).load(filePath).into(cameraImageView);

        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MaxPictureActivity.class);
                intent.putExtra("pictureName", node.getId());
                startActivity(intent);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                node.setId(idEditText.getText().toString());
                node.setDescription(descriptionEditText.getText().toString());
                //TODO node.setPicturePath();
                node.setCoordinates(coordinatesEditText.getText().toString());
                databaseHandler.updateNode(node, oldNodeId);
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx)
                        .setTitle("Löschen?")
                        .setMessage("Soll der Node " + nodeName + " wirklich gelöscht werden?")
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
}
