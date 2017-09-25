package de.htwberlin.f4.ai.ma.indoorroutefinder.edge;

import java.util.List;

import de.htwberlin.f4.ai.ma.indoorroutefinder.node.Node;

/**
 * Created by Johann Winter
 */

public class EdgeFactory {

    public static Edge createInstance(Node nodeA, Node nodeB, boolean accessible, float weight) {
        return new EdgeImpl(nodeA, nodeB, accessible, weight);
    }

    public static Edge createInstance(Node nodeA, Node nodeB, boolean accessible, List<String> stepCoordList, float weight, String additionalInfo) {
        return new EdgeImpl(nodeA, nodeB, accessible, stepCoordList, weight, additionalInfo);
    }
}
