package de.htwberlin.f4.ai.ma.navigation.dijkstra;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.htwberlin.f4.ai.ma.edge.Edge;
import de.htwberlin.f4.ai.ma.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerFactory;

/**
 * Created by Johann Winter
 */


// TODO: RETURNS

public class DijkstraAlgorithmImpl implements DijkstraAlgorithm {

    /**
     * Thanks to tognitos for contribution.
     *
     * The Dijkstra algorithm uses maps the common Node and Edge objects to its own DijkstraNode and
     * DijkstraEdge objects in order to avoid loading the model objects with the algorithm's logic.
     * It is important that methods from the Node and Edge classes (of the Model) are avoided.
     * This allows us to change just how the mapping to the DijkstraNodes and DijkstraEdges work.
     * Prinzip der "losen Kopplung".
     */

    // DijkstraNodes and dijkstraEdges
    final List<DijkstraNode> dijkstraNodes;
    private final List<DijkstraEdge> dijkstraEdges;

    // Settled and unsettled dijkstraNodes (seen / not seen)
    private Set<DijkstraNode> settledNodes;
    private Set<DijkstraNode> unSettledNodes;

    // (Smallest) predecessors
    private Map<DijkstraNode, DijkstraNode> predecessors;

    // Smallest distance (weight) for the node
    private Map<DijkstraNode, Double> distance;

    private DatabaseHandler databaseHandler;
    private boolean accessible;


    public DijkstraAlgorithmImpl(Context context, boolean accessible) {
        databaseHandler = DatabaseHandlerFactory.getInstance(context);
        this.accessible = accessible;
        this.dijkstraNodes = mapNodes(databaseHandler.getAllNodes());
        this.dijkstraEdges = mapEdges(databaseHandler.getAllEdges());
    }


    /**
     * Map the normal Node objects from the Model to the custom DijkstraNode of the Dijkstra Algorithm.
     * This is done to avoid filling the model objects with logic elements.
     * @param nodes a list of Nodes
     * @return a list of DijkstraNodes
     */
    public List<DijkstraNode> mapNodes(ArrayList<Node> nodes) {
        ArrayList<DijkstraNode> dijkstraNodes = new ArrayList<>(nodes.size());
        for(Node node : nodes){
            dijkstraNodes.add(new DijkstraNode(node));
        }
        return dijkstraNodes;
    }


    /**
     * Map the normal Edge objects from the Model to the custom DijkstraEdge of the Dijkstra
     * algorithm. This is done in order to avoid filling the model objects with logic elements.
     * Since the graph is bidirectional, it maps 2 DijkstraEdges: one for poiA->poiB, one for poiB->poiA.
     * @param edges a list of Edges
     * @return a list of DijkstraEdges
     */
    public List<DijkstraEdge> mapEdges(ArrayList<Edge> edges) {
        ArrayList<DijkstraEdge> dijkstraEdges = new ArrayList<>();

        for (Edge edge : edges) {

            // Since it is an undirected graph, add both directions
            DijkstraNode source = new DijkstraNode(edge.getNodeA());
            DijkstraNode destination = new DijkstraNode(edge.getNodeB());

            // If accessible search wanted
            if (accessible) {
                if (edge.getAccessibility()) {
                    DijkstraEdge sourceToDestination = new DijkstraEdge(source, destination, edge.getWeight());
                    DijkstraEdge destinationToSource = new DijkstraEdge(destination, source, edge.getWeight());
                    dijkstraEdges.add(sourceToDestination);
                    dijkstraEdges.add(destinationToSource);
                }// If accessibility doesn't play a role
            } else {
                DijkstraEdge sourceToDestination = new DijkstraEdge(source, destination, edge.getWeight());
                DijkstraEdge destinationToSource = new DijkstraEdge(destination, source, edge.getWeight());
                dijkstraEdges.add(sourceToDestination);
                dijkstraEdges.add(destinationToSource);
            }
        }
        return dijkstraEdges;
    }


    /**
     * Executes all the calculations and the shortest paths from the specified source node.
     * @param sourceNodeId the startnode for the calculations
     * @throws IllegalArgumentException if the sourcenodeid does not exist
     */
    public void execute(String sourceNodeId) throws IllegalArgumentException {
        final Node source = databaseHandler.getNode(sourceNodeId);

        if(source == null){
            throw new IllegalArgumentException("Source Node Id is invalid! Given was:" + sourceNodeId);
        }
        final DijkstraNode sourceNode = new DijkstraNode(databaseHandler.getNode(sourceNodeId));
        settledNodes = new HashSet<>();
        unSettledNodes = new HashSet<>();
        distance = new HashMap<>();
        predecessors = new HashMap<>();
        distance.put(sourceNode, 0.0);
        unSettledNodes.add(sourceNode);
        while (unSettledNodes.size() > 0) {
            DijkstraNode node = getMinimumDistance(unSettledNodes);
            settledNodes.add(node);
            unSettledNodes.remove(node);

            Log.d("execute", "node=" + node.getId());
            findMinimalDistances(node);
        }
    }


    /**
     * Find the minimal distance from a node
     * @param node the startnode
     */
    public void findMinimalDistances(DijkstraNode node) {
        List<DijkstraNode> adjacentNodes = getNeighbors(node);
        for (DijkstraNode target : adjacentNodes) {
            if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
                distance.put(target, getShortestDistance(node) + getDistance(node, target));
                predecessors.put(target, node);
                unSettledNodes.add(target);
            }
        }
    }


    /**
     * Get the distance between the two DijkstraNodes
     * @param node the start-node
     * @param target the target-node
     * @return the weight (distance) between start-node and target-node
     */
    public double getDistance(DijkstraNode node, DijkstraNode target) {
        Log.d("getDistance", "------- GET DISTANCE ---------");
        Log.d("getDistance", "node=" + node.getId());
        Log.d("getDistance", "target=" + target.getId());

        for (DijkstraEdge dijkstraEdge : dijkstraEdges) {
            Log.d("-------", "---------------------------------------------------------");
            Log.d("getDistance", "--- dijkstraEdge.getSource().getNode().getId() = " + dijkstraEdge.getSource().getNode().getId());
            Log.d("getDistance", "--- node.getNode().getId()= " + node.getNode().getId());

            Log.d("getDistance", "+++ dijkstraEdge.getDestination().getNode().getId() = " + dijkstraEdge.getDestination().getNode().getId());
            Log.d("getDistance", "+++ target.getNode().getId()= " + target.getNode().getId());
            Log.d("-------", "---------------------------------------------------------");
            Log.d("getDistance", "SOURCE = node.getNode    " + dijkstraEdge.getSource().getNode().equals(node.getNode()));
            Log.d("getDistance", "DEST = target.getNode    " + dijkstraEdge.getDestination().getNode().equals(target.getNode()));

            if (dijkstraEdge.getSource().getNode().getId().equals(node.getNode().getId()) && dijkstraEdge.getDestination().getNode().getId().equals(target.getNode().getId())) {
                Log.d("getDistance", "Edge weight: " + dijkstraEdge.getWeight());
                return dijkstraEdge.getWeight();
            }
        }
        throw new RuntimeException("Should not happen");
    }


    /**
     * Get all the neighbours (directly connected DijkstraNodes) for the specified node.
     * @param node the specified node
     * @return list of neighbor nodes
     */
    public List<DijkstraNode> getNeighbors(DijkstraNode node) {
        List<DijkstraNode> neighbors = new ArrayList<>();
        for (DijkstraEdge dijkstraEdge : dijkstraEdges) {
            if (dijkstraEdge.getSource().equals(node) && !isSettled(dijkstraEdge.getDestination())) {
                neighbors.add(dijkstraEdge.getDestination());
            }
        }
        Log.d("getNeighbors", neighbors.toString());
        return neighbors;
    }


    /**
     * Get the minimum shortest distance of all the DijkstraNodes.
     * @param dijkstraNodes a set of DijkstraNodes
     * @return the nearest DijkstraNode
     */
    public DijkstraNode getMinimumDistance(Set<DijkstraNode> dijkstraNodes) {
        DijkstraNode minimum = null;
        for (DijkstraNode dijkstraNode : dijkstraNodes) {
            if (minimum == null) {
                minimum = dijkstraNode;
            } else {
                if (getShortestDistance(dijkstraNode) < getShortestDistance(minimum)) {
                    minimum = dijkstraNode;
                }
            }
        }
        return minimum;
    }


    /**
     * @param dijkstraNode the input DijkstraNode
     * @return true if the DijkstraNode was already settled
     */
    public boolean isSettled(DijkstraNode dijkstraNode) {
        return settledNodes.contains(dijkstraNode);
    }


    /**
     * Gets the shortest distance to the destination, from the calculated start-node (called through
     * the method execute).
     * @param destinationNode the destination-node
     * @return the shortest distance
     */
    public double getShortestDistance(DijkstraNode destinationNode) {
        Double d = distance.get(destinationNode);
        if (d == null) {
            return Double.MAX_VALUE;
        } else {
            return d;
        }
    }


    /**
     * This method returns the path from the source to the selected target and
     * NULL if no path exists
     *
     * @param targetSourceId the ID of the target Node
     */
    public LinkedList<Node> getPath(String targetSourceId) {
        LinkedList<Node> path = new LinkedList<>();
        DijkstraNode step = new DijkstraNode(databaseHandler.getNode(targetSourceId));

        // check if a path exists
        if (predecessors.get(step) == null) {
            return null;
        }
        path.add(step.getNode());
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step.getNode());
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }
}