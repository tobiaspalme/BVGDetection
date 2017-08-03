package de.htwberlin.f4.ai.ma.indoor_graph;

import java.util.List;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;

/**
 * Created by Johann Winter
 */

public interface IndoorGraph {

    Path getPath(Node startNode, Node endNode);

    //TODO
    void setEdge(Node nodeA, Node nodeB);

    //List<Node> getAllNodes();

    // TODO: komplettes Management von Nodes

    //DatabaseHandler getIndoorGraphDB();
}
