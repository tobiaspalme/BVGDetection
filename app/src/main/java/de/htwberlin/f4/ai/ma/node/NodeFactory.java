package de.htwberlin.f4.ai.ma.node;

import de.htwberlin.f4.ai.ma.node.fingerprint.Fingerprint;

/**
 * Created by Johann Winter
 */

public class NodeFactory {

    public static Node createInstance(String id, String description, Fingerprint fingerprint, String coordinates, String picturePath, String additionalInfo) {
        return new NodeImplementation(id, description, fingerprint, coordinates, picturePath, additionalInfo);

    }
}
