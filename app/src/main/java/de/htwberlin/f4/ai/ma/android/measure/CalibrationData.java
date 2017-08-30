package de.htwberlin.f4.ai.ma.android.measure;

import de.htwberlin.f4.ai.ma.measurement.IndoorMeasurementType;

/**
 * SImple class for saving calibration stuff
 * TODO: maybe rename properly
 */

public class CalibrationData {

    private float stepLength;
    private int stepPeriod;
    private float airPressure;
    // non calibration related stuff
    private float[] coordinates;
    private IndoorMeasurementType indoorMeasurementType;
    private float lowpassFilterValue;
    private boolean useStepDirection;
    private boolean stairs;

    public boolean isStairs() {
        return stairs;
    }

    public void setStairs(boolean stairs) {
        this.stairs = stairs;
    }

    public float getBarometerThreshold() {
        return barometerThreshold;
    }

    public void setBarometerThreshold(float barometerThreshold) {
        this.barometerThreshold = barometerThreshold;
    }

    private float barometerThreshold;

    public boolean getUseStepDirection() {
        return useStepDirection;
    }

    public void setUseStepDirection(boolean useStepDirection) {
        this.useStepDirection = useStepDirection;
    }

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

    public IndoorMeasurementType getIndoorMeasurementType() {
        return indoorMeasurementType;
    }

    public void setIndoorMeasurementType(IndoorMeasurementType indoorMeasurementType) {
        this.indoorMeasurementType = indoorMeasurementType;
    }

    public float getLowpassFilterValue() {
        return lowpassFilterValue;
    }

    public void setLowpassFilterValue(float lowpassFilterValue) {
        this.lowpassFilterValue = lowpassFilterValue;
    }
}
