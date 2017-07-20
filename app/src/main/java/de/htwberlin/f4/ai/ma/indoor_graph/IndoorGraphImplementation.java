package de.htwberlin.f4.ai.ma.indoor_graph;

import java.util.List;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;

/**
 * Created by Johann Winter
 */

class IndoorGraphImplementation implements IndoorGraph {

    DatabaseHandler databaseHandler;

    @Override
    public Path getPath(Node startNode, Node endNode) {
        return null;
    }

    @Override
    public void setEdge(Node nodeA, Node nodeB) {}

    public List<Node> listAllNodes() {
        List<Node> allNodes = databaseHandler.getAllNodes();
        return allNodes;
    }

}
