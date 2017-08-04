package de.htwberlin.f4.ai.ma.persistence;

import java.util.ArrayList;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;
import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.location_result.LocationResult;
import de.htwberlin.f4.ai.ma.location_result.LocationResultImplementation;

/**
 * Created by Johann Winter
 */

public interface DatabaseHandler {

    void insertNode(Node node);
    void updateNode(Node node, String oldNodeId);
    ArrayList<Node> getAllNodes();
    Node getNode(String nodeName);
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

}
