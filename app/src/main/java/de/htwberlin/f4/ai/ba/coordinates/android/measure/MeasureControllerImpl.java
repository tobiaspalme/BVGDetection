package de.htwberlin.f4.ai.ba.coordinates.android.measure;

import android.app.AlertDialog;
import android.os.Handler;
import android.os.Looper;

import de.htwberlin.f4.ai.ba.coordinates.android.calibrate.CalibratePersistance;
import de.htwberlin.f4.ai.ba.coordinates.android.calibrate.CalibratePersistanceImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorData;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModelImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactoryImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurement;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurementFactory;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurementListener;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurementType;

/**
 * Created by benni on 18.07.2017.
 */

public class MeasureControllerImpl implements MeasureController {

    private static final int CALIBRATION_TIME = 3000;

    private MeasureView view;
    private IndoorMeasurement indoorMeasurement;
    private Handler timerHandler;
    private MeasureCalibration pressureCalibration;
    private SensorDataModel sensorDataModel;
    private boolean calibrated;
    private AlertDialog calibrationDialog;
    private int stepCount = -1;

    @Override
    public void setView(MeasureView view) {
        this.view = view;
    }


    @Override
    public void onStartClicked() {



        calibrated = false;
        sensorDataModel = new SensorDataModelImpl();

        SensorFactory sensorFactory = new SensorFactoryImpl(view.getContext());
        indoorMeasurement = IndoorMeasurementFactory.getIndoorMeasurement(sensorFactory);



        indoorMeasurement.setIndoorMeasurementListener(new IndoorMeasurementListener() {

            @Override
            public void onNewCoordinates(float x, float y, float z) {
                view.updateCoordinates(x, y, z);
                stepCount++;
                view.updateStepCount(stepCount);
            }
        });

        indoorMeasurement.setSensorListener(new SensorListener() {
            @Override
            public void valueChanged(SensorData sensorData) {
                SensorType sensorType = sensorData.getSensorType();
                switch (sensorType) {

                    case COMPASS_FUSION:
                        view.updateAzimuth(sensorData.getValues()[0]);
                        // store compass data in model, while calibration
                        // isn't finished
                        if (!calibrated) {
                            sensorDataModel.insertData(sensorData);
                        }
                        break;
                    case BAROMETER:
                        view.updatePressure(sensorData.getValues()[0]);
                        // store barometer data in model, while airpressure calibration
                        // isn't finished
                        if (!calibrated) {
                            sensorDataModel.insertData(sensorData);
                        }

                        break;
                    default:
                        break;

                }
            }
        });

        indoorMeasurement.startSensors(SensorType.COMPASS_FUSION,
                                SensorType.BAROMETER);

        calibratePressure();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
        alertDialogBuilder.setMessage("Bitte warten");
        alertDialogBuilder.setTitle("Kalibrierung im gange");
        calibrationDialog = alertDialogBuilder.create();
        calibrationDialog.setCancelable(false);
        calibrationDialog.show();

    }

    @Override
    public void onStopClicked() {
        if (indoorMeasurement != null) {
            indoorMeasurement.stop();
        }
    }

    @Override
    public void onAddClicked() {

    }

    @Override
    public void onPause() {
        if (indoorMeasurement != null) {
            indoorMeasurement.stop();
        }
    }

    private void calibratePressure() {
        timerHandler = new Handler(Looper.getMainLooper());
        pressureCalibration = new MeasureCalibration(sensorDataModel);

        pressureCalibration.setListener(new MeasureCalibrationListener() {
            @Override
            public void onFinish(float airPressure, float azimuth) {
                calibrationDialog.dismiss();
                timerHandler.removeCallbacks(pressureCalibration);
                calibrated = true;
                // load calibration
                CalibratePersistance calibratePersistance = new CalibratePersistanceImpl(view.getContext());
                if (calibratePersistance.load()) {
                    // calibrate
                    indoorMeasurement.calibrate(calibratePersistance.getStepLength(), calibratePersistance.getStepPeriod(), airPressure, azimuth);
                    // start measurement
                    indoorMeasurement.start(IndoorMeasurementType.VARIANT_A);
                }
            }
        });

        timerHandler.postDelayed(pressureCalibration, CALIBRATION_TIME);


    }
}
