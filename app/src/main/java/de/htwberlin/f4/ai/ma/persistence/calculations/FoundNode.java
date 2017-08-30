package de.htwberlin.f4.ai.ma.persistence.calculations;

/**
 * Created by Johann Winter
 */

public class FoundNode {
    private String id;
    private double percent;

    public FoundNode(String id, double percent) {
        this.id = id;
        this.percent = percent;
    }

    public String getId() {
        return this.id;
    }
    public double getPercent() {
        return this.percent;
    }
}
