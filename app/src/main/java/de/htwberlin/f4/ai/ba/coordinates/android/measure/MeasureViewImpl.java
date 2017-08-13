package de.htwberlin.f4.ai.ba.coordinates.android.measure;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carol.bvg.R;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ba.coordinates.android.BaseActivity;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorData;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModelImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurementType;
import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerImplementation;


public class MeasureViewImpl extends BaseActivity implements MeasureView{

    private MeasureController controller;
    private TextView compassView;
    private TextView barometerView;
    private ImageView compassImageView;
    private TextView stepCounterView;
    private TextView distanceView;
    private TextView heightView;
    private TextView coordinatesView;
    private TextView startNodeCoordinatesView;
    private TextView targetNodeCoordinatesView;
    private TextView edgeDistanceView;

    private Button btnStart;
    private Button btnStop;
    private Button btnAdd;
    private Button btnTest;

    private ListView stepListView;

    private Spinner modeSpinner;
    private Spinner startNodeSpinner;
    private Spinner targetNodeSpinner;

    private ImageView startNodeImage;
    private ImageView targetNodeImage;
    private ImageView handycapImage;
    private ImageView edgeArrow;


    public MeasureViewImpl() {
        controller = new MeasureControllerImpl();
        controller.setView(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.fragment_coordinates_measure, contentFrameLayout);

        DatabaseHandler databaseHandler = new DatabaseHandlerImplementation(getContext());
        final List<Node> nodeList = databaseHandler.getAllNodes();
        List<String> nodeNames = new ArrayList<>();

        for (Node node : nodeList) {
            nodeNames.add(node.getId());
        }

        compassView = (TextView) findViewById(R.id.coordinates_measure_compass);
        compassImageView = (ImageView) findViewById(R.id.coordinates_measure_compass_iv);
        stepCounterView = (TextView) findViewById(R.id.coordinates_measure_stepvalue);
        distanceView = (TextView) findViewById(R.id.coordinates_measure_distancevalue);
        edgeDistanceView = (TextView) findViewById(R.id.coordinates_measure_edge_distance);

        //heightView = (TextView) root.findViewById(R.id.coordinates_measure_heightvalue);
        coordinatesView = (TextView) findViewById(R.id.coordinates_measure_coordinates);

        startNodeImage = (ImageView) findViewById(R.id.coordinates_measure_start_image);
        targetNodeImage = (ImageView) findViewById(R.id.coordinates_measure_target_image);
        edgeArrow = (ImageView) findViewById(R.id.coordinates_measure_arrow);

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

        startNodeCoordinatesView = (TextView) findViewById(R.id.coordinates_measure_start_coords);
        targetNodeCoordinatesView = (TextView) findViewById(R.id.coordinates_measure_target_coords);

        modeSpinner = (Spinner) findViewById(R.id.coordinates_measure_spinner);
        final List<IndoorMeasurementType> spinnerValues = new ArrayList<>();
        spinnerValues.add(IndoorMeasurementType.VARIANT_A);
        spinnerValues.add(IndoorMeasurementType.VARIANT_B);
        spinnerValues.add(IndoorMeasurementType.VARIANT_C);
        spinnerValues.add(IndoorMeasurementType.VARIANT_D);
        final ArrayAdapter<IndoorMeasurementType> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, spinnerValues);
        modeSpinner.setAdapter(spinnerAdapter);
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (controller != null) {
                    controller.onMeasurementTypeSelected(spinnerAdapter.getItem(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        startNodeSpinner = (Spinner) findViewById(R.id.coordinates_measure_startnode);
        final ArrayAdapter<String> startAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nodeNames);
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
                        Glide.with(view.getContext())
                                .load(startNode.getPicturePath())
                                .into(startNodeImage);

                        //startNodeImage.setImageURI(Uri.parse(startNode.getPicturePath()));
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
        final ArrayAdapter<String> targetAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nodeNames);
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
                        Glide.with(getApplicationContext())
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





        btnStart = (Button) findViewById(R.id.coordinates_measure_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onStartClicked();
                }
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                btnAdd.setEnabled(true);

                //stepListAdapter.clear();
            }
        });

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


                /*SensorDataModel dataModel = createTestDataLastStep();
                StepDirectionDetect stepDirectionDetect = new StepDirectionDetectImpl(0l);
                StepDirection direction = stepDirectionDetect.getLastStepDirection(dataModel);
                Log.d("tmp", "testdata direction: " + direction);*/
            }
        });

        btnTest = (Button) findViewById(R.id.coordinates_measure_test);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controller != null) {
                    controller.onTestClicked();
                }
            }
        });



        /*
        stepListView = (ListView) findViewById(R.id.coordinates_measure_steplist);
        stepListAdapter = new StepListAdapter(getContext(), new ArrayList<StepData>(), nodeList);
        stepListAdapter.setNodeSpinnerListener(new NodeSpinnerListener() {
            @Override
            public void onNodeSelected(Node node, StepData step) {
                if (controller != null) {
                    controller.onNodeSelected(node, step);
                }
            }
        });
        stepListView.setAdapter(stepListAdapter);
        */
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

        coordinatesView.setText(roundX + " / " + roundY + " / " + roundZ);
    }

    @Override
    public void updateStartNodeCoordinates(float x, float y, float z) {
        double roundX = Math.round(x * 100.0) / 100.0;
        double roundY = Math.round(y * 100.0) / 100.0;
        double roundZ = Math.round(z * 100.0) / 100.0;

        startNodeCoordinatesView.setText(roundX + " / " + roundY + " / " + roundZ);
    }

    @Override
    public void updateTargetNodeCoordinates(float x, float y, float z) {
        double roundX = Math.round(x * 100.0) / 100.0;
        double roundY = Math.round(y * 100.0) / 100.0;
        double roundZ = Math.round(z * 100.0) / 100.0;

        targetNodeCoordinatesView.setText(roundX + " / " + roundY + " / " + roundZ);
    }

    @Override
    public void updateEdge(Edge edge) {
        // handycap friendly
        if (edge.getAccessibly()) {
            // load handycap image
            Glide.with(getContext())
                    .load(R.drawable.barrierefrei)
                    .into(handycapImage);
        } else {
            // load handycap image
            Glide.with(getContext())
                    .load(R.drawable.nicht_barrierefrei)
                    .into(handycapImage);
        }
        // edge weight is in cm, but we use meters, so convert it
        float edgeDistance = edge.getWeight() / 100.0f;
        edgeDistanceView.setText(String.valueOf(edgeDistance));
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
    public void showAlert(String msg) {
        Toast toast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void loadEdgeDetailsView(Node startNode, Node targetNode) {
        loadEdgeDetails(startNode.getId(), targetNode.getId());
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

    private SensorDataModel createTestData() {
        SensorDataModel dataModel = new SensorDataModelImpl();

        // step right -> step forward

        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953933747l,0.07f,0.0f,0.089999996f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953933863l,0.19999999f,-0.22f,-0.16f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953933943l,0.0f,-0.24f,0.39999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953934067l,-0.03f,-0.13f,-0.03f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953934145l,-0.26f,-0.29f,-0.16f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953934269l,0.65999997f,-0.089999996f,-0.32f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953934432l,0.81f,-0.049999997f,0.01f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953934562l,1.13f,0.39999998f,-0.64f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953934671l,0.87f,0.01f,-0.049999997f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953934759l,1.3f,-0.28f,-0.91999996f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953934877l,2.28f,-0.62f,-1.9499999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953934998l,3.72f,-0.44f,-0.71f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953935155l,0.9f,-0.29f,1.14f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953935155l,0.9f,-0.29f,1.14f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953935411l,-2.1699998f,-0.52f,-2.02f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953935477l,-2.03f,-0.88f,-1.27f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953935600l,-1.52f,-0.08f,0.42999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953935768l,1.26f,0.45f,0.84f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953935881l,0.01f,0.16f,-0.41f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953935970l,0.02f,-0.17f,-0.84f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953936083l,-0.77f,-0.099999994f,-0.21f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953936206l,0.25f,0.28f,0.03f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953936290l,-0.37f,0.17f,-0.049999997f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953936407l,0.0f,0.59999996f,0.31f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953936532l,0.69f,0.85999995f,0.17999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953936690l,1.3199999f,1.0699999f,-1.12f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953936811l,0.77f,1.63f,-1.9599999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953936936l,-0.13f,2.9099998f,0.07f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953937027l,0.90999997f,1.3f,4.2799997f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953937135l,-1.36f,-1.8f,0.61f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953937214l,0.089999996f,-4.33f,3.6899998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953937345l,-0.089999996f,-2.71f,-2.77f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953937495l,0.42999998f,-2.0f,0.42999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953937629l,-0.14f,-1.18f,2.85f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953937747l,-0.25f,-1.17f,-0.25f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953937826l,-0.39f,-1.02f,-2.4299998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953937939l,0.38f,-0.74f,0.0f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1501953938061l,0.74f,-0.29f,-0.35f));

        return dataModel;
    }


    private SensorDataModel creaTestDataLeftRight() {
        SensorDataModel dataModel = new SensorDataModelImpl();

        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019507669l,0.14999999f,0.14999999f,-0.51f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019507759l,0.12f,0.0f,0.049999997f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019507831l,-0.29999998f,0.089999996f,-0.26999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019507869l,0.14f,0.08f,0.29999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019507956l,-0.42999998f,-0.08f,0.01f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019508072l,0.22f,-0.049999997f,0.7f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019508153l,0.089999996f,-0.099999994f,-0.049999997f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019508235l,-0.22999999f,0.099999994f,-0.13f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019508271l,-0.35999998f,-0.21f,0.22999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019508358l,-1.1f,0.14f,-1.41f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019508477l,-1.51f,0.02f,0.51f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019508560l,-1.49f,0.26999998f,0.06f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019508646l,-1.0699999f,0.45999998f,0.21f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019508681l,-2.02f,0.25f,-0.62f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019508760l,-1.7199999f,0.12f,-0.17999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019508883l,-1.68f,0.089999996f,-0.93f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019508970l,-3.76f,-0.48999998f,-0.91999996f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019509083l,-2.87f,-0.94f,1.86f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019509161l,1.49f,0.48f,3.25f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019509243l,0.37f,-0.39999998f,2.01f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019509287l,4.48f,0.39999998f,2.78f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019509374l,0.72999996f,-0.25f,-0.04f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019509374l,0.72999996f,-0.25f,-0.04f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019509584l,2.44f,-0.32f,-1.9599999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019509611l,2.78f,-0.21f,-0.39f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019509689l,0.25f,0.17999999f,-0.7f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019509808l,0.34f,0.19999999f,0.51f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019509892l,0.53999996f,0.5f,-1.27f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019510019l,-0.57f,-0.17999999f,-0.37f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019510091l,0.28f,0.14999999f,-0.01f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019510174l,0.41f,0.04f,0.06f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019510221l,-0.19999999f,0.25f,-0.47f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019510291l,-0.089999996f,0.26999998f,0.17f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019510423l,-0.58f,0.35f,0.26999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019510499l,-0.28f,0.28f,0.71999997f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019510576l,-1.1899999f,0.049999997f,-0.59999996f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019510624l,-0.7f,0.19f,0.01f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019510746l,-0.77f,0.13f,0.74f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019510819l,-0.14999999f,-0.29f,-0.19999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019510900l,-0.099999994f,-0.22999999f,-0.47f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019510939l,0.04f,-0.19f,-0.29f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019511022l,0.32f,-0.19999999f,-0.14999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019511142l,-0.41f,-0.87f,-0.16f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019511222l,-1.1999999f,-0.48f,-0.39f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019511316l,-0.87f,-0.39f,1.23f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019511345l,-1.42f,-0.28f,-0.34f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019511422l,-0.81f,-0.11f,-0.03f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019511551l,-1.09f,-0.08f,0.24f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019511627l,-0.57f,0.0f,-0.26999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019511707l,-0.68f,-0.22f,-0.71f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019511749l,-0.02f,-0.39f,0.32f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019511830l,0.68f,-0.06f,0.72999996f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019511950l,0.52f,0.08f,-0.12f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019512031l,0.25f,0.089999996f,1.18f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019512107l,0.19999999f,-0.14f,-1.2099999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019512155l,0.95f,-0.24f,0.32f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019512273l,1.1f,0.32f,-0.78f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019512355l,0.07f,0.06f,0.25f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019512435l,0.42999998f,-0.08f,0.39999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019512477l,-0.59f,0.06f,-0.96f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019512559l,0.26f,0.53999996f,0.32f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019512559l,0.26f,0.53999996f,0.32f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019512838l,-0.29f,0.62f,0.13f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019512876l,0.02f,0.81f,-0.26999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019512956l,-0.66999996f,0.96f,-0.76f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019513075l,-0.68f,0.53f,-0.93f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019513158l,0.42999998f,0.58f,-0.5f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019513247l,-0.17999999f,0.51f,0.81f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019513290l,0.34f,0.84f,-0.24f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019513360l,0.31f,0.71f,-0.14f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019513485l,0.34f,0.63f,-0.52f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019513562l,-0.11f,0.099999994f,0.42f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019513653l,0.75f,0.77f,-0.01f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019513697l,0.78999996f,1.24f,0.25f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019513811l,0.11f,0.42999998f,-1.16f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019513886l,1.36f,0.64f,-0.02f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019514023l,1.9499999f,0.71999997f,-1.8199999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019514091l,2.23f,0.55f,-0.51f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019514169l,2.61f,0.42f,-0.52f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019514220l,2.6299999f,-0.14f,-0.099999994f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019514371l,2.33f,0.29999998f,3.55f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019514416l,-2.71f,-2.06f,2.35f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019514495l,-3.26f,1.55f,1.04f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019514613l,-3.73f,1.3f,-0.65999997f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019514698l,-2.83f,2.0799999f,-1.3299999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019514776l,-5.06f,0.69f,-1.9f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019514816l,-2.18f,-0.16f,2.9199998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019514895l,-2.78f,0.11f,-0.66999996f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019515026l,-1.09f,-0.06f,1.05f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019515099l,-1.24f,1.03f,-2.49f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019515141l,-1.24f,0.82f,0.83f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019515228l,-0.59f,0.03f,0.29999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019515341l,0.45999998f,0.89f,-1.53f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019515427l,-1.01f,-0.049999997f,0.29999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019515514l,-0.16f,0.38f,-0.22999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502019515560l,-0.08f,0.44f,0.31f));

        return dataModel;
    }

    private SensorDataModel createTestDataLastStep() {

        SensorDataModel dataModel = new SensorDataModelImpl();
        // right step, detected as left step because of missing lowpeak
        // lowpeaks occurs after stepdetection, so its missing
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032393798l, 0.34f, -0.55f, -0.06f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032393826l, -0.07f, -0.52f, 0.02f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032393834l, -0.45999998f, -0.55f, -0.17f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032393855l, 0.01f, -0.52f, -0.51f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032393875l, 0.79999995f, -0.28f, -0.31f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032393892l, 0.89f, -0.24f, 0.22999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032393917l, 0.19f, -0.26999998f, 0.44f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032393936l, 0.049999997f, -0.19999999f, 0.049999997f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032393959l, 0.53999996f, -0.099999994f, -0.35999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032393977l, 0.69f, -0.04f, -0.38f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032393997l, 0.62f, -0.11f, -0.12f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394018l, 0.42999998f, -0.19f, 0.38f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394043l, 0.08f, -0.25f, 0.78f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394059l, 0.04f, -0.19f, 0.71999997f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394077l, 0.39999998f, -0.08f, 0.51f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394099l, 0.59f, -0.08f, 0.22999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394118l, 0.26999998f, -0.07f, -0.29999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394142l, -0.01f, -0.089999996f, -0.48f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394159l, -0.01f, -0.13f, -0.04f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394182l, -0.04f, -0.089999996f, 0.45999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394202l, -0.03f, -0.08f, 0.51f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394202l, 0.049999997f, -0.14999999f, 0.089999996f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394224l, 0.099999994f, -0.22f, -0.37f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394242l, 0.08f, -0.24f, -0.22999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394260l, 0.13f, -0.28f, 0.21f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394281l, 0.16f, -0.28f, 0.37f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394303l, 0.22f, -0.22999999f, 0.28f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394323l, 0.28f, -0.19999999f, 0.02f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394341l, 0.26f, -0.17f, -0.26f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394360l, 0.29f, -0.12f, -0.31f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394381l, 0.32999998f, -0.089999996f, -0.11f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394407l, 0.22f, -0.049999997f, 0.02f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394423l, 0.14999999f, -0.08f, -0.01f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394442l, 0.25f, -0.17f, 0.08f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394461l, 0.31f, -0.16f, 0.29f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394484l, 0.32f, -0.17999999f, 0.39999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394507l, 0.22f, -0.17999999f, 0.13f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394523l, 0.099999994f, -0.16f, -0.22999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394541l, 0.099999994f, -0.089999996f, -0.29999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394564l, 0.25f, -0.03f, -0.32f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394590l, 0.35f, -0.11f, -0.02f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394606l, 0.39f, -0.19999999f, 0.22999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394623l, 0.25f, -0.19999999f, 0.14f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394643l, 0.01f, -0.24f, 0.11f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394666l, -0.12f, -0.19999999f, 0.049999997f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394686l, 0.13f, -0.21f, -0.089999996f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394704l, 0.42999998f, -0.19f, -0.089999996f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394721l, 0.34f, -0.17999999f, 0.28f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394741l, -0.08f, -0.12f, 0.35999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394762l, -0.049999997f, -0.089999996f, -0.02f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394782l, 0.28f, -0.089999996f, -0.34f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394804l, 0.35f, -0.07f, -0.11f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394822l, 0.32f, -0.089999996f, 0.14999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394842l, 0.22f, -0.12f, 0.03f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394863l, 0.12f, -0.12f, -0.04f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394885l, 0.13f, -0.099999994f, -0.04f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394902l, 0.13f, -0.08f, -0.099999994f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394922l, 0.26999998f, -0.089999996f, -0.08f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394943l, 0.26999998f, -0.11f, 0.13f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394963l, 0.16f, -0.11f, 0.32f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032394985l, 0.02f, -0.14f, 0.39999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395003l, -0.08f, -0.13f, 0.19f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395025l, 0.04f, -0.08f, 0.06f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395043l, 0.29f, -0.06f, -0.11f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395064l, 0.26999998f, -0.07f, -0.19999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395085l, 0.22f, -0.19999999f, -0.11f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395103l, 0.099999994f, -0.11f, 0.0f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395125l, 0.04f, -0.099999994f, 0.06f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395146l, 0.12f, -0.14999999f, -0.13f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395168l, 0.14999999f, -0.17f, -0.17f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395185l, 0.06f, -0.21f, 0.17f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395206l, -0.13f, -0.19999999f, 0.38f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395226l, -0.14f, -0.099999994f, 0.07f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395246l, 0.07f, -0.07f, -0.22999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395266l, 0.22999999f, -0.01f, -0.17999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395285l, 0.24f, -0.02f, 0.06f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395306l, 0.08f, -0.01f, 0.22999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395328l, 0.01f, -0.02f, 0.17f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395350l, 0.07f, 0.01f, -0.049999997f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395371l, 0.099999994f, -0.03f, -0.089999996f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395387l, 0.08f, -0.049999997f, -0.11f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395407l, 0.11f, -0.049999997f, 0.089999996f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395436l, 0.17999999f, -0.04f, 0.42999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395452l, 0.19999999f, -0.01f, 0.42999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395471l, 0.14999999f, -0.06f, 0.11f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395488l, -0.01f, -0.08f, -0.22f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395508l, -0.01f, -0.08f, -0.17999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395528l, 0.02f, -0.08f, 0.06f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395551l, 0.06f, -0.089999996f, 0.19f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395570l, 0.21f, -0.089999996f, 0.049999997f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395614l, 0.14f, -0.089999996f, -0.06f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395614l, -0.03f, -0.08f, -0.02f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395634l, 0.02f, -0.08f, 0.03f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395661l, 0.11f, -0.02f, -0.04f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395674l, 0.19f, -0.04f, -0.089999996f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395691l, 0.19f, -0.08f, -0.06f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395714l, 0.25f, -0.07f, -0.099999994f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395732l, 0.24f, -0.03f, -0.089999996f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395751l, 0.04f, -0.049999997f, 0.11f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395770l, -0.17f, -0.11f, 0.34f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395791l, -0.12f, -0.17999999f, 0.39999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395815l, 0.06f, -0.17999999f, 0.24f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395833l, 0.26f, -0.11f, 0.02f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395851l, 0.38f, -0.08f, -0.06f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395873l, 0.21f, -0.049999997f, -0.01f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395892l, 0.01f, -0.03f, -0.099999994f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395916l, 0.13f, -0.01f, -0.42f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395934l, 0.37f, 0.02f, -0.47f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395953l, 0.39f, -0.02f, 0.04f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395975l, 0.099999994f, -0.13f, 0.45999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032395999l, -0.22f, -0.17f, 0.44f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396018l, -0.099999994f, -0.14f, 0.24f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396038l, 0.22f, -0.06f, -0.03f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396054l, 0.32999998f, -0.01f, -0.29f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396076l, 0.17f, -0.01f, -0.19f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396099l, 0.02f, -0.03f, 0.19999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396115l, 0.12f, -0.07f, 0.25f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396134l, 0.19f, -0.08f, 0.049999997f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396154l, 0.11f, -0.04f, 0.03f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396178l, 0.07f, -0.02f, 0.06f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396194l, 0.12f, -0.04f, 0.049999997f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396213l, 0.17999999f, -0.08f, -0.03f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396235l, 0.22f, -0.11f, -0.19f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396256l, 0.21f, -0.14f, -0.14999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396279l, 0.04f, -0.16f, 0.22999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396295l, -0.14999999f, -0.099999994f, 0.29999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396318l, -0.08f, -0.04f, -0.32f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396336l, 0.089999996f, -0.02f, -0.76f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396363l, 0.17999999f, -0.04f, -0.24f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396384l, 0.17f, -0.11f, 0.7f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396400l, 0.03f, -0.14f, 0.82f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396419l, 0.0f, -0.13f, 0.089999996f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396420l, 0.19999999f, -0.07f, -0.44f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396438l, 0.26999998f, 0.01f, -0.13f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396461l, 0.14999999f, 0.02f, 0.19f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396481l, 0.08f, -0.03f, -0.06f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396498l, 0.13f, -0.07f, -0.42999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396518l, 0.099999994f, -0.08f, -0.25f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396539l, -0.02f, -0.14f, 0.32999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396564l, -0.06f, -0.21f, 0.51f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396583l, 0.06f, -0.21f, 0.19999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396599l, 0.22999999f, -0.12f, -0.099999994f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396619l, 0.19999999f, -0.03f, -0.12f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396639l, 0.12f, 0.03f, -0.22999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396662l, 0.16f, 0.02f, -0.32999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396680l, 0.25f, -0.04f, -0.13f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396699l, 0.21f, -0.06f, 0.31f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396720l, 0.049999997f, -0.07f, 0.47f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396744l, -0.089999996f, -0.03f, 0.19f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396762l, 0.099999994f, -0.04f, -0.06f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396782l, 0.26999998f, -0.049999997f, -0.16f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396804l, 0.08f, -0.02f, -0.03f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396825l, -0.17f, -0.02f, 0.089999996f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396843l, -0.089999996f, -0.08f, -0.04f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396863l, 0.08f, -0.06f, 0.04f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396880l, 0.049999997f, 0.02f, 0.04f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396901l, -0.06f, 0.07f, -0.34f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396926l, -0.11f, 0.04f, -0.42f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396944l, 0.07f, -0.08f, 0.32f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396961l, 0.16f, -0.099999994f, 0.81f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032396981l, 0.07f, -0.099999994f, 0.55f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397002l, -0.089999996f, -0.06f, -0.08f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397026l, 0.02f, -0.03f, -0.37f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397044l, 0.29f, -0.02f, -0.11f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397062l, 0.29f, 0.0f, 0.04f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397085l, 0.16f, 0.01f, -0.17999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397109l, 0.12f, -0.02f, -0.19f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397126l, 0.12f, -0.04f, 0.049999997f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397144l, 0.12f, -0.13f, 0.07f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397175l, 0.01f, -0.12f, 0.13f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397184l, -0.089999996f, -0.07f, 0.28f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397208l, -0.07f, -0.01f, 0.16f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397227l, 0.02f, -0.02f, -0.17f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397245l, 0.17f, 0.0f, -0.35f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397265l, 0.29f, -0.02f, -0.13f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397285l, 0.16f, -0.02f, 0.16f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397308l, 0.08f, -0.03f, 0.42999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397328l, 0.17f, -0.06f, 0.35999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397345l, 0.17999999f, -0.08f, 0.07f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397370l, 0.049999997f, -0.11f, 0.07f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397392l, 0.01f, -0.13f, 0.28f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397409l, 0.17999999f, -0.04f, 0.11f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397428l, 0.29999998f, -0.02f, -0.29f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397448l, 0.22999999f, 0.0f, -0.21f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397474l, 0.17f, 0.0f, 0.04f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397491l, 0.02f, -0.01f, 0.08f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397509l, 0.08f, -0.06f, -0.12f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397528l, 0.19999999f, -0.14f, -0.26999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397548l, 0.22999999f, -0.22999999f, -0.16f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397572l, 0.25f, -0.25f, 0.22999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397596l, 0.099999994f, -0.21f, 0.39999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397614l, -0.19f, -0.14f, 0.07f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397631l, -0.21f, -0.089999996f, -0.42f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397655l, 0.0f, -0.01f, -0.59f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397675l, 0.14999999f, -0.01f, -0.16f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397690l, 0.12f, -0.049999997f, 0.32999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397709l, -0.04f, -0.049999997f, 0.44f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397728l, -0.17f, -0.049999997f, 0.19999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397748l, -0.11f, -0.02f, -0.12f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397770l, 0.0f, 0.02f, -0.049999997f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397788l, -0.049999997f, 0.07f, 0.07f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397809l, -0.11f, 0.07f, -0.16f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397830l, -0.089999996f, 0.03f, -0.39f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397854l, -0.14999999f, -0.01f, -0.14999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397872l, -0.14999999f, -0.03f, 0.049999997f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397889l, -0.14999999f, -0.04f, 0.17f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397910l, -0.12f, -0.06f, 0.37f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397932l, -0.08f, -0.04f, 0.41f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397954l, -0.08f, 0.01f, 0.19999999f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397972l, -0.04f, 0.049999997f, -0.17f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032397991l, 0.01f, 0.08f, -0.19f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398013l, 0.07f, 0.06f, 0.28f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398038l, 0.21f, 0.0f, 0.52f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398056l, 0.22f, 0.01f, 0.13f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398073l, 0.0f, 0.089999996f, -0.38f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398092l, -0.12f, 0.14f, -0.52f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398115l, -0.08f, 0.089999996f, -0.35999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398136l, -0.01f, -0.03f, -0.03f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398156l, 0.07f, -0.07f, 0.31f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398173l, 0.01f, -0.049999997f, 0.48f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398193l, -0.19999999f, -0.04f, 0.32999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398214l, -0.19f, -0.04f, 0.06f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398234l, 0.089999996f, -0.04f, 0.03f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398255l, 0.29999998f, -0.03f, 0.17f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398277l, 0.26f, -0.01f, 0.11f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398300l, 0.24f, -0.02f, -0.089999996f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398317l, 0.34f, -0.06f, -0.28f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398336l, 0.35999998f, -0.14f, -0.26999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398358l, 0.29f, -0.21f, 0.13f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398376l, 0.34f, -0.19f, 0.62f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398399l, 0.59f, -0.14999999f, 0.65f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398417l, 0.84f, -0.17f, 0.31f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398436l, 0.91999996f, -0.19f, 0.049999997f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398457l, 0.90999997f, -0.22f, -0.17f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398481l, 0.81f, -0.24f, -0.35f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398499l, 0.62f, -0.22f, -0.39999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398515l, 0.7f, -0.21f, -0.47f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398536l, 0.95f, -0.17999999f, -0.44f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398560l, 1.03f, -0.13f, -0.22f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398581l, 0.87f, -0.11f, -0.08f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398599l, 0.65999997f, -0.04f, -0.02f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398617l, 0.78999996f, 0.01f, 0.11f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398617l, 1.11f, 0.03f, 0.22f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398641l, 1.05f, 0.06f, 0.16f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398662l, 0.69f, 0.049999997f, 0.049999997f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398682l, 0.45f, -0.049999997f, 0.08f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398699l, 0.41f, -0.12f, 0.07f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398720l, 0.53999996f, -0.08f, -0.19f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398741l, 1.0699999f, -0.02f, -0.42999998f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398765l, 1.5899999f, -0.11f, -0.48f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398783l, 1.56f, -0.19999999f, -0.53f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398799l, 1.35f, -0.26f, -0.47f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398821l, 1.52f, -0.32f, -0.13f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398844l, 1.87f, -0.39f, -0.01f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398865l, 1.86f, -0.47f, -0.28f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398882l, 1.88f, -0.59f, -0.71f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398901l, 1.89f, -0.65999997f, -0.77f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398922l, 1.7199999f, -0.66999996f, -0.5f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398946l, 1.77f, -0.65f, -0.47f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398964l, 2.04f, -0.64f, -0.81f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032398981l, 2.4099998f, -0.7f, -1.15f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032399002l, 2.75f, -0.90999997f, -1.06f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032399023l, 2.71f, -1.1899999f, -0.57f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032399047l, 2.33f, -1.38f, -0.049999997f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032399064l, 2.24f, -1.43f, 0.53999996f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032399081l, 1.9599999f, -1.28f, 0.76f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032399102l, 1.54f, -1.11f, 0.63f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032399125l, 1.5799999f, -0.96999997f, 0.47f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032399146l, 1.5699999f, -0.68f, 0.53999996f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032399164l, 0.88f, -0.48f, 1.15f));
        dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032399182l, -0.16f, -0.48f, 2.35f));

        // if we add this, the right step is properly detected
        //dataModel.insertData(new SensorData(SensorType.ACCELEROMETER_LINEAR,1502032399183l, -3.16f, -0.48f, 2.35f));

        return dataModel;
    }
}
