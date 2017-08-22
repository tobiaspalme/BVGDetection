package de.htwberlin.f4.ai.ma.navigation.dijkstra;

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
        this.weight = weight == 0.0?15000.0:weight;
    }

    DijkstraNode getDestination() {
        return destination;
    }

    DijkstraNode getSource() {
        return source;
    }
    double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return source + " " + destination;
    }

}