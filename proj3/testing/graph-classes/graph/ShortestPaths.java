package graph;

/* See restrictions in Graph.java. */

import java.util.List;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.ArrayList;

/** The shortest paths through an edge-weighted labeled graph of type GRAPHTYPE.
 *  By overrriding methods getWeight, setWeight, getPredecessor, and
 *  setPredecessor, the client can determine how to get parameters of the
 *  search and to return results.  By overriding estimatedDistance, clients
 *  can search for paths to specific destinations using A* search.
 *  @author Adam Shi
 */
public abstract class ShortestPaths {

    /** The shortest paths in G from SOURCE. */
    public ShortestPaths(Graph G, int source) {
        this(G, source, 0);
    }

    /** A shortest path in G from SOURCE to DEST. */
    public ShortestPaths(Graph G, int source, int dest) {
        _G = G;
        _source = source;
        _dest = dest;

    }

    /** Initialize the shortest paths.  Must be called before using
     *  getWeight, getPredecessor, and pathTo. */
    public void setPaths() {


        ArrayList<Integer> vertices = new ArrayList<Integer>();
        for (int v : _G.vertices()) {
            vertices.add(v);
            if (v == _source) {
                setWeight(v, 0);
            } else {
                setWeight(v, Double.POSITIVE_INFINITY);
            }
        }

        AStarTraversal A = new AStarTraversal(_G);
        A.traverse(vertices);

    }

    /** Returns the starting vertex. */
    public int getSource() {

        return _source;
    }

    /** Returns the target vertex, or 0 if there is none. */
    public int getDest() {

        return _dest;
    }

    /** Returns the current weight of vertex V in the graph.  If V is
     *  not in the graph, returns positive infinity. */
    public abstract double getWeight(int v);

    /** Set getWeight(V) to W. Assumes V is in the graph. */
    protected abstract void setWeight(int v, double w);

    /** Returns the current predecessor vertex of vertex V in the graph, or 0 if
     *  V is not in the graph or has no predecessor. */
    public abstract int getPredecessor(int v);

    /** Set getPredecessor(V) to U. */
    protected abstract void setPredecessor(int v, int u);

    /** Returns an estimated heuristic weight of the shortest path from vertex
     *  V to the destination vertex (if any).  This is assumed to be less
     *  than the actual weight, and is 0 by default. */
    protected double estimatedDistance(int v) {
        return 0.0;
    }

    /** Returns the current weight of edge (U, V) in the graph.  If (U, V) is
     *  not in the graph, returns positive infinity. */
    protected abstract double getWeight(int u, int v);

    /** Returns a list of vertices starting at _source and ending
     *  at V that represents a shortest path to V.  Invalid if there is a
     *  destination vertex other than V. */
    public List<Integer> pathTo(int v) {

        setPaths();
        ArrayList<Integer> path = new ArrayList<Integer>();
        int currentVertex = v;
        while (getPredecessor(currentVertex) != 0) {
            path.add(0, currentVertex);
            currentVertex = getPredecessor(currentVertex);
        }

        path.add(0, currentVertex);

        return path;
    }

    /** Returns a list of vertices starting at the source and ending at the
     *  destination vertex. Invalid if the destination is not specified. */
    public List<Integer> pathTo() {
        return pathTo(getDest());
    }

    /** The graph being searched. */
    protected final Graph _G;
    /** The starting vertex. */
    private final int _source;
    /** The target vertex. */
    private final int _dest;

    /** Initial capacity of a queue. */
    private final int initialCapacity = 11;

    /** A* traversal. */
    class AStarTraversal extends Traversal {

        /** Constructor for traversal of G. */
        protected AStarTraversal(Graph G) {
            super(G, null);
            QueueOrdering<Integer>  Q = new QueueOrdering<Integer>();
            _fringe = new PriorityQueue<Integer>(initialCapacity, Q);
        }

        @Override
        protected boolean visit(int v) {
            if (v == _dest) {
                return false;
            }

            double tempEdgeWeight;
            for (int w : _G.successors(v)) {
                tempEdgeWeight = getWeight(v, w);
                if (getWeight(w) > getWeight(v) + tempEdgeWeight) {
                    setWeight(w, getWeight(v) + tempEdgeWeight);
                    setPredecessor(w, v);
                    _fringe.remove(w);
                    _fringe.add(w);
                }
            }
            return true;
        }

        @Override
        protected boolean processSuccessor(int u, int v) {
            return false;
        }

    }

    /** Comparator combining distance and heuristic. */
    class QueueOrdering<T> implements Comparator<T> {

        /** Constructor. */
        public QueueOrdering() {

        }

        /** For priority queue, returns -1 if V1 should be before V2 in
         *  in the queue, 0 for equal priority, and 1 for after.  */
        @Override
        public int compare(T v1, T v2) {

            double val1 = getWeight((Integer) v1)
                + estimatedDistance((Integer) v1);
            double val2 = getWeight((Integer) v2)
                + estimatedDistance((Integer) v2);
            if (val1 < val2) {
                return -1;
            } else if (val2 < val1) {
                return 1;
            } else {
                return 0;
            }
        }

        /** Required for comparator but not used. Return false or any O. */
        @Override
        public boolean equals(Object o) {
            return false;
        }

        /** Style checker. Returns 0. */
        @Override
        public int hashCode() {
            return 0;
        }
    }

}
