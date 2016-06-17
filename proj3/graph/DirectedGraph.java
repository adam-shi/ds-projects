package graph;

/* See restrictions in Graph.java. */

import java.util.TreeMap;
import java.util.ArrayList;

/** Represents a general unlabeled directed graph whose vertices are denoted by
 *  positive integers. Graphs may have self edges.
 *
 *  @author Adam Shi
 */

public class DirectedGraph extends GraphObj {

    @Override
    public boolean isDirected() {
        return true;
    }

    @Override
    public int inDegree(int v) {
        if (!mine(v)) {
            return 0;
        }

        ArrayList<ArrayList<Integer>> vConnections = getVertices().get(v);

        return vConnections.get(0).size();
    }

    @Override
    public int predecessor(int v, int k) {

        TreeMap<Integer, ArrayList<ArrayList<Integer>>> verticesCopy
            = getVertices();

        if (!verticesCopy.containsKey(v)) {
            return 0;
        }

        ArrayList<Integer> vPredecessors = verticesCopy.get(v).get(0);
        if (k > vPredecessors.size() - 1) {
            return 0;
        } else {
            return vPredecessors.get(k);
        }
    }

    @Override
    public Iteration<Integer> predecessors(int v) {

        TreeMap<Integer, ArrayList<ArrayList<Integer>>> verticesCopy
            = getVertices();

        if (!verticesCopy.containsKey(v)) {
            ArrayList<Integer> empty = new ArrayList<Integer>();
            return Iteration.iteration(empty);
        }

        return Iteration.iteration(verticesCopy.get(v).get(0));
    }

}
