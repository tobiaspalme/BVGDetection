package de.htwberlin.f4.ai.ma.fingerprint_generator.node;

import java.util.List;

/**
 * Created by Johann Winter
 */

public class NodeFactory {

    public static Node getInstance() {
        return new NodeImplementation();
    }

    public static Node getInstance(String id, float zValue, List<SignalInformation> signalInformationList) {
        return new NodeImplementation(id, zValue, signalInformationList);
    }
}
