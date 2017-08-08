package de.htwberlin.f4.ai.ma.indoor_graph;

import android.content.Context;

import java.util.ArrayList;

import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.node.Node;
//import de.htwberlin.f4.ai.ma.navigation.Path;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerImplementation;


/**
 * Created by Johann Winter
 */

class IndoorGraphImplementation implements IndoorGraph {

    private Context context;
    private DatabaseHandler databaseHandler = new DatabaseHandlerImplementation(context);

    private static DatabaseHandler instance;


    IndoorGraphImplementation(Context context) {
        this.context = context;
    }



    //@Override
    //public Path getPath(Node startNode, Node endNode) {
    //    return null;
    //}

    @Override
    public void insertNode(Node node) {
        databaseHandler.insertNode(node);
    }

    @Override
    public void updateNode(Node node, String oldNodeId) {
        databaseHandler.updateNode(node, oldNodeId);
    }

    @Override
    public Node getNode(String nodeID) {
        return databaseHandler.getNode(nodeID);
    }


    @Override
    public ArrayList<Node> getAllNodes() {
        return databaseHandler.getAllNodes();
    }

    @Override
    public void deleteNode(Node node) {
        databaseHandler.deleteNode(node);
    }

    @Override
    public void insertEdge(Edge edge) {
        databaseHandler.insertEdge(edge);
    }

    @Override
    public boolean checkIfEdgeExists(Edge edge) {
        return databaseHandler.checkIfEdgeExists(edge);
    }

    @Override
    public ArrayList<Edge> getAllEdges() {
        return databaseHandler.getAllEdges();
    }

    @Override
    public void deleteEdge(Edge edge) {
        databaseHandler.deleteEdge(edge);
    }





        /*
    @Override
    public void setEdge(Node nodeA, Node nodeB) {}
*/


/*
    public static DatabaseHandler getIndoorGraphDB() {
            if (instance == null) {
                instance = new DatabaseHandlerImplementation(null);
            }
            return instance;
    }
*/

}
