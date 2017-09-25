package de.htwberlin.f4.ai.ma.indoorroutefinder.routefinder.dijkstra;

/**
 * Created by Johann Winter
 */


class DijkstraEdge {

    private final DijkstraNode source;
    private final DijkstraNode destination;
    private final double weight;

    DijkstraEdge(DijkstraNode source, DijkstraNode destination, double weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
        //this.weight = weight == 0.0 ? 15000.0:weight;
    }

    /**
     * Getter for the destination DijkstraNode
     * @return the destination DijkstraNode
     */
    DijkstraNode getDestination() {
        return destination;
    }

    /**
     * Getter for the source DijkstraNode
     * @return the source DijkstraNode
     */
    DijkstraNode getSource() {
        return source;
    }

    /**
     * Getter for the weight (length) of the DijkstraEdge
     * @return
     */
    double getWeight() {
        return weight;
    }

    /**
     * Overrides standard toString() method
     * @return
     */
    @Override
    public String toString() {
        return source + " " + destination;
    }

}