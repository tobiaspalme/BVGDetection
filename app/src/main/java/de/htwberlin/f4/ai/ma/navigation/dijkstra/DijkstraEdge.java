package de.htwberlin.f4.ai.ma.navigation.dijkstra;

/**
 * Created by Johann Winter
 */


class DijkstraEdge {

    private final DijkstraVertex source;
    private final DijkstraVertex destination;
    private final double weight;

    public DijkstraEdge(DijkstraVertex source, DijkstraVertex destination, double weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight == 0.0?15000.0:weight;
    }

    public DijkstraVertex getDestination() {
        return destination;
    }

    public DijkstraVertex getSource() {
        return source;
    }
    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return source + " " + destination;
    }


}