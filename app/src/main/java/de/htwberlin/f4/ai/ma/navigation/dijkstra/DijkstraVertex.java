package de.htwberlin.f4.ai.ma.navigation.dijkstra;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;

/**
 * Created by Johann Winter
 */

class DijkstraVertex {

    private final Node node;
    private final String id;

    public DijkstraVertex(Node node) {
        this.node = node;
        this.id = node.getId();
    }
    public String getId() {
        return id;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DijkstraVertex other = (DijkstraVertex) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return id;
    }

    public Node getNode() {
        return node;
    }
}