package de.htwberlin.f4.ai.ba.coordinates.android.calibrate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorData;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactoryImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurement;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurementFactory;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurementListener;

/**
 * Created by benni on 18.07.2017.
 */

public class CalibrateControllerImpl implements CalibrateController {

    private CalibrateView view;
    private IndoorMeasurement indoorMeasurement;

    private int stepCount;
    // for calculating the avg step period
    private List<Long> stepTimes;
    private float stepDistance;
    private int stepPeriod;

    @Override
    public void onStartStepSetupClick() {

        stepTimes = new ArrayList<>();
        SensorFactory sensorFactory = new SensorFactoryImpl(view.getContext());
        indoorMeasurement = IndoorMeasurementFactory.getIndoorMeasurement(sensorFactory);
        indoorMeasurement.setListener(new IndoorMeasurementListener() {
            @Override
            public void valueChanged(SensorData sensorData) {
                if (sensorData.getSensorType() == SensorType.STEPCOUNTER) {
                    stepCount = (int) sensorData.getValues()[0];
                    view.updateStepCount(stepCount);
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    stepTimes.add(timestamp.getTime());
                }
            }
        });
        indoorMeasurement.startSensors(SensorType.STEPCOUNTER);
    }

    @Override
    public void onStopStepSetupClick() {
        if (indoorMeasurement != null) {
            indoorMeasurement.stop();
        }
    }

    @Override
    public void onNextClicked(int currentStep) {
        // stop all sensors
        if (indoorMeasurement != null) {
            indoorMeasurement.stop();
        }

        if (currentStep == 1) {
            view.loadCalibrateStep(2);
            stepPeriod = calculateAverageStepperiod(stepTimes);
            view.updateAverageStepperiod(stepPeriod);
        } else if (currentStep == 2) {
            view.loadCalibrateStep(3);

            if (indoorMeasurement != null) {
                indoorMeasurement.setListener(new IndoorMeasurementListener() {
                    @Override
                    public void valueChanged(SensorData sensorData) {
                        if (sensorData.getSensorType() == SensorType.COMPASS_FUSION) {
                            view.updateAzimuth((int) sensorData.getValues()[0]);
                        }
                    }
                });
            }
            indoorMeasurement.startSensors(SensorType.COMPASS_FUSION);

        } else if (currentStep == 3) {
            if (indoorMeasurement != null) {
                indoorMeasurement.stop();
            }
            //TODO: save calibration
        }

    }

    @Override
    public void onBackClicked(int currentStep) {
        // stop all sensors
        if (indoorMeasurement != null) {
            indoorMeasurement.stop();
        }

        if (currentStep == 2) {
            view.loadCalibrateStep(1);
        } else if (currentStep == 3) {
            view.loadCalibrateStep(2);
        }
    }


    @Override
    public void onStepIncreaseClicked() {
        stepCount++;
        view.updateStepCount(stepCount);
    }

    @Override
    public void onStepDecreaseClicked() {
        stepCount--;
        view.updateStepCount(stepCount);
    }

    @Override
    public void onDistanceChange(float distance) {
        stepDistance = distance;

        if (stepCount != 0 && distance != 0) {
            view.updateAverageStepdistance(distance / stepCount);
        }
    }

    @Override
    public void onPause() {
        if (indoorMeasurement != null) {
            indoorMeasurement.stop();
        }
    }

    @Override
    public void setView(CalibrateView view) {
        this.view = view;
    }

    private int calculateAverageStepperiod(List<Long> timestamps) {
        List<Long> stepDurations = new ArrayList<>();

        // calculate the durations between each step
        for (int i = 0; i < timestamps.size(); i++) {
            if (i < timestamps.size() -1) {
                Long start = timestamps.get(i);
                Long end = timestamps.get(i+1);
                Long difference = end - start;
                stepDurations.add(difference);
            }
        }

        // sum up all durations
        Long durationSum = 0L;
        for (Long duration : stepDurations) {
            durationSum += duration;
        }

        // calculate avg
        int result = (int) (durationSum / (stepDurations.size()));

        return result;
    }

}
