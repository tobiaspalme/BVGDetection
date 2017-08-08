package de.htwberlin.f4.ai.ma.indoor_graph;

import java.util.ArrayList;

import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.node.Node;
//import de.htwberlin.f4.ai.ma.navigation.Path;

/**
 * Created by Johann Winter
 */

public interface IndoorGraph {

    //Path getPath(Node startNode, Node endNode);

    //TODO
    //void setEdge(Node nodeA, Node nodeB);





    void insertNode(Node node);
    void updateNode(Node node, String oldNodeId);
    Node getNode(String nodeID);
    ArrayList<Node> getAllNodes();
    void deleteNode(Node node);

    void insertEdge(Edge edge);
    boolean checkIfEdgeExists(Edge edge);
    ArrayList<Edge> getAllEdges();
    void deleteEdge(Edge edge);

    //Context getContext();



    //void exportDatabase();


    // TODO: komplettes Management von Nodes

    //DatabaseHandler getIndoorGraphDB();
}
