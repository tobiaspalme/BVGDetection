package de.htwberlin.f4.ai.ma.indoor_graph;

import java.util.List;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;

/**
 * Created by Johann Winter
 */

public interface IndoorGraph {

    Path getPath(Node startNode, Node endNode);

    void setEdge(Node nodeA, Node nodeB);

    List<Node> listAllNodes();

    // TODO: komplettes Management von Nodes

}
