package de.htwberlin.f4.ai.ba.coordinates.android.measure;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.carol.bvg.R;




public class MeasureViewImpl extends Fragment implements MeasureView{

    private MeasureController controller;
    private TextView compassView;
    private TextView compassView2;
    private TextView barometerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_coordinates_measure, container, false);

        compassView = (TextView) root.findViewById(R.id.coordinates_measure_compass);
        compassView2 = (TextView) root.findViewById(R.id.coordinates_measure_compass2);
        barometerView = (TextView) root.findViewById(R.id.coordinates_measure_pressure);

        Button btnStart = (Button) root.findViewById(R.id.coordinates_measure_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onStartClicked();
                }
            }
        });



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
    public void updatePressure(Float pressure) {
        barometerView.setText(getString(R.string.measure_pressure) + " " + String.valueOf(pressure));
    }

    @Override
    public void updateAzimuth(Integer azimuth) {
        compassView.setText(String.valueOf(azimuth));
    }

    @Override
    public void updateAzimuth2(Integer azimuth) {
        compassView2.setText(String.valueOf(azimuth));
    }


}
