package de.htwberlin.f4.ai.ma.persistence;

import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
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
 *
 * It also can calculate the matching node for a given node (current location).
 */

public interface DatabaseHandler {

    // Node management
    void insertNode(Node node);
    void updateNode(Node node, String oldNodeId);
    ArrayList<Node> getAllNodes();
    Node getNode(String nodeID);
    boolean checkIfNodeExists(String nodeID);
    void deleteNode(Node node);

    // Edges management
    void insertEdge(Edge edge);
    Edge getEdge(Node nodeA, Node nodeB);
    ArrayList<Edge> getAllEdges();
    void updateEdge(Edge edge);
    void updateEdge(Edge edge, String nodeToBeUpdated, String value);
    boolean checkIfEdgeExists(Edge edge);
    void deleteEdge(Edge edge);

    // Import / Export
    SQLiteDatabase getReadableDatabase();
    SQLiteDatabase getWritableDatabase();
    boolean importDatabase(String dbPath) throws IOException;
    boolean exportDatabase();

    // Calculate my location
    String calculateNodeId(Node node);

    // LocationResult management
    void insertLocationResult(LocationResult locationResult);
    ArrayList<LocationResultImplementation> getAllLocationResults();
    void deleteLocationResult(LocationResult locationResult);

}
