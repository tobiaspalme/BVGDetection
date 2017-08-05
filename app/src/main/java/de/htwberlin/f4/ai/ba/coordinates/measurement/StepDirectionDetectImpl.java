package de.htwberlin.f4.ai.ba.coordinates.measurement;

import android.util.Log;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorData;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;

/**
 * Created by benni on 05.08.2017.
 */

public class StepDirectionDetectImpl implements StepDirectionDetect {

    private long lastStepTimestamp;
    private static final float THRESHOLD_POSITIVE = 2.5f;
    private static final float THRESHOLD_NEGATIVE = -2.5f;


    public StepDirectionDetectImpl() {
        lastStepTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
    }

    //TODO: liste mit allen peaks erstellen und nur den letzten peak überprüfen,
    // falls ein Step nicht erkannt wurde
    @Override
    public StepDirection getLastStepDirection(SensorDataModel dataModel) {
        long currentStepTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
        StepDirection direction = StepDirection.FORWARD;

        // get all data from laststep to now
        Map<SensorType, List<SensorData>> intervalMap = dataModel.getDataInInterval(lastStepTimestamp, currentStepTimestamp);
        // get all SensorType.ACCELEROMETER_LINEAR data
        List<SensorData> intervalValues = intervalMap.get(SensorType.ACCELEROMETER_LINEAR);
        // if we have data, check for direction
        if (intervalValues != null) {
            SensorData[] peaksX = findPeaksX(intervalValues);
            SensorData highPeakX = peaksX[0];
            SensorData lowPeakX = peaksX[1];

            SensorData[] peaksY = findPeaksY(intervalValues);
            SensorData highPeakY = peaksY[0];
            SensorData lowPeakY = peaksY[1];

            Log.d("tmp", "Timestamp lowpeakX: " + lowPeakX.getTimestamp() + "lowpeakX value: " + lowPeakX.getValues()[1]);
            Log.d("tmp", "Timestamp highpeakX: " + highPeakX.getTimestamp() + "highpeakX value: " + highPeakX.getValues()[1]);

            Log.d("tmp", "Timestamp lowpeakY: " + lowPeakY.getTimestamp() + "lowpeakY value: " + lowPeakY.getValues()[1]);
            Log.d("tmp", "Timestamp highpeakY: " + highPeakY.getTimestamp() + "highpeakY value: " + highPeakY.getValues()[1]);

            // check if forward/backward OR left/right

            // if we have the highest peak on x axis
            if (highPeakX.getValues()[0] > highPeakY.getValues()[1]) {
                // if we have a highpeak, followed by a lowpeak -> right
                if (highPeakX.getTimestamp() < lowPeakX.getTimestamp()) {
                    direction = StepDirection.RIGHT;
                }
                // if we have a lowpeak, followed by a highpeak -> left
                else if (lowPeakX.getTimestamp() < highPeakX.getTimestamp()) {
                    direction = StepDirection.LEFT;
                }
            }
            // if we have the highest peak on y axis
            else if (highPeakY.getValues()[1] > highPeakX.getValues()[0]) {
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

        lastStepTimestamp = currentStepTimestamp;

        return direction;
    }

    // Making a step forward / backward results in change of linear_acceleration on y axis
    // result[0] = highpeak
    // result[1] = lowpeak
    private SensorData[] findPeaksY(List<SensorData> dataList) {
        SensorData highPeak = new SensorData();
        SensorData lowPeak = new SensorData();
        SensorData[] result = new SensorData[2];

        for (SensorData data : dataList) {
            // check for high peak
            // value has to be greater than the threshold and old highpeak
            if (data.getValues()[1] >= THRESHOLD_POSITIVE && data.getValues()[1] > highPeak.getValues()[1]) {
                highPeak = data;
            }
            // check for low peak
            else if (data.getValues()[1] <= THRESHOLD_NEGATIVE && data.getValues()[1] < lowPeak.getValues()[1]) {
                lowPeak = data;
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

        for (SensorData data : dataList) {
            // check for high peak
            // value has to be greater than the threshold and old highpeak
            if (data.getValues()[0] >= THRESHOLD_POSITIVE && data.getValues()[0] > highPeak.getValues()[0]) {
                highPeak = data;
            }
            // check for low peak
            else if (data.getValues()[0] <= THRESHOLD_NEGATIVE && data.getValues()[0] < lowPeak.getValues()[0]) {
                lowPeak = data;
            }
        }

        result[0] = highPeak;
        result[1] = lowPeak;

        return result;
    }


}
