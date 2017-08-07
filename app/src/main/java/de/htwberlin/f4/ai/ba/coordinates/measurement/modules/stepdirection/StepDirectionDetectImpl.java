package de.htwberlin.f4.ai.ba.coordinates.measurement.modules.stepdirection;

import android.util.Log;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.Sensor;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorData;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModelImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;

/**
 * Created by benni on 05.08.2017.
 *
 * not working correctly if the step gets detected before the last high / lowpeak occured..
 * For example: we did a rightstep, the highpeak is right before the step and the lowpeak
 * would be right after step... so we just found a highpeak and no lowpeak. Since lowpeak got
 * a default timestamp of "0", the non existent lowpeak occured before highpeak -> detect left step
 * instead of right step
 *
 * A solution would be to use a scheduled thread, which gets executed 50-100ms after the stepdetection,
 * so we could get the missing low / high peak. Then we need a IndoorMeasurementListener which can
 * inform about the direction
 *
 */

public class StepDirectionDetectImpl implements StepDirectionDetect {

    private long lastStepTimestamp;
    private SensorFactory sensorFactory;
    private Sensor sensor;
    private SensorDataModel dataModel;

    // there seems to be a general lower acceleration for a step left / right
    // so we chose a quite lower threshold value for those directions
    private static final float THRESHOLD_POSITIVE_X = 2.25f;
    private static final float THRESHOLD_NEGATIVE_X = -2.25f;
    private static final float THRESHOLD_POSITIVE_Y = 2.75f;
    private static final float THRESHOLD_NEGATIVE_Y = -2.75f;


    public StepDirectionDetectImpl(SensorFactory sensorFactory) {
        lastStepTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
        this.sensorFactory = sensorFactory;
        dataModel = new SensorDataModelImpl();
        initSensor();
    }

    // constructor for testing purpose, remove later
    public StepDirectionDetectImpl(long lastStepTimestamp) {
        this.lastStepTimestamp = lastStepTimestamp;
    }

    private void initSensor() {
        // saving data from accelerator_linear sensor, so we can check for step direction
        sensor = sensorFactory.getSensor(SensorType.ACCELEROMETER_LINEAR, Sensor.SENSOR_RATE_FASTEST);
        sensor.setListener(new SensorListener() {
            @Override
            public void valueChanged(SensorData newValue) {
                dataModel.insertData(newValue);
            }
        });

        sensor.start();
    }

    // checking for the last high and lowpeaks on x and y axis
    // so we dont get incorrect results if the user shaked the device e.g. without
    // triggering the stepdetector and doing a step afterwards.
    @Override
    public StepDirection getLastStepDirection() {
        long currentStepTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
        StepDirection direction = StepDirection.FORWARD;

        // get all data from laststep to now
        Map<SensorType, List<SensorData>> intervalMap = dataModel.getDataInInterval(lastStepTimestamp, currentStepTimestamp);
        // get all SensorType.ACCELEROMETER_LINEAR data
        List<SensorData> intervalValues = intervalMap.get(SensorType.ACCELEROMETER_LINEAR);
        // if we have data, check for direction
        if (intervalValues != null) {
            // find last peak on x axis
            SensorData[] peaksX = findPeaksX(intervalValues);
            SensorData highPeakX = peaksX[0];
            SensorData lowPeakX = peaksX[1];
            float peakDiffX = highPeakX.getValues()[0] + Math.abs(lowPeakX.getValues()[0]);
            // find last peak on y axis
            SensorData[] peaksY = findPeaksY(intervalValues);
            SensorData highPeakY = peaksY[0];
            SensorData lowPeakY = peaksY[1];
            float peakDiffY = highPeakY.getValues()[1] + Math.abs(lowPeakY.getValues()[1]);

            Log.d("tmp", "Timestamp lowpeakX: " + lowPeakX.getTimestamp() + " lowpeakX value: " + lowPeakX.getValues()[0]);
            Log.d("tmp", "Timestamp highpeakX: " + highPeakX.getTimestamp() + " highpeakX value: " + highPeakX.getValues()[0]);

            Log.d("tmp", "Timestamp lowpeakY: " + lowPeakY.getTimestamp() + " lowpeakY value: " + lowPeakY.getValues()[1]);
            Log.d("tmp", "Timestamp highpeakY: " + highPeakY.getTimestamp() + " highpeakY value: " + highPeakY.getValues()[1]);

            // check which axis movement happened last

            // movement along y axis happend last -> forward / backward
            if (highPeakX.getTimestamp() < highPeakY.getTimestamp()) {
                // make sure the highest peak is really on y axis
                if (peakDiffY > peakDiffX) {
                    // if we have a highpeak, followed by a lowpeak -> forward
                    if (highPeakY.getTimestamp() < lowPeakY.getTimestamp()) {
                        direction = StepDirection.FORWARD;
                    }
                    // if we have a lowpeak, followed by a highpeak -> backward
                    else if (lowPeakY.getTimestamp() < highPeakY.getTimestamp()) {
                        direction = StepDirection.BACKWARD;
                    }
                }
                // the last movement was along y axis but with a lower peak than x,
                // so we assume its because of noise or user movement and ignore the peak
                else if (peakDiffY < peakDiffX) {
                    // if we have a highpeak, followed by a lowpeak -> right
                    if (highPeakX.getTimestamp() < lowPeakX.getTimestamp()) {
                        direction = StepDirection.RIGHT;
                    }
                    // if we have a lowpeak, followed by a highpeak -> left
                    else if (lowPeakX.getTimestamp() < highPeakX.getTimestamp()) {
                        direction = StepDirection.LEFT;
                    }
                }

            }

            // movement along x axis happened last -> left / right
            else if (highPeakX.getTimestamp() > highPeakY.getTimestamp()) {
                // make sure highest peak is really on x axis
                if (peakDiffX > peakDiffY) {
                    // if we have a highpeak, followed by a lowpeak -> right
                    if (highPeakX.getTimestamp() < lowPeakX.getTimestamp()) {
                        direction = StepDirection.RIGHT;
                    }
                    // if we have a lowpeak, followed by a highpeak -> left
                    else if (lowPeakX.getTimestamp() < highPeakX.getTimestamp()) {
                        direction = StepDirection.LEFT;
                    }
                }

                // the last movement was along x axis but with a lower peak than y,
                // so we assume its because of noise or user movement and ignore the peak
                else if (peakDiffX < peakDiffY) {
                    // if we have a highpeak, followed by a lowpeak -> forward
                    if (highPeakY.getTimestamp() < lowPeakY.getTimestamp()) {
                        direction = StepDirection.FORWARD;
                    }
                    // if we have a lowpeak, followed by a highpeak -> backward
                    else if (lowPeakY.getTimestamp() < highPeakY.getTimestamp()) {
                        direction = StepDirection.BACKWARD;
                    }
                }



            }
        }

        lastStepTimestamp = currentStepTimestamp;

        return direction;
    }

    @Override
    public Sensor getSensor() {
        return sensor;
    }

    // Making a step forward / backward results in change of linear_acceleration on y axis
    // result[0] = highpeak
    // result[1] = lowpeak
    private SensorData[] findPeaksY(List<SensorData> dataList) {
        SensorData highPeak = new SensorData();
        SensorData lowPeak = new SensorData();
        SensorData[] result = new SensorData[2];
        boolean foundLowPeak = false;
        boolean foundHighPeak = false;

        // start at the end from data
        for (int i = dataList.size()-1; i > 0; i--) {
            SensorData data = dataList.get(i);

            // check for high peak
            // value has to be greater than the threshold and old highpeak
            if (data.getValues()[1] >= THRESHOLD_POSITIVE_Y && data.getValues()[1] > highPeak.getValues()[1]) {
                highPeak = data;
                // check if the previous data is smaller, so we can be sure it's a highpeak
                if (dataList.get(i-1).getValues()[1] < highPeak.getValues()[1]) {
                    foundHighPeak = true;
                }
            }
            // check for low peak
            else if (data.getValues()[1] <= THRESHOLD_NEGATIVE_Y && data.getValues()[1] < lowPeak.getValues()[1]) {
                lowPeak = data;
                // check if the previous data is bigger, so we can be sure it's a lowpeak
                if (dataList.get(i-1).getValues()[1] > lowPeak.getValues()[1]) {
                    foundLowPeak = true;
                }
            }

            if (foundLowPeak && foundHighPeak) {
                break;
            }
        }


        result[0] = highPeak;
        result[1] = lowPeak;

        return result;
    }


    // Making a step left / right results in change of linear_acceleration on x axis
    // result[0] = highpeak
    // result[1] = lowpeak
    private SensorData[] findPeaksX(List<SensorData> dataList) {
        SensorData highPeak = new SensorData();
        SensorData lowPeak = new SensorData();
        SensorData[] result = new SensorData[2];
        boolean foundLowPeak = false;
        boolean foundHighPeak = false;

        // start at the end from data
        for (int i = dataList.size()-1; i > 0; i--) {
            SensorData data = dataList.get(i);

            // check for high peak
            // value has to be greater than the threshold and old highpeak
            if (data.getValues()[0] >= THRESHOLD_POSITIVE_X && data.getValues()[0] > highPeak.getValues()[0]) {
                highPeak = data;
                // check if the previous data is smaller, so we can be sure it's a highpeak
                if (dataList.get(i-1).getValues()[0] < highPeak.getValues()[0]) {
                    foundHighPeak = true;
                }
            }
            // check for low peak
            else if (data.getValues()[0] <= THRESHOLD_NEGATIVE_X && data.getValues()[0] < lowPeak.getValues()[0]) {
                lowPeak = data;
                // check if the previous data is bigger, so we can be sure it's a lowpeak
                if (dataList.get(i-1).getValues()[0] > lowPeak.getValues()[0]) {
                    foundLowPeak = true;
                }
            }

            if (foundLowPeak && foundHighPeak) {
                break;
            }
        }


        result[0] = highPeak;
        result[1] = lowPeak;

        return result;
    }


}
