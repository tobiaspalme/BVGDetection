package de.htwberlin.f4.ai.ma.android.measure.edges;

/**
 * StepData Class
 *
 * used to store details for each step so we can save it later in edge object
 *
 * Author: Benjamin Kneer
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
