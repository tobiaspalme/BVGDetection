package de.htwberlin.f4.ai.ma.navigation.dijkstra;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.node.Node;

/**
 * Created by Johann Winter
 *
 * Thanks to tognitos for contribution.
 *
 * The Dijkstra algorithm uses maps the common Node and Edge objects to its own DijkstraNode and
 * DijkstraEdge objects in order to avoid loading the model objects with the algorithm's logic.
 * It is important that methods from the Node and Edge classes (of the Model) are avoided.
 * This allows us to change just how the mapping to the DijkstraNodes and DijkstraEdges work.
 */

public interface DijkstraAlgorithm {

    /**
     * Map the normal Node objects from the Model to the custom DijkstraNode of the Dijkstra Algorithm.
     * This is done to avoid filling the model objects with logic elements.
     * @param nodes a list of Nodes
     * @return a list of DijkstraNodes
     */
    List<DijkstraNode> mapNodes(ArrayList<Node> nodes);

    /**
     * Map the normal Edge objects from the Model to the custom DijkstraEdge of the Dijkstra
     * algorithm. This is done in order to avoid filling the model objects with logic elements.
     * Since the graph is bidirectional, it maps 2 DijkstraEdges: one for poiA->poiB, one for poiB->poiA.
     * @param edges a list of Edges
     * @return a list of DijkstraEdges
     */
    List<DijkstraEdge> mapEdges(ArrayList<Edge> edges);

    /**
     * Executes all the calculations and the shortest paths from the specified source node.
     * @param sourceNodeId the startnode for the calculations
     * @throws IllegalArgumentException if the sourcenodeid does not exist
     */
    void execute(String sourceNodeId) throws IllegalArgumentException;

    /**
     * Find the minimal distance from a node
     * @param node the startnode
     */
    void findMinimalDistances(DijkstraNode node);

    /**
     * Get the distance between the two DijkstraNodes
     * @param node the start-node
     * @param target the target-node
     * @return the weight (distance) between start-node and target-node
     */
    double getDistance(DijkstraNode node, DijkstraNode target);

    /**
     * Get all the neighbours (directly connected DijkstraNodes) for the specified node.
     * @param node the specified node
     * @return list of neighbor nodes
     */
    List<DijkstraNode> getNeighbors(DijkstraNode node);

    /**
     * Get the minimum shortest distance of all the DijkstraNodes.
     * @param dijkstraNodes a set of DijkstraNodes
     * @return the nearest DijkstraNode
     */
    DijkstraNode getMinimumDistance(Set<DijkstraNode> dijkstraNodes);

    /**
     * @param dijkstraNode the input DijkstraNode
     * @return true if the DijkstraNode was already settled
     */
    boolean isSettled(DijkstraNode dijkstraNode);

    /**
     * Gets the shortest distance to the destination, from the calculated start-node (called through
     * the method execute).
     * @param destinationNode the destination-node
     * @return the shortest distance
     */
    double getShortestDistance(DijkstraNode destinationNode);

    /**
     * This method returns the path from the source to the selected target and
     * NULL if no path exists
     *
     * @param targetSourceId the ID of the target Node
     */
    LinkedList<Node> getPath(String targetSourceId);

}
