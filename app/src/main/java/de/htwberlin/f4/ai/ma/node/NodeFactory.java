package de.htwberlin.f4.ai.ma.node;

import java.util.List;

/**
 * Created by Johann Winter
 */

public class NodeFactory {

    public static Node createInstance(String id, String description, Fingerprint fingerprint, String coordinates, String picturePath, String additionalInfo) {
        return new NodeImplementation(id, description, fingerprint, coordinates, picturePath, additionalInfo);

    }
}
