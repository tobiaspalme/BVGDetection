package de.htwberlin.f4.ai.ba.coordinates.android.calibrate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.carol.bvg.R;

import org.w3c.dom.Text;

/**
 * Created by benni on 17.07.2017.
 */

public class CalibrateViewImpl extends Fragment implements CalibrateView {

    private CalibrateController controller;
    private TextView stepCountView;
    private TextView stepDistanceView;
    private TextView stepPeriodView;
    private ImageView compassView;
    private TextView azimuthView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_coordinates_calibrate_step1, container, false);



        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadStepOneView();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (controller != null) {
            controller.onPause();
        }
    }

    @Override
    public void setController(CalibrateController controller) {
        this.controller = controller;
    }

    @Override
    public void updateStepCount(int stepCount) {
        stepCountView.setText(String.valueOf(stepCount));
    }

    @Override
    public void loadCalibrateStep(int setupStep) {
        switch (setupStep) {
            case 1:
                loadStepOneView();
                break;
            case 2:
                loadStepTwoView();
                break;
            case 3:
                loadStepThreeView();
                break;
            default:
                break;
        }
    }

    @Override
    public void updateAverageStepdistance(float distance) {
        stepDistanceView.setText(getString(R.string.step_setup_average_stepdistance) + " " + distance);
    }

    @Override
    public void updateAverageStepperiod(int period) {
        stepPeriodView.setText(getString(R.string.step_setup_average_stepperiod) + " " + period);
    }

    @Override
    public void updateAzimuth(int azimuth) {
        azimuthView.setText(azimuth + "°");
        compassView.setRotation(-azimuth);
    }

    private void loadStepOneView() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_coordinates_calibrate_step1, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(view);


        stepCountView = (TextView) view.findViewById(R.id.coordinates_calibrate_tv_step);

        final Button btnStepInc = (Button) view.findViewById(R.id.coordinates_calibrate_btn_stepinc);
        btnStepInc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onStepIncreaseClicked();
                }
            }
        });


        final Button btnStepDec = (Button) view.findViewById(R.id.coordinates_calibrate_btn_stepdec);
        btnStepDec.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onStepDecreaseClicked();
                }
            }
        });

        final Button btnNext = (Button) view.findViewById(R.id.coordinates_calibrate_next_1);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controller != null) {
                    controller.onNextClicked(1);
                }
            }
        });

        Button btnStepSetupStart = (Button) view.findViewById(R.id.coordinates_calibrate_btn_stepstart);
        btnStepSetupStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onStartStepSetupClick();
                }
                btnStepInc.setEnabled(false);
                btnStepDec.setEnabled(false);
                btnNext.setEnabled(true);
            }
        });

        Button btnStepSetupStop = (Button) view.findViewById(R.id.coordinates_calibrate_btn_stepstop);
        btnStepSetupStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onStopStepSetupClick();
                }
                btnStepInc.setEnabled(true);
                btnStepDec.setEnabled(true);
            }
        });







    }

    private void loadStepTwoView() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_coordinates_calibrate_step2, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(view);

        Button btnNext = (Button) view.findViewById(R.id.coordinates_calibrate_next_2);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controller != null) {
                    controller.onNextClicked(2);
                }
            }
        });

        Button btnBack = (Button) view.findViewById(R.id.coordinates_calibrate_back_2);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controller != null) {
                    controller.onBackClicked(2);
                }
            }
        });

        final EditText etDistance = (EditText) view.findViewById(R.id.coordinates_calibrate_distance);
        etDistance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (controller != null) {
                    // check if there is any text, !.equals() doesn't seem to work
                    if (etDistance.getText().toString().length() > 0) {
                        controller.onDistanceChange(Float.valueOf(etDistance.getText().toString()));
                    }
                }
            }
        });

        stepDistanceView = (TextView) view.findViewById(R.id.coordinates_calibrate_avgstepdistance);
        stepPeriodView = (TextView) view.findViewById(R.id.coordinates_calibrate_avgstepperiod);
    }

    private void loadStepThreeView() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_coordinates_calibrate_step3, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(view);

        Button btnNext = (Button) view.findViewById(R.id.coordinates_calibrate_next_3);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controller != null) {
                    controller.onNextClicked(3);
                }
            }
        });

        Button btnBack = (Button) view.findViewById(R.id.coordinates_calibrate_back_3);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controller != null) {
                    controller.onBackClicked(3);
                }
            }
        });

        compassView = (ImageView) view.findViewById(R.id.coordinates_calibrate_compass_iv);
        azimuthView = (TextView) view.findViewById(R.id.coordinates_calibrate_compass_value);
    }
}
