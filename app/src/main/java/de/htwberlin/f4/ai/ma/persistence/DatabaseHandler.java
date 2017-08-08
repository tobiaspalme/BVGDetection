package de.htwberlin.f4.ai.ma.persistence;

import java.util.ArrayList;

import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.location.LocationResult;
import de.htwberlin.f4.ai.ma.location.LocationResultImplementation;

/**
 * Created by Johann Winter
 *
 *
 * This interface offers all necessary database-functions to
 * create, edit and delete Nodes, Edges and LocationResults
 * and the export of the SQLite DB.
 * It also can calculate the matching node for a given node (current location).
 */

public interface DatabaseHandler {

    void insertNode(Node node);
    void updateNode(Node node, String oldNodeId);
    ArrayList<Node> getAllNodes();
    Node getNode(String nodeID);
    boolean checkIfNodeExists(String nodeID);
    void deleteNode(Node node);

    void insertLocationResult(LocationResult locationResult);
    ArrayList<LocationResultImplementation> getAllLocationResults();
    void deleteLocationResult(LocationResult locationResult);

    void insertEdge(Edge edge);
    ArrayList<Edge> getAllEdges();
    boolean checkIfEdgeExists(Edge edge);
    void deleteEdge(Edge edge);

    //boolean importDatabase(String dbPath) throws IOException;
    void exportDatabase();

    String calculateNodeId(Node node);
}
