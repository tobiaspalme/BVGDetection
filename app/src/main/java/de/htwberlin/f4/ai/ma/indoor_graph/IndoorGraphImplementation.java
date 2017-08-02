package de.htwberlin.f4.ai.ma.indoor_graph;

import android.content.Context;

import java.util.List;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerImplementation;


/**
 * Created by Johann Winter
 */

class IndoorGraphImplementation implements IndoorGraph {

    Context ctx;
    DatabaseHandler databaseHandler = new DatabaseHandlerImplementation(ctx);

    @Override
    public Path getPath(Node startNode, Node endNode) {
        return null;
    }

    @Override
    public void setEdge(Node nodeA, Node nodeB) {}

    public List<Node> getAllNodes() {
        //List<Node> allNodes = databaseHandlerImplementation.getAllNodes();
        List<Node> allNodes = databaseHandler.getAllNodes();

        return allNodes;
    }



}
