package de.htwberlin.f4.ai.ba.coordinates.android.measure;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.carol.bvg.R;

import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorData;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModelImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurementType;
import de.htwberlin.f4.ai.ba.coordinates.measurement.StepDirection;
import de.htwberlin.f4.ai.ba.coordinates.measurement.StepDirectionDetect;
import de.htwberlin.f4.ai.ba.coordinates.measurement.StepDirectionDetectImpl;
import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerImplementation;


public class MeasureViewImpl extends Fragment implements MeasureView{

    private MeasureController controller;
    private TextView compassView;
    private TextView barometerView;
    private ImageView compassImageView;
    private TextView stepCounterView;
    private TextView distanceView;
    private TextView heightView;
    private TextView coordinatesView;

    private Button btnStart;
    private Button btnStop;
    private Button btnAdd;

    private ListView stepListView;
    private StepListAdapter stepListAdapter;

    private Spinner modeSpinner;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_coordinates_measure, container, false);

        compassView = (TextView) root.findViewById(R.id.coordinates_measure_compass);
        compassImageView = (ImageView) root.findViewById(R.id.coordinates_measure_compass_iv);
        stepCounterView = (TextView) root.findViewById(R.id.coordinates_measure_stepvalue);
        distanceView = (TextView) root.findViewById(R.id.coordinates_measure_distancevalue);

        //heightView = (TextView) root.findViewById(R.id.coordinates_measure_heightvalue);
        coordinatesView = (TextView) root.findViewById(R.id.coordinates_measure_coordinates);

        modeSpinner = (Spinner) root.findViewById(R.id.coordinates_measure_spinner);
        final List<IndoorMeasurementType> spinnerValues = new ArrayList<>();
        spinnerValues.add(IndoorMeasurementType.VARIANT_A);
        spinnerValues.add(IndoorMeasurementType.VARIANT_B);
        final ArrayAdapter<IndoorMeasurementType> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerValues);
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

        btnStart = (Button) root.findViewById(R.id.coordinates_measure_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onStartClicked();
                }
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                btnAdd.setEnabled(true);

                stepListAdapter.clear();
            }
        });

        btnStop = (Button) root.findViewById(R.id.coordinates_measure_stop);
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

        btnAdd = (Button) root.findViewById(R.id.coordinates_measure_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onAddClicked();
                }

                /*
                SensorDataModel dataModel = createTestData();
                StepDirectionDetect stepDirectionDetect = new StepDirectionDetectImpl(0l);
                StepDirection direction = stepDirectionDetect.getLastStepDirection(dataModel);
                Log.d("tmp", "testdata direction: " + direction);*/
            }
        });


        DatabaseHandler databaseHandler = new DatabaseHandlerImplementation(getContext());
        List<Node> nodeList = databaseHandler.getAllNodes();

        stepListView = (ListView) root.findViewById(R.id.coordinates_measure_steplist);
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

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (controller != null) {
            controller.onPause();
        }
    }

    @Override
    public void setController(MeasureController controller) {
        this.controller = controller;
    }


    @Override
    public void updateAzimuth(float azimuth) {
        compassView.setText(String.valueOf(azimuth));
        compassImageView.setRotation(-azimuth);
    }

    @Override
    public void updateStepCount(int stepCount) {
        stepCounterView.setText(String.valueOf(stepCount));
    }

    @Override
    public void updateDistance(float distance) {
        distanceView.setText(String.valueOf(distance));
    }

    @Override
    public void updateCoordinates(float x, float y, float z) {
        coordinatesView.setText(x + " / " + y + " / " + z);
    }


    @Override
    public void insertStep(StepData stepData) {
        stepListAdapter.add(stepData);
        //stepListAdapter.notifyDataSetChanged();

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
}
