package de.htwberlin.f4.ai.ma.location.locationcalculator;

import com.google.common.collect.Multimap;

import java.util.List;

import de.htwberlin.f4.ai.ma.location.calculations.FoundNode;
import de.htwberlin.f4.ai.ma.location.calculations.RestructedNode;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.node.fingerprint.Fingerprint;
import de.htwberlin.f4.ai.ma.node.fingerprint.SignalInformation;
import de.htwberlin.f4.ai.ma.node.fingerprint.signalstrength.SignalStrength;

/**
 * Created by Johann Winter
 */

public interface LocationCalculator {

    /**
     * Calculate a Node for a Fingerprint
     *
     * @param fingerprint the input Fingerprint to be compared with all existent Nodes to get the position
     * @return the ID (name) of the resulting Node
     */
    FoundNode calculateNodeId(Fingerprint fingerprint);


    /**
     * Get a list of SignalStrengths by passing a list of SignalInformation (unwrap).
     *
     * @param signalInformationList a list of SignalInformations
     * @return a list of SignalStrengthInformations
     */
    List<SignalStrength> getSignalStrengths(List<SignalInformation> signalInformationList);


    /**
     * Rewrite the nodelist to restrucetd Nodes and delete weak MAC addresses
     *
     * @param allNodes list of all nodes
     * @return restructed Node list
     */
    List<RestructedNode> calculateNewNodeDateset(List<Node> allNodes);


    /**
     * Create a multimap with MAC address and signal strength values
     *
     * @param node input Node
     * @param macAdresses list of MAC addresses
     * @return multimap with mac address and signal strengths
     */
    Multimap<String, Double> getMultiMap(Node node, List<String> macAdresses);


    /**
     * Get all mac addresses of a specific Node
     *
     * @param node the Node
     * @return list of unique MAC addresses
     */
    List<String> getMacAddresses(Node node);

}