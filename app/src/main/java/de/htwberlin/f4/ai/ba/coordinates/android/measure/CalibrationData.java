package de.htwberlin.f4.ai.ba.coordinates.android.measure;

/**
 * SImple class for saving calibration stuff
 */

public class CalibrationData {

    private float stepLength;
    private int stepPeriod;
    private float airPressure;
    private float[] coordinates;

    public CalibrationData() {
        stepLength = 0.0f;
        stepPeriod = 0;
        airPressure = 0.0f;
        coordinates = new float[3];
    }

    public CalibrationData(float stepLength, int stepPeriod) {
        this.stepLength = stepLength;
        this.stepPeriod = stepPeriod;
        airPressure = 0.0f;
        coordinates = new float[3];
    }

    public CalibrationData(float stepLength, int stepPeriod, float[] coordinates) {
        this.stepLength = stepLength;
        this.stepPeriod = stepPeriod;
        airPressure = 0.0f;
        this.coordinates = coordinates;
    }

    public float getStepLength() {
        return stepLength;
    }

    public void setStepLength(float stepLength) {
        this.stepLength = stepLength;
    }

    public int getStepPeriod() {
        return stepPeriod;
    }

    public void setStepPeriod(int stepPeriod) {
        this.stepPeriod = stepPeriod;
    }

    public float getAirPressure() {
        return airPressure;
    }

    public void setAirPressure(float airPressure) {
        this.airPressure = airPressure;
    }

    public float[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(float[] coordinates) {
        this.coordinates = coordinates;
    }
}
