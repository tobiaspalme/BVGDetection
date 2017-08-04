package de.htwberlin.f4.ai.ma.indoor_graph;

/**
 * Created by Johann Winter
 */

public final class IndoorGraphFactory {

    private static IndoorGraph instance;

    public static IndoorGraph createInstance() {
        if (instance == null) {
            instance = new IndoorGraphImplementation();
        }
        return instance;
    }
}