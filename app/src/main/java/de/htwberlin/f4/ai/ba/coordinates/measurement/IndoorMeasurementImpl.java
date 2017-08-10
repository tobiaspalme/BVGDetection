package de.htwberlin.f4.ai.ba.coordinates.measurement;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.htwberlin.f4.ai.ba.coordinates.android.CoordinatesActivity;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorData;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorListener;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.Sensor;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.PositionModule;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.a.PositionModuleA;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.b.PositionModuleB;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.c.PositionModuleC;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.stepdirection.StepDirection;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.stepdirection.StepDirectionDetect;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.stepdirection.StepDirectionDetectImpl;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.stepdirection.StepDirectionDetectListener;
import de.htwberlin.f4.ai.ba.coordinates.measurement.modules.stepdirection.StepDirectionRunnable;


/**
 * Class which implements IndoorMeasurement interface
 *
 *
 */

public class IndoorMeasurementImpl implements IndoorMeasurement {

    private SensorFactory sensorFactory;
    private SensorListener sensorListener;
    private List<Sensor> sensorList;
    private CalibrationData calibrationData;

    private PositionModule positionModule;
    private StepDirectionDetect directionDetect;

    // for direction detection
    private Handler timerHandler;
    private StepDirectionRunnable stepDirectionRunnable;
    // delay for the direction detection in ms
    private static final int DIRECTION_DETECT_DELAY = 100;


    public IndoorMeasurementImpl(SensorFactory sensorFactory) {
        this.sensorFactory = sensorFactory;
        sensorList = new ArrayList<>();
    }


    @Override
    public void calibrate(CalibrationData calibrationData) {
        this.calibrationData = calibrationData;
    }

    @Override
    public void start(IndoorMeasurementType indoorMeasurementType) {
        timerHandler = new Handler(Looper.getMainLooper());
        directionDetect = new StepDirectionDetectImpl(sensorFactory);
        stepDirectionRunnable = new StepDirectionRunnable(directionDetect);
        // TODO: inform controller about direction and handle it, if direction != forward
        stepDirectionRunnable.setListener(new StepDirectionDetectListener() {
            @Override
            public void onDirectionDetect(StepDirection stepDirection) {
                /*Context context = CoordinatesActivity.getInstance().getApplicationContext();
                Toast toast = Toast.makeText(context, "Direction: " + stepDirection, Toast.LENGTH_SHORT);
                toast.show();*/
            }
        });

        // add the sensor from direction detect to our sensorlist, so we can stop it later
        sensorList.add(directionDetect.getSensor());

        // create different position module, depending on chosen viarant
        if (indoorMeasurementType == IndoorMeasurementType.VARIANT_A) {
            positionModule = new PositionModuleA(sensorFactory, calibrationData);
            positionModule.start();
        } else if (indoorMeasurementType == IndoorMeasurementType.VARIANT_B) {
            positionModule = new PositionModuleB(sensorFactory, calibrationData);
            positionModule.start();
        } else if (indoorMeasurementType == IndoorMeasurementType.VARIANT_C) {
            positionModule = new PositionModuleC(sensorFactory, calibrationData);
            positionModule.start();
        }
    }

    @Override
    public void stop() {
        // stop all sensors controlled by this class
        for (Sensor sensor : sensorList) {
            sensor.stop();
        }
        if (positionModule != null) {
            positionModule.stop();
        }
        if (timerHandler != null) {
            timerHandler.removeCallbacks(stepDirectionRunnable);
        }
    }

    @Override
    public void startSensors(int sensorRate, SensorType... sensorType) {
        sensorList.clear();

        for (final SensorType type : sensorType) {
            Sensor sensor = sensorFactory.getSensor(type, sensorRate);
            sensor.setListener(new SensorListener() {
                @Override
                public void valueChanged(SensorData newValue) {
                    if (sensorListener != null) {
                        sensorListener.valueChanged(newValue);
                    }
                }
            });
            sensor.start();
            sensorList.add(sensor);
        }
    }

    @Override
    public float[] getCoordinates() {

        //saveRecordData(dataModel);
        //StepDirection direction = directionDetect.getLastStepDirection(dataModel);
        //Context context = CoordinatesActivity.getInstance().getApplicationContext();
        //Toast toast = Toast.makeText(context, "Direction: " + direction, Toast.LENGTH_SHORT);
        //toast.show();


        //Log.d("tmp", "Direction: " + direction);
        //dataModel.clearData();

        Log.d("tmp", "before runnable time: " + new Timestamp(System.currentTimeMillis()).getTime());
        // we delay the analysis of stepdirection and do it in a thread, in case the step is detected before
        // the low / high peak of the movement happened
        timerHandler.postDelayed(stepDirectionRunnable, DIRECTION_DETECT_DELAY);

        float[] result = null;

        if (positionModule != null) {
            result = positionModule.calculatePosition();
        }
        return result;
    }

    @Override
    public void setSensorListener(SensorListener listener) {
        sensorListener = listener;
    }

    @Override
    public Map<SensorType, SensorData> getLastSensorValues() {
        Map<SensorType, SensorData> sensorValues = new HashMap<>();

        for (Sensor sensor : sensorList) {
            sensorValues.put(sensor.getSensorType(), sensor.getValues());
        }

        return sensorValues;
    }

    //TODO: remove
    private void saveRecordData(SensorDataModel dataModel) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/Coordinates/RecordData");
        if (!dir.exists()) {
            dir.mkdirs();
        }


        File file = new File(dir, "laststep.txt");

        FileOutputStream outputStream;


        try {
            outputStream = new FileOutputStream(file);
            Map<SensorType, List<SensorData>> data = dataModel.getData();
            // loop through the sensortypes
            for (Map.Entry<SensorType, List<SensorData>> entry : data.entrySet()) {
                SensorType sensorType = entry.getKey();
                List<SensorData> sensorValues = entry.getValue();

                // loop through the sensordata list
                for (SensorData valueEntry : sensorValues) {

                    StringBuilder builder = new StringBuilder();
                    builder.append(sensorType + ";" + valueEntry.getTimestamp());

                    for (int i = 0; i < valueEntry.getValues().length; i++) {
                        builder.append(";" + valueEntry.getValues()[i]);
                    }

                    builder.append(";");
                    outputStream.write(builder.toString().getBytes());
                    outputStream.write(System.lineSeparator().getBytes());
                }
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
