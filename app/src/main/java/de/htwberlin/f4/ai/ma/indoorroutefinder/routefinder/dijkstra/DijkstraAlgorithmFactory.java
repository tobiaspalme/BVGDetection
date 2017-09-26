package de.htwberlin.f4.ai.ma.indoorroutefinder.routefinder.dijkstra;

import android.content.Context;

/**
 * Created by Johann Winter
 */

public class DijkstraAlgorithmFactory {

    public static DijkstraAlgorithm createInstance(Context context, boolean accessible) {
        return new DijkstraAlgorithmImpl(context, accessible);
    }
}