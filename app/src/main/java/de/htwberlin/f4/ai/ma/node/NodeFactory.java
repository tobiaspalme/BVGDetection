package de.htwberlin.f4.ai.ma.node;

import java.util.List;

/**
 * Created by Johann Winter
 */

public class NodeFactory {

    //public static Node createInstance(String id, float zValue, String description, List<SignalInformation> signalInformationList, String coordinates, String picturePath, String additionalInfo) {

    public static Node createInstance(String id, String description, Fingerprint fingerprint, String coordinates, String picturePath, String additionalInfo) {
        //return new NodeImplementation(id, zValue, description, signalInformationList, coordinates, picturePath, additionalInfo);
        return new NodeImplementation(id, description, fingerprint, coordinates, picturePath, additionalInfo);

    }
}
