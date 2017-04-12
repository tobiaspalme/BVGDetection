package com.example.carol.bvg;

import com.google.common.collect.Multimap;

/**
 * a class to restruct the nodes in a multimap
 */
public class RestructedNode {
    String id;
    Multimap<String, Integer> restructedSignals;

    public RestructedNode(String id, Multimap<String, Integer> restructedSignals) {
        this.id = id;
        this.restructedSignals = restructedSignals;
    }
}
