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
import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandler;
import de.htwberlin.f4.ai.ma.persistence.DatabaseHandlerImplementation;

/**
 * Created by Johann Winter
 */

public class DijkstraAlgorithm {

    /**
     * Created by tognitos on 22.01.17.
     *
     * Dijkstra algorithm uses maps the common Node and Edge objects to its own DijkstraVertex and
     * DijkstraEdge objects in order to avoid loading the model objects with the algorithm's logic.
     * It is important that methods from the Node and Edge classes (of the Model) are avoided.
     * This allows us to change just how the mapping to the Dijkstra's Vertices and Edges work.
     * "Prinzip der lose Kopplung"
     */

        // dijkstraNodes and dijkstraEdges
        private final List<DijkstraVertex> dijkstraNodes;
        private final List<DijkstraEdge> dijkstraEdges;
        // graph reference
       // private final Graph graph;

        // settled and unsettled dijkstraNodes (seen / not seen)
        private Set<DijkstraVertex> settledNodes;
        private Set<DijkstraVertex> unSettledNodes;

        // (smallest) predecessors
        private Map<DijkstraVertex, DijkstraVertex> predecessors;

        // smallest distance (weight) for the node
        private Map<DijkstraVertex, Double> distance;


        private DatabaseHandler databaseHandler;

        /*
        public DijkstraAlgorithm(Graph graph) {
            this.graph = graph;

            // create a copy of the array so that we can operate on this array
            this.dijkstraNodes = mapNodes(graph.getNodes());
            this.dijkstraEdges = mapEdges(graph.getEdges());
        }*/

        public DijkstraAlgorithm(Context context) {
            databaseHandler = new DatabaseHandlerImplementation(context);

            this.dijkstraNodes = mapNodes(databaseHandler.getAllNodes());
            this.dijkstraEdges = mapEdges(databaseHandler.getAllEdges());
        }




        /**
         * Map the normal Node objects from the Model to the custom Vertex of the Dijkstra Algorithm.
         * This is done to avoid filling the model objects with logic elements.
         * @param nodes
         * @return
         */
        private List<DijkstraVertex> mapNodes(ArrayList<Node> nodes) {
            ArrayList<DijkstraVertex> vertices = new ArrayList<>(nodes.size());
            for(Node node : nodes){
                vertices.add(new DijkstraVertex(node));
            }
            return vertices;
        }

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
                DijkstraVertex source = new DijkstraVertex(edge.getNodeA());
                DijkstraVertex destination = new DijkstraVertex(edge.getNodeB());

                DijkstraEdge sourceToDestination = new DijkstraEdge(source, destination, edge.getWeight());
                DijkstraEdge destinationToSource = new DijkstraEdge(destination, source, edge.getWeight());

                dijkstraEdges.add(sourceToDestination);
                dijkstraEdges.add(destinationToSource);
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
//            final DijkstraVertex sourceVertex = new DijkstraVertex(graph.getNode(sourceNodeId));
            final DijkstraVertex sourceVertex = new DijkstraVertex(databaseHandler.getNode(sourceNodeId));

            settledNodes = new HashSet<>();
            unSettledNodes = new HashSet<>();
            distance = new HashMap<>();
            predecessors = new HashMap<>();
            distance.put(sourceVertex, 0.0);
            unSettledNodes.add(sourceVertex);
            while (unSettledNodes.size() > 0) {
                DijkstraVertex node = getMinimum(unSettledNodes);
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
        private void findMinimalDistances(DijkstraVertex node) {
            List<DijkstraVertex> adjacentNodes = getNeighbors(node);
            for (DijkstraVertex target : adjacentNodes) {

                /*
                Log.d("findMinimalDistances", "target=" + target.getId());
                Log.d("findMinimalDistances", "#####getShortestDistance(target)= " + getShortestDistance(target));
                Log.d("findMinimalDistances", "#####getShortestDistance(node)= " + getShortestDistance(node));
                Log.d("findMinimalDistances", "#####getDistance(node, target)= " + getDistance(node, target));
*/

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
        private double getDistance(DijkstraVertex node, DijkstraVertex target) {
            Log.d("getDistance", "------- GET DISTANCE ---------");
            Log.d("getDistance", "node=" + node.getId());
            Log.d("getDistance", "target=" + target.getId());


            for (DijkstraEdge dijkstraEdge : dijkstraEdges) {
                //Log.d("getDistance", "dijkstraedge.toString:" + dijkstraEdge.toString());


                //Log.d("getDistance", "dijkstraEdge.getSource().getNode().equals(node.getNode()).   " + dijkstraEdge.getSource().getNode().equals(node.getNode()));
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
        private List<DijkstraVertex> getNeighbors(DijkstraVertex node) {
            List<DijkstraVertex> neighbors = new ArrayList<>();
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
        private DijkstraVertex getMinimum(Set<DijkstraVertex> dijkstraVertices) {
            DijkstraVertex minimum = null;
            for (DijkstraVertex dijkstraVertex : dijkstraVertices) {
                if (minimum == null) {
                    minimum = dijkstraVertex;
                } else {
                    if (getShortestDistance(dijkstraVertex) < getShortestDistance(minimum)) {
                        minimum = dijkstraVertex;
                    }
                }
            }
            return minimum;
        }

        /**
         *
         * @param dijkstraVertex
         * @return true if the vertex was already settled
         */
        private boolean isSettled(DijkstraVertex dijkstraVertex) {
            return settledNodes.contains(dijkstraVertex);
        }

        /**
         * Gets the shortest distance to the destination, from the calculated start node (called through
         * the method execute)
         * @param destinationVertex
         * @return
         */
        private double getShortestDistance(DijkstraVertex destinationVertex) {
            Double d = distance.get(destinationVertex);
            Log.d("getShortestDistance", "distance.get(destinationvertex): " + distance.get(destinationVertex));
            if (d == null) {
                //Log.d("getShortestDistance" , "return Double.MAX_VALUE");
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
            Log.d("getPath", "GET PATH CALLED");

            LinkedList<Node> path = new LinkedList<>();

            //DijkstraVertex step = new DijkstraVertex(graph.getNode(targetSourceId));
            DijkstraVertex step = new DijkstraVertex(databaseHandler.getNode(targetSourceId));

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
