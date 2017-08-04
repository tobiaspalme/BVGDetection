package de.htwberlin.f4.ai.ma.indoor_graph;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;
import de.htwberlin.f4.ai.ma.navigation.Path;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;


/**
 * Created by Johann Winter
 */

public class IndoorGraphImplementation implements IndoorGraph {

    //private Context ctx;
    //private DatabaseHandler databaseHandler = new DatabaseHandlerImplementation(ctx);

    private static DatabaseHandler instance;

    @Override
    public Path getPath(Node startNode, Node endNode) {
        return null;
    }

    @Override
    public void setEdge(Node nodeA, Node nodeB) {}

    /*
    public List<Node> getAllNodes() {
        List<Node> allNodes = databaseHandler.getAllNodes();
        return allNodes;
    }*/


/*
    public static DatabaseHandler getIndoorGraphDB() {
            if (instance == null) {
                instance = new DatabaseHandlerImplementation(null);
            }
            return instance;
    }
*/

}
