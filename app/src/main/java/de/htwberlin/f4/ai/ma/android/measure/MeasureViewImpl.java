package de.htwberlin.f4.ai.ma.android.measure;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carol.bvg.R;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ma.android.BaseActivity;
import de.htwberlin.f4.ai.ma.android.measure.barcode.BarcodeCaptureActivity;
import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.measurement.WKT;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerFactory;


public class MeasureViewImpl extends BaseActivity implements MeasureView{

    private MeasureController controller;
    private TextView compassView;
    private ImageView compassImageView;
    private TextView stepCounterView;
    private TextView distanceView;
    private TextView coordinatesView;
    private TextView startNodeCoordinatesView;
    private TextView targetNodeCoordinatesView;
    private TextView edgeDistanceView;

    private Button btnStart;
    private Button btnStop;
    private Button btnAdd;

    private Spinner startNodeSpinner;
    private Spinner targetNodeSpinner;

    private ImageView startNodeImage;
    private ImageView targetNodeImage;
    private ImageView handycapImage;
    private ImageView edgeArrow;
    private ImageView locateWifiImage;
    private ImageView locateQrImage;

    private ArrayAdapter<String> startAdapter;
    private ArrayAdapter<String> targetAdapter;
    private final List<String> nodeNames = new ArrayList<>();
    private final List<Node> nodeList = new ArrayList<>();

    public MeasureViewImpl() {
        controller = new MeasureControllerImpl();
        controller.setView(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Wegvermessung");

        final FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_measure, contentFrameLayout);

        DatabaseHandler databaseHandler = DatabaseHandlerFactory.getInstance(getContext());
        List<Node> tmpNodeList = databaseHandler.getAllNodes();

        for (Node node : tmpNodeList) {
            nodeNames.add(node.getId());
            nodeList.add(node);
        }

        compassView = (TextView) findViewById(R.id.coordinates_measure_compass);
        compassImageView = (ImageView) findViewById(R.id.coordinates_measure_compass_iv);
        stepCounterView = (TextView) findViewById(R.id.coordinates_measure_stepvalue);
        distanceView = (TextView) findViewById(R.id.coordinates_measure_distancevalue);
        edgeDistanceView = (TextView) findViewById(R.id.coordinates_measure_edge_distance);

        coordinatesView = (TextView) findViewById(R.id.coordinates_measure_coordinates);

        startNodeImage = (ImageView) findViewById(R.id.coordinates_measure_start_image);
        targetNodeImage = (ImageView) findViewById(R.id.coordinates_measure_target_image);
        edgeArrow = (ImageView) findViewById(R.id.coordinates_measure_arrow);

        startNodeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controller != null) {
                    controller.onStartNodeImageClicked();
                }
            }
        });

        targetNodeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controller != null) {
                    controller.onTargetNodeImageClicked();
                }
            }
        });

        edgeArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controller != null) {
                    controller.onEdgeDetailsClicked();
                }
            }
        });

        handycapImage = (ImageView) findViewById(R.id.coordinates_measure_handycapped);
        // load handycap image as default
        Glide.with(getContext())
                .load(R.drawable.barrierefrei)
                .into(handycapImage);

        locateWifiImage = (ImageView) findViewById(R.id.coordinates_measure_locate_wifi);
        locateWifiImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controller != null) {
                    controller.onLocateWifiClicked();
                }
            }
        });

        locateQrImage = (ImageView) findViewById(R.id.coordinates_measure_locate_qr);
        locateQrImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controller != null) {
                    controller.onLocateQrClicked();
                }
            }
        });

        startNodeCoordinatesView = (TextView) findViewById(R.id.coordinates_measure_start_coords);
        targetNodeCoordinatesView = (TextView) findViewById(R.id.coordinates_measure_target_coords);

        btnStart = (Button) findViewById(R.id.coordinates_measure_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onStartClicked();
                }
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                btnAdd.setEnabled(true);
            }
        });
        btnStart.setEnabled(false);

        btnStop = (Button) findViewById(R.id.coordinates_measure_stop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onStopClicked();
                }
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                btnAdd.setEnabled(false);
            }
        });
        btnStop.setEnabled(false);

        btnAdd = (Button) findViewById(R.id.coordinates_measure_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onAddClicked();
                }
            }
        });
        btnAdd.setEnabled(false);

        startNodeSpinner = (Spinner) findViewById(R.id.coordinates_measure_startnode);
        startAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nodeNames);
        startNodeSpinner.setAdapter(startAdapter);
        startNodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Node startNode = nodeList.get(i);
                if (startNode.getPicturePath() == null) {
                    startNodeImage.setImageResource(R.drawable.unknown);
                } else {
                    Uri imageUri = Uri.parse(startNode.getPicturePath());
                    File image = new File(imageUri.getPath());

                    if (image.exists()) {
                        //using glide to reduce ui lag
                        Glide.with(getContext())
                                .load(startNode.getPicturePath())
                                .into(startNodeImage);
                    }

                }

                if (controller != null) {
                    controller.onStartNodeSelected(startNode);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        targetNodeSpinner = (Spinner) findViewById(R.id.coordinates_measure_targetnode);
        targetAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nodeNames);
        targetNodeSpinner.setAdapter(targetAdapter);
        targetNodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Node targetNode = nodeList.get(i);
                if (targetNode.getPicturePath() == null) {
                    targetNodeImage.setImageResource(R.drawable.unknown);
                } else {

                    Uri imageUri = Uri.parse(targetNode.getPicturePath());
                    File image = new File(imageUri.getPath());

                    if (image.exists()) {
                        //using glide to reduce ui lag
                        Glide.with(getContext())
                                .load(targetNode.getPicturePath())
                                .into(targetNodeImage);
                        //targetNodeImage.setImageURI(Uri.parse(targetNode.getPicturePath()));
                    }
                }

                if (controller != null) {
                    controller.onTargetNodeSelected(targetNode);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (controller != null) {
            controller.onPause();
        }
    }

    @Override
    public void updateAzimuth(float azimuth) {
        double roundAzimuth = Math.round(azimuth * 100.0) / 100.0;
        compassView.setText(String.valueOf(roundAzimuth) + " °");
        compassImageView.setRotation(-azimuth);
    }

    @Override
    public void updateStepCount(int stepCount) {
        stepCounterView.setText(String.valueOf(stepCount));
    }

    @Override
    public void updateDistance(float distance) {
        double roundDistance = Math.round(distance * 100.0) / 100.0;
        distanceView.setText(String.valueOf(roundDistance));
    }

    @Override
    public void updateCoordinates(float x, float y, float z) {
        double roundX = Math.round(x * 100.0) / 100.0;
        double roundY = Math.round(y * 100.0) / 100.0;
        double roundZ = Math.round(z * 100.0) / 100.0;

        coordinatesView.setText(roundX + " | " + roundY + " | " + roundZ);
    }

    @Override
    public void updateStartNodeCoordinates(float x, float y, float z) {
        double roundX = Math.round(x * 100.0) / 100.0;
        double roundY = Math.round(y * 100.0) / 100.0;
        double roundZ = Math.round(z * 100.0) / 100.0;

        startNodeCoordinatesView.setText(roundX + " | " + roundY + " | " + roundZ);
    }

    @Override
    public void updateTargetNodeCoordinates(float x, float y, float z) {
        double roundX = Math.round(x * 100.0) / 100.0;
        double roundY = Math.round(y * 100.0) / 100.0;
        double roundZ = Math.round(z * 100.0) / 100.0;

        targetNodeCoordinatesView.setText(roundX + " | " + roundY + " | " + roundZ);
    }

    @Override
    public void updateEdge(Edge edge) {
        // handycap friendly
        if (edge.getAccessibility()) {
            // load handycap image
            Glide.with(getContext())
                    .load(R.drawable.barrierefrei)
                    .into(handycapImage);
        } else {
            // load handycap image
            Glide.with(getContext())
                    .load(R.drawable.nicht_barrierefrei1)
                    .into(handycapImage);
        }
        // edge weight is in cm, but we use meters, so convert it
        //float edgeDistance = edge.getWeight() / 100.0f;
        float edgeDistance = edge.getWeight();
        edgeDistanceView.setText(String.valueOf(Math.round(edgeDistance * 100.0f) / 100.0f));
    }


    @Override
    public void enableStart() {
        if (btnStart != null) {
            btnStart.setEnabled(true);
        }
    }

    @Override
    public void disableStart() {
        if (btnStart != null) {
            btnStart.setEnabled(false);
        }
    }

    @Override
    public void enableStop() {
        if (btnStop != null) {
            btnStop.setEnabled(true);
        }
    }

    @Override
    public void disableStop() {
        if (btnStop != null) {
            btnStop.setEnabled(false);
        }
    }

    @Override
    public void enableAdd() {
        if (btnAdd != null) {
            btnAdd.setEnabled(true);
        }
    }

    @Override
    public void disableAdd() {
        if (btnAdd != null) {
            btnAdd.setEnabled(false);
        }
    }

    @Override
    public void startQrActivity() {
        Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
        startActivityForResult(intent, 1);
    }

    // used when we receive startnode from wifi scan or qr code
    @Override
    public void setStartNode(Node node) {
        // check if we know node already and stored it in list
        boolean found = false;
        for (String nodeName : nodeNames) {
            if (node.getId().equals(nodeName)) {
                found = true;
                break;
            }
        }
        // new node
        if (!found) {
            startAdapter.add(node.getId());
            nodeList.add(node);

            startNodeSpinner.setSelection(nodeList.indexOf(node));
            if (node.getCoordinates() != null && node.getCoordinates().length() > 0) {
                float[] coordinates = WKT.strToCoord(node.getCoordinates());
                updateStartNodeCoordinates(coordinates[0], coordinates[1], coordinates[2]);
            } else {
                updateStartNodeCoordinates(0.0f, 0.0f, 0.0f);
            }

        }
        // existing node
        else {
            // find correct node object stored in nodelist, so we can set spinner selection
            Node foundNode = null;
            for (Node tmpNode : nodeList) {
                if (tmpNode.getId().equals(node.getId())) {
                    foundNode = tmpNode;
                    break;
                }
            }

            startNodeSpinner.setSelection(nodeList.indexOf(foundNode));
            if (node.getCoordinates() != null && node.getCoordinates().length() > 0) {
                float[] coordinates = WKT.strToCoord(node.getCoordinates());
                updateStartNodeCoordinates(coordinates[0], coordinates[1], coordinates[2]);
            } else {
                updateStartNodeCoordinates(0.0f, 0.0f, 0.0f);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    if (controller != null) {
                        controller.onQrResult(barcode.displayValue);
                    }
                } else {
                    if (controller != null) {
                        controller.onQrResult("");
                    }
                }
            } else Log.e("tmp", "error");
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (controller != null) {
            controller.onResume();
        }
    }
}
