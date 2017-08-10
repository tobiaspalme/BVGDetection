package de.htwberlin.f4.ai.ba.coordinates.measurement;

/**
 * enum for different ways to get relative coordinates in a closed room
 */

public enum IndoorMeasurementType {

    VARIANT_A ("Variante A"),
    VARIANT_B ("Variante B"),
    VARIANT_C ("Variante C");

    private final String name;

    private IndoorMeasurementType(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }
}
