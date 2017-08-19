package de.htwberlin.f4.ai.ba.coordinates.measurement;

/**
 * Created by benni on 19.08.2017.
 */

public class WKT {

    public static final String POINT_Z = "POINT Z";


    // expects 3 coordinate values
    public static String coordToStr(float[] coordinates) {
        String result = null;

        if (coordinates.length == 3) {
            result = POINT_Z + "(" + coordinates[0] + " " + coordinates[1] + " " + coordinates[2] + ")";
        }

        return result;
    }

    // expects a string containing 3 coordinate values
    // for example: POINT Z(1.0 2.0 3.0)
    public static float[] strToCoord(String coordinatesStr) {
        float[] coordinates = null;
        String subStr = coordinatesStr.substring(coordinatesStr.indexOf("(") + 1, coordinatesStr.indexOf(")"));
        String[] splitted = subStr.split(" ");

        if (splitted.length == 3) {
            coordinates = new float[3];
            coordinates[0] = Float.valueOf(splitted[0]);
            coordinates[1] = Float.valueOf(splitted[1]);
            coordinates[2] = Float.valueOf(splitted[2]);
        }

        return coordinates;
    }
}
