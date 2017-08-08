package de.htwberlin.f4.ai.ma.persistence.fingerprint;

import com.google.common.collect.Multimap;

/**
 * Created by Johann Winter
 */

public class RestructedNode {
    String id;
    Multimap<String, Double> restructedSignals;

    public String getId() {
        return this.id;
    }

    public Multimap<String, Double> getRestructedSignals() {
        return this.restructedSignals;
    }



    // TODO: Private setzen?
    public RestructedNode(String id, Multimap<String, Double> restructedSignals) {
        this.id = id;
        this.restructedSignals = restructedSignals;
    }
}