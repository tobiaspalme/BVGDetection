package de.htwberlin.f4.ai.ma.android.calibrate;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.carol.bvg.R;

import de.htwberlin.f4.ai.ma.android.BaseActivity;

/**
 * Created by benni on 17.07.2017.
 */

public class CalibrateViewImpl extends BaseActivity implements CalibrateView {

    private CalibrateController controller;
    private TextView stepCountView;
    private TextView stepDistanceView;
    private TextView stepPeriodView;
    private ImageView compassView;
    private TextView azimuthView;

    private Button btnStepIncStepOne;
    private Button btnStepDecStepOne;
    private Button btnNextStepOne;
    private Button btnStartStepOne;
    private Button btnStopStepOne;

    private Button btnNextStepTwo;
    private Button btnBackStepTwo;


    public CalibrateViewImpl() {
        controller = new CalibrateControllerImpl();
        controller.setView(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Kalibrieren");
        loadStepOneView();
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_calibrate_step1, container, false);

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadStepOneView();
    }*/

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

    @Override
    public Context getContext() {
        return this;
    }

    private void loadStepOneView() {
        //LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        //View view = inflater.inflate(R.layout.activity_calibrate_step1, null);

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        contentFrameLayout.removeAllViews();

        View view = getLayoutInflater().inflate(R.layout.activity_calibrate_step1, contentFrameLayout);


        //ViewGroup rootView = (ViewGroup) getView();
        //rootView.removeAllViews();
        //rootView.addView(view);


        stepCountView = (TextView) view.findViewById(R.id.coordinates_calibrate_tv_step);

        btnStepIncStepOne = (Button) view.findViewById(R.id.coordinates_calibrate_btn_stepinc);
        btnStepIncStepOne.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onStepIncreaseClicked();
                }
            }
        });
        btnStepIncStepOne.setEnabled(false);


        btnStepDecStepOne = (Button) view.findViewById(R.id.coordinates_calibrate_btn_stepdec);
        btnStepDecStepOne.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onStepDecreaseClicked();
                }
            }
        });
        btnStepDecStepOne.setEnabled(false);

        btnNextStepOne = (Button) view.findViewById(R.id.coordinates_calibrate_next_1);
        btnNextStepOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controller != null) {
                    controller.onNextClicked(1);
                }
            }
        });
        btnNextStepOne.setEnabled(false);

        btnStartStepOne = (Button) view.findViewById(R.id.coordinates_calibrate_btn_stepstart);
        btnStartStepOne.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onStartStepSetupClick();
                }
                btnStepIncStepOne.setEnabled(false);
                btnStepDecStepOne.setEnabled(false);
                btnStartStepOne.setEnabled(false);
                btnNextStepOne.setEnabled(true);
                btnStopStepOne.setEnabled(true);
            }
        });

        btnStopStepOne = (Button) view.findViewById(R.id.coordinates_calibrate_btn_stepstop);
        btnStopStepOne.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (controller != null) {
                    controller.onStopStepSetupClick();
                }
                btnStepIncStepOne.setEnabled(true);
                btnStepDecStepOne.setEnabled(true);
                btnStartStepOne.setEnabled(true);
                btnStopStepOne.setEnabled(false);
            }
        });
        btnStopStepOne.setEnabled(false);
    }

    private void loadStepTwoView() {
        /*
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_calibrate_step2, null);


        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(view);*/


        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        contentFrameLayout.removeAllViews();
        View view = getLayoutInflater().inflate(R.layout.activity_calibrate_step2, contentFrameLayout);



        btnNextStepTwo = (Button) view.findViewById(R.id.coordinates_calibrate_next_2);
        btnNextStepTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (controller != null) {
                    controller.onNextClicked(2);
                }
            }
        });
        btnNextStepTwo.setEnabled(false);

        btnBackStepTwo = (Button) view.findViewById(R.id.coordinates_calibrate_back_2);
        btnBackStepTwo.setOnClickListener(new View.OnClickListener() {
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
                        btnNextStepTwo.setEnabled(true);
                    }
                }
            }
        });

        stepDistanceView = (TextView) view.findViewById(R.id.coordinates_calibrate_avgstepdistance);
        stepPeriodView = (TextView) view.findViewById(R.id.coordinates_calibrate_avgstepperiod);

    }

    private void loadStepThreeView() {
        /*
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_calibrate_step3, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(view);*/

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        contentFrameLayout.removeAllViews();
        View view = getLayoutInflater().inflate(R.layout.activity_calibrate_step3, contentFrameLayout);

        Button btnNext = (Button) view.findViewById(R.id.coordinates_calibrate_next_3);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMeasurement();
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
