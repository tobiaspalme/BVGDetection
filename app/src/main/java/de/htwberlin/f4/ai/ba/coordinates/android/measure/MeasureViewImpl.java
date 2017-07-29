package de.htwberlin.f4.ai.ba.coordinates.android.measure;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.carol.bvg.R;

import org.w3c.dom.Text;


public class MeasureViewImpl extends Fragment implements MeasureView{

    private MeasureController controller;
    private TextView compassView;
    private TextView barometerView;
    private ImageView compassImageView;
    private TextView stepCounterView;
    private TextView distanceView;
    private TextView heightView;
    private TextView coordinatesView;

    private ExpandableListView stepListView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_coordinates_measure, container, false);

        compassView = (TextView) root.findViewById(R.id.coordinates_measure_compass);
        compassImageView = (ImageView) root.findViewById(R.id.coordinates_measure_compass_iv);
        stepCounterView = (TextView) root.findViewById(R.id.coordinates_measure_stepvalue);
        distanceView = (TextView) root.findViewById(R.id.coordinates_measure_distancevalue);
        barometerView = (TextView) root.findViewById(R.id.coordinates_measure_pressurevalue);
        heightView = (TextView) root.findViewById(R.id.coordinates_measure_heightvalue);
        coordinatesView = (TextView) root.findViewById(R.id.coordinates_measure_coordinates);

        Button btnStart = (Button) root.findViewById(R.id.coordinates_measure_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onStartClicked();
                }
            }
        });

        Button btnStop = (Button) root.findViewById(R.id.coordinates_measure_stop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onStopClicked();
                }
            }
        });

        Button btnAdd = (Button) root.findViewById(R.id.coordinates_measure_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onAddClicked();
                }
            }
        });

        stepListView = (ExpandableListView) root.findViewById(R.id.coordinates_measure_steplist);


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
    public void updatePressure(float pressure) {
        barometerView.setText(String.valueOf(pressure));
    }

    @Override
    public void updateAzimuth(int azimuth) {
        compassView.setText(String.valueOf(azimuth));
        compassImageView.setRotation(-azimuth);
    }

    @Override
    public void updateStepCount(int stepCount) {
        stepCounterView. setText(String.valueOf(stepCount));
    }

    @Override
    public void updateDistance(float distance) {
        distanceView.setText(String.valueOf(distance));
    }

    @Override
    public void updateCoordinates(float[] coordinates) {
        coordinatesView.setText(coordinates[0] + " / " + coordinates[1] + " / " + coordinates[2]);
    }

    @Override
    public void updateHeight(float height) {
        heightView.setText(String.valueOf(height));
    }

    @Override
    public void insertStep() {

    }


}
