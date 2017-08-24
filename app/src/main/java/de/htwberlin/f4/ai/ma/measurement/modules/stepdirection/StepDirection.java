package de.htwberlin.f4.ai.ma.measurement.modules.stepdirection;

/**
 * Created by benni on 05.08.2017.
 */

public enum StepDirection {

    FORWARD ("Vorwärts"),
    BACKWARD ("Rückwärts"),
    LEFT ("Links"),
    RIGHT ("Rechts");

    private final String name;

    private StepDirection(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }
}
