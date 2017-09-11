package de.htwberlin.f4.ai.ma.measurement.modules.stepdirection;

/**
 * Enumeration for the step direction
 *
 * Author: Benjamin Kneer
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
