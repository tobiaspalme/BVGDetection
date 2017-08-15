package de.htwberlin.f4.ai.ma.navigation.dijkstra;

/**
 * Created by Johann Winter
 */


class DijkstraEdge {

    private final DijkstraVertex source;
    private final DijkstraVertex destination;
    private final double weight;

    DijkstraEdge(DijkstraVertex source, DijkstraVertex destination, double weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight == 0.0?15000.0:weight;
    }

    DijkstraVertex getDestination() {
        return destination;
    }

    DijkstraVertex getSource() {
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