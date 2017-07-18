package de.htwberlin.f4.ai.ma.fingerprint_generator.fingerprint;

import com.google.common.collect.Multimap;

/**
 * Created by Johann Winter
 */

class RestructedNode {
    String id;
    Multimap<String, Double> restructedSignals;

    // TODO: Private setzen?
    RestructedNode(String id, Multimap<String, Double> restructedSignals) {
        this.id = id;
        this.restructedSignals = restructedSignals;
    }
}