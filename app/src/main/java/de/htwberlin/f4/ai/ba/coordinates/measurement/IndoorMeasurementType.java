package de.htwberlin.f4.ai.ba.coordinates.measurement;

/**
 * enum for different ways to get relative coordinates in a closed room
 */

public enum IndoorMeasurementType {

    VARIANT_A("Variante A"),
    VARIANT_B("Variante B"),
    VARIANT_C("Variante C"),
    VARIANT_D("Variante D");

    private final String name;

    IndoorMeasurementType(String s) {
        name = s;
    }

    public String toString() {
        return this.name;
    }

    // needed, because valueOf doesn't work correctly
    public static IndoorMeasurementType fromString(String text) {
        for (IndoorMeasurementType b : IndoorMeasurementType.values()) {
            if (b.name.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
