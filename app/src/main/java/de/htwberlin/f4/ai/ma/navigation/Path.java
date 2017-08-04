package de.htwberlin.f4.ai.ma.navigation;

import java.util.List;

import de.htwberlin.f4.ai.ma.fingerprint_generator.node.Node;

/**
 * Created by Johann Winter
 */

public interface Path {

    // Never ever change that object -> comment

    List<Node> getNodeList();

}
