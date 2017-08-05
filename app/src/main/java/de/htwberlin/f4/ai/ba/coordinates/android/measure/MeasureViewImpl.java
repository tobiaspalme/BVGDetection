package de.htwberlin.f4.ai.ba.coordinates.android.measure;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurementType;
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



}
