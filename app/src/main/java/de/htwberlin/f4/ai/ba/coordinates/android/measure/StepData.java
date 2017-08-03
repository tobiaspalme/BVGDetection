package de.htwberlin.f4.ai.ba.coordinates.android.measure;

/**
 * Created by benni on 03.08.2017.
 */

public class StepData {

    private String stepName;
    private float[] coords;

    public StepData() {
        stepName = "";
        coords = new float[]{0.0f, 0.0f, 0.0f};
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public float[] getCoords() {
        return coords;
    }

    public void setCoords(float[] coords) {
        this.coords = coords;
    }
}
