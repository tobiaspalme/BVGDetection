package de.htwberlin.f4.ai.ba.coordinates.android.calibrate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.carol.bvg.R;

/**
 * Created by benni on 17.07.2017.
 */

public class CalibrateViewImpl extends Fragment implements CalibrateView {

    private CalibrateController controller;
    private TextView stepCountView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_coordinates_calibrate, container, false);

        stepCountView = (TextView) root.findViewById(R.id.coordinates_calibrate_tv_stepcount);

        Button btnStepSetup = (Button) root.findViewById(R.id.coordinates_calibrate_btn_stepsetup);
        btnStepSetup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onStartStepSetupClick();
                }
            }
        });

        Button btnStepSetupStop = (Button) root.findViewById(R.id.coordinates_calibrate_btn_stepstop);
        btnStepSetupStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onStopStepSetupClick();
                }
            }
        });

        return root;
    }

    @Override
    public void setController(CalibrateController controller) {
        this.controller = controller;
    }

    @Override
    public void updateStepCount(int stepCount) {
        stepCountView.setText(getString(R.string.step_count) + " " + String.valueOf(stepCount));
    }
}
