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

public class DijkstraAlgorithm {

    /**
     * Created by tognitos on 22.01.17.
     *
     * Dijkstra algorithm uses maps the common Node and Edge objects to its own DijkstraNode and
     * DijkstraEdge objects in order to avoid loading the model objects with the algorithm's logic.
     * It is important that methods from the Node and Edge classes (of the Model) are avoided.
     * This allows us to change just how the mapping to the Dijkstra's Vertices and Edges work.
     * "Prinzip der lose Kopplung"
     */

        // dijkstraNodes and dijkstraEdges
        private final List<DijkstraNode> dijkstraNodes;
        private final List<DijkstraEdge> dijkstraEdges;

        // settled and unsettled dijkstraNodes (seen / not seen)
        private Set<DijkstraNode> settledNodes;
        private Set<DijkstraNode> unSettledNodes;

        // (smallest) predecessors
        private Map<DijkstraNode, DijkstraNode> predecessors;

        // smallest distance (weight) for the node
        private Map<DijkstraNode, Double> distance;

    private DatabaseHandler databaseHandler;
    private boolean accessible;


    public DijkstraAlgorithm(Context context, boolean accessible) {
        databaseHandler = DatabaseHandlerFactory.getInstance(context);

        this.accessible = accessible;
        this.dijkstraNodes = mapNodes(databaseHandler.getAllNodes());
        this.dijkstraEdges = mapEdges(databaseHandler.getAllEdges());
    }




        /**
         * Map the normal Node objects from the Model to the custom Vertex of the Dijkstra Algorithm.
         * This is done to avoid filling the model objects with logic elements.
         * @param nodes
         * @return
         */
        private List<DijkstraNode> mapNodes(ArrayList<Node> nodes) {
            ArrayList<DijkstraNode> vertices = new ArrayList<>(nodes.size());
            for(Node node : nodes){
                vertices.add(new DijkstraNode(node));
            }
            return vertices;
        }

        // TODO Beschreibung
        /**
         * Map the normal Edge objects from the Graph model to the custom Edge of the Dijkstra
         * algorithm. This is done in order to avoid filling the model objects with logic elements.
         * Since the graph is bidirectional, it maps 2 dijkstraEdges: one for poiA->poiB, one for poiB->poiA
         * @param edges
         * @return
         */
        private List<DijkstraEdge> mapEdges(ArrayList<Edge> edges) {
            ArrayList<DijkstraEdge> dijkstraEdges = new ArrayList<>();

            for (Edge edge : edges) {


                // since it is an undirected graph, add both directions
                DijkstraNode source = new DijkstraNode(edge.getNodeA());
                DijkstraNode destination = new DijkstraNode(edge.getNodeB());

                // If accessible search wanted
                if (accessible) {
                    if (edge.getAccessibility()) {
                        DijkstraEdge sourceToDestination = new DijkstraEdge(source, destination, edge.getWeight());
                        DijkstraEdge destinationToSource = new DijkstraEdge(destination, source, edge.getWeight());
                        dijkstraEdges.add(sourceToDestination);
                        dijkstraEdges.add(destinationToSource);
                    }
                // If accessibility doesn't play a role
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
         * @param sourceNodeId
         * @throws IllegalArgumentException if the sourcenodeid does not exist
         */
        public void execute(String sourceNodeId) throws IllegalArgumentException {
            //final Node sourceNode = graph.getNode(sourceNodeId);
            final Node sourceNode = databaseHandler.getNode(sourceNodeId);

            if(sourceNode == null){
                throw new IllegalArgumentException("Source Node Id is invalid! Given was:" + sourceNodeId);
            }
            final DijkstraNode sourceVertex = new DijkstraNode(databaseHandler.getNode(sourceNodeId));

            settledNodes = new HashSet<>();
            unSettledNodes = new HashSet<>();
            distance = new HashMap<>();
            predecessors = new HashMap<>();
            distance.put(sourceVertex, 0.0);
            unSettledNodes.add(sourceVertex);
            while (unSettledNodes.size() > 0) {
                DijkstraNode node = getMinimum(unSettledNodes);
                settledNodes.add(node);
                unSettledNodes.remove(node);

                Log.d("execute", "node=" + node.getId());

                findMinimalDistances(node);
            }
        }

        /**
         * Find the minimal distance from a node
         * @param node
         */
        private void findMinimalDistances(DijkstraNode node) {
            List<DijkstraNode> adjacentNodes = getNeighbors(node);
            for (DijkstraNode target : adjacentNodes) {

                if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
                    System.out.println("findMinimalDistances. getshortestDistance(target) > getShortestDistance(node) + getDistance(node, target)");
                    distance.put(target, getShortestDistance(node)
                            + getDistance(node, target));
                    predecessors.put(target, node);
                    unSettledNodes.add(target);
                }
            }
        }


        /**
         * Get the distance between the two vertices
         * @param node
         * @param target
         * @return
         */
        private double getDistance(DijkstraNode node, DijkstraNode target) {
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


                // TODO Objekte vergleichen, nicht id's?
                //if (dijkstraEdge.getSource().getNode().equals(node.getNode()) && dijkstraEdge.getDestination().getNode().equals(target.getNode())) {
                if (dijkstraEdge.getSource().getNode().getId().equals(node.getNode().getId()) && dijkstraEdge.getDestination().getNode().getId().equals(target.getNode().getId())) {
                    Log.d("getDistance", "Edge weight: " + dijkstraEdge.getWeight());
                    return dijkstraEdge.getWeight();
                }
            }
            throw new RuntimeException("Should not happen");
        }

        /**
         * Get all the neighbours (directly connected dijkstraNodes) for the specified node.
         * @param node
         * @return
         */
        private List<DijkstraNode> getNeighbors(DijkstraNode node) {
            List<DijkstraNode> neighbors = new ArrayList<>();
            for (DijkstraEdge dijkstraEdge : dijkstraEdges) {
                if (dijkstraEdge.getSource().equals(node)
                        && !isSettled(dijkstraEdge.getDestination())) {
                    neighbors.add(dijkstraEdge.getDestination());
                }
            }
            Log.d("getNeighbors", neighbors.toString());
            return neighbors;
        }

        /**
         * Get the minimum shortest distance of all the vertices.
         * @param dijkstraVertices
         * @return
         */
        private DijkstraNode getMinimum(Set<DijkstraNode> dijkstraVertices) {
            DijkstraNode minimum = null;
            for (DijkstraNode dijkstraNode : dijkstraVertices) {
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
         *
         * @param dijkstraNode
         * @return true if the vertex was already settled
         */
        private boolean isSettled(DijkstraNode dijkstraNode) {
            return settledNodes.contains(dijkstraNode);
        }

        /**
         * Gets the shortest distance to the destination, from the calculated start node (called through
         * the method execute)
         * @param destinationVertex
         * @return
         */
        private double getShortestDistance(DijkstraNode destinationVertex) {
            Double d = distance.get(destinationVertex);
            if (d == null) {
                return Double.MAX_VALUE;
            } else {
                return d;
            }
        }

        /**
         * This method returns the path from the source to the selected target and
         * NULL if no path exists
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
