package de.htwberlin.f4.ai.ba.coordinates.measurement;

/**
 * SImple class for saving calibration stuff
 */

public class CalibrationData {

    private float stepLength;
    private int stepPeriod;
    private float airPressure;
    private float azimuth;

    public CalibrationData() {
        stepLength = 0.0f;
        stepPeriod = 0;
        airPressure = 0.0f;
        azimuth = 0.0f;
    }

    public CalibrationData(float stepLength, int stepPeriod) {
        this.stepLength = stepLength;
        this.stepPeriod = stepPeriod;
        airPressure = 0.0f;
        azimuth = 0.0f;
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

    public float getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }
}
