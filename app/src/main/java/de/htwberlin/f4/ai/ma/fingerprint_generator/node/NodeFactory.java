package de.htwberlin.f4.ai.ma.fingerprint_generator.node;

import java.util.List;

/**
 * Created by Johann Winter
 */

public class NodeFactory {

    public static Node getInstance() {
        return new NodeImplementation();
    }

    public static Node getInstance(String id, float zValue, String description, List<SignalInformation> signalInformationList, String coordinates, String picturePath) {
        return new NodeImplementation(id, zValue, description, signalInformationList, coordinates, picturePath);
    }
}
