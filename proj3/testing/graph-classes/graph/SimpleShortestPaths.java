package graph;

/* See restrictions in Graph.java. */

import java.util.HashMap;

/** A partial implementation of ShortestPaths that contains the weights of
 *  the vertices and the predecessor edges.   The client needs to
 *  supply only the two-argument getWeight method.
 *  @author Adam Shi
 */
public abstract class SimpleShortestPaths extends ShortestPaths {

    /** The shortest paths in G from SOURCE. */
    public SimpleShortestPaths(Graph G, int source) {
        this(G, source, 0);
    }

    /** A shortest path in G from SOURCE to DEST. */
    public SimpleShortestPaths(Graph G, int source, int dest) {
        super(G, source, dest);
        vertexWeights = new HashMap<Integer, Double>();

        predecessors = new HashMap<Integer, Integer>();

        for (int v : _G.vertices()) {
            vertexWeights.put(v, Double.POSITIVE_INFINITY);
            predecessors.put(v, 0);
        }
    }

    @Override
    public double getWeight(int v) {

        if (!vertexWeights.containsKey(v)) {
            return Double.POSITIVE_INFINITY;
        }

        return vertexWeights.get(v);
    }

    @Override
    protected void setWeight(int v, double w) {

        vertexWeights.put(v, w);
    }

    @Override
    public int getPredecessor(int v) {
        if (!predecessors.containsKey(v)) {
            return 0;
        }
        return predecessors.get(v);
    }

    @Override
    protected void setPredecessor(int v, int u) {

        predecessors.put(v, u);
    }

    /** The weights of each edge. */
    private HashMap<Integer, Double> vertexWeights;


    /** The predecessor of each vertex. */
    private HashMap<Integer, Integer> predecessors;


}
