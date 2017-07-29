package de.htwberlin.f4.ai.ba.coordinates.android.record;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorData;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModel;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorDataModelImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactory;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorFactoryImpl;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.SensorType;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.accelerometer.AccelerometerLinear;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.accelerometer.AccelerometerSimple;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.barometer.Barometer;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.gravity.GravitySensor;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.gyroscope.Gyroscope;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.gyroscope.GyroscopeUncalibrated;
import de.htwberlin.f4.ai.ba.coordinates.android.sensors.magneticfield.MagneticFieldSensor;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurement;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurementFactory;
import de.htwberlin.f4.ai.ba.coordinates.measurement.IndoorMeasurementListener;

/**
 * Created by benni on 22.07.2017.
 */

public class RecordControllerImpl implements RecordController {

    private RecordView view;
    private IndoorMeasurement indoorMeasurement;
    private SensorDataModel sensorDataModel;
    private Handler timerHandler;
    private Runnable recordRunnable;
    private int savePeriod;


    public RecordControllerImpl() {
        sensorDataModel = new SensorDataModelImpl();
        savePeriod = 0;
    }

    @Override
    public void setView(RecordView view) {
        this.view = view;
    }

    @Override
    public void onStartClicked() {

        SensorFactory sensorFactory = new SensorFactoryImpl(view.getContext());
        indoorMeasurement = IndoorMeasurementFactory.getIndoorMeasurement(sensorFactory);
        indoorMeasurement.setListener(new IndoorMeasurementListener() {
            @Override
            public void valueChanged(SensorData sensorData) {
                SensorType sensorType = sensorData.getSensorType();

                switch (sensorType) {

                    case ACCELEROMETER_SIMPLE:
                        view.updateAcceleration(sensorData.getValues());
                        break;
                    case ACCELEROMETER_LINEAR:
                        view.updateAccelerationLinear(sensorData.getValues());
                        break;
                    case GRAVITY:
                        view.updateGravity(sensorData.getValues());
                        break;
                    case GYROSCOPE:
                        view.updateGyroscope(sensorData.getValues());
                        break;
                    case GYROSCOPE_UNCALIBRATED:
                        view.updateGyroscopeUncalibrated(sensorData.getValues());
                        break;
                    case MAGNETIC_FIELD:
                        view.updateMagneticField(sensorData.getValues());
                        break;
                    case COMPASS_FUSION:
                        view.updateCompassFusion((int) sensorData.getValues()[0]);
                        break;
                    case COMPASS_SIMPLE:
                        view.updateCompassSimple((int) sensorData.getValues()[0]);
                        break;
                    case BAROMETER:
                        view.updatePressure(sensorData.getValues()[0]);
                        break;
                    default:
                        break;

                }
            }
        });

        indoorMeasurement.startSensors(SensorType.ACCELEROMETER_SIMPLE,
                                SensorType.ACCELEROMETER_LINEAR,
                                SensorType.GRAVITY,
                                SensorType.GYROSCOPE,
                                SensorType.GYROSCOPE_UNCALIBRATED,
                                SensorType.MAGNETIC_FIELD,
                                SensorType.COMPASS_FUSION,
                                SensorType.COMPASS_SIMPLE,
                                SensorType.BAROMETER);

        startTimer();

    }

    @Override
    public void onStopClicked() {
        stopMeasurement();
        stopTimer();
        saveRecordData();
    }

    @Override
    public void onPause() {
        stopMeasurement();
        stopTimer();
    }

    @Override
    public void onSavePeriodChanged(int value) {
        savePeriod = value;
    }

    private void startTimer() {
        timerHandler = new Handler(Looper.getMainLooper());
        recordRunnable = new RecordRunnable(sensorDataModel, indoorMeasurement, timerHandler, savePeriod);
        timerHandler.postDelayed(recordRunnable, 250);
    }

    private void stopTimer() {
        if (timerHandler != null) {
            timerHandler.removeCallbacks(recordRunnable);
        }
    }

    private void stopMeasurement() {
        if (indoorMeasurement != null) {
            indoorMeasurement.stop();
        }
    }

    /**
     * In order to access the data do the following on your phone:
     * Open settings -> Memory & USB -> explore -> navigate to the directory
     * Open the File in GoogleDocs -> open it on PC
     *
     * This is necessary because Nexus5X doesn't have a real external storage (SD CARD)
     * and Android prevents user access to files unless you have rooted your device.
     *
     * Write Sensordatas to file in csv format
     * sensortype;timestamp;value[0];value[1];value[2]
     */
    private void saveRecordData() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/Coordinates/RecordData");
        if (!dir.exists()) {
            dir.mkdirs();
        }


        File file = new File(dir, String.valueOf(timestamp.getTime()) + ".txt");

        FileOutputStream outputStream;


        try {
            outputStream = new FileOutputStream(file);
            Map<SensorType, List<SensorData>> data = sensorDataModel.getData();
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
