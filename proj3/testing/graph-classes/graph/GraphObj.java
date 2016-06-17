package graph;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.HashSet;

/** A partial implementation of Graph containing elements common to
 *  directed and undirected graphs.
 *
 *  @author Adam Shi
 */

abstract class GraphObj extends Graph {

    /** A new, empty GraphObj. */
    GraphObj() {
        vertices = new TreeMap<Integer, ArrayList<ArrayList<Integer>>>();
        edges = new HashSet<ArrayList<Integer>>();

    }

    @Override
    public int vertexSize() {


        return vertices.size();
    }

    @Override
    public int maxVertex() {
        if (vertices.isEmpty()) {
            return 0;
        }
        int maxV = 0;
        for (int vertex : vertices.keySet()) {
            if (vertex > maxV) {
                maxV = vertex;
            }
        }
        return maxV;
    }

    @Override
    public int edgeSize() {

        return edges.size();
    }

    @Override
    public abstract boolean isDirected();

    @Override
    public int outDegree(int v) {
        if (!mine(v)) {
            return 0;
        }

        ArrayList<ArrayList<Integer>> vConnections = vertices.get(v);

        return vConnections.get(1).size();
    }

    @Override
    public abstract int inDegree(int v);

    @Override
    public boolean contains(int u) {

        return vertices.containsKey(u);
    }

    @Override
    public boolean contains(int u, int v) {


        if (!contains(u) || !contains(u)) {
            return false;
        }

        if (!isDirected()) {
            if (v < u) {
                int temp = u;
                u = v;
                v = temp;
            }
        }

        ArrayList<Integer> tempEdge = new ArrayList<Integer>();
        tempEdge.add(u);
        tempEdge.add(v);
        return edges.contains(tempEdge);
    }

    @Override
    public int add() {

        int currentVertex;
        if (deletedVertices.isEmpty()) {
            currentVertex = vertices.size() + 1;
        } else {
            currentVertex = deletedVertices.pollFirst();
        }

        ArrayList<ArrayList<Integer>> empty
            = new ArrayList<ArrayList<Integer>>();
        empty.add(new ArrayList<Integer>());
        empty.add(new ArrayList<Integer>());

        vertices.put(currentVertex, empty);
        return currentVertex;
    }

    @Override
    public int add(int u, int v) {

        if (!isDirected()) {
            if (v < u) {
                int temp = u;
                u = v;
                v = temp;
            }
        }

        ArrayList<Integer> tempEdge = new ArrayList<Integer>();
        tempEdge.add(u);
        tempEdge.add(v);
        if (!edges.contains(tempEdge)) {
            edges.add(tempEdge);
            ArrayList<Integer> uSuccs = vertices.get(u).get(1);
            ArrayList<Integer> vPreds = vertices.get(v).get(0);
            uSuccs.add(v);
            vPreds.add(u);

            if (!isDirected()) {
                if (u != v) {
                    ArrayList<Integer> vSuccs = vertices.get(v).get(1);
                    ArrayList<Integer> uPreds = vertices.get(u).get(0);
                    vSuccs.add(u);
                    uPreds.add(v);
                }
            }
        }

        return u;
    }

    @Override
    public void remove(int v) {

        vertices.remove(v);
        deletedVertices.add(v);

        ArrayList<ArrayList<Integer>> removals
            = new ArrayList<ArrayList<Integer>>();
        for (ArrayList<Integer> edge : edges) {
            if (edge.contains(v)) {
                removals.add(edge);
            }
        }

        for (ArrayList<Integer> edge : removals) {
            edges.remove(edge);
        }

        for (ArrayList<ArrayList<Integer>> connections : vertices.values()) {
            connections.get(0).remove((Object) v);
            connections.get(1).remove((Object) v);
        }
    }

    @Override
    public void remove(int u, int v) {

        if (!isDirected()) {
            if (v < u) {
                int temp = u;
                u = v;
                v = temp;
            }
        }

        ArrayList<Integer> tempEdge = new ArrayList<Integer>();
        tempEdge.add(u);
        tempEdge.add(v);
        edges.remove(tempEdge);

        ArrayList<ArrayList<Integer>> uConnections = vertices.get(u);
        uConnections.get(1).remove((Object) v);

        ArrayList<ArrayList<Integer>> vConnections = vertices.get(v);
        vConnections.get(0).remove((Object) u);

        if (!isDirected()) {
            uConnections.get(0).remove((Object) v);
            vConnections.get(1).remove((Object) u);
        }
    }

    @Override
    public Iteration<Integer> vertices() {

        return Iteration.iteration(vertices.keySet());
    }

    @Override
    public int successor(int v, int k) {

        if (!vertices.containsKey(v)) {
            return 0;
        }

        ArrayList<Integer> vSuccessors = vertices.get(v).get(1);
        if (k > vSuccessors.size() - 1) {
            return 0;
        } else {
            return vSuccessors.get(k);
        }
    }

    @Override
    public abstract int predecessor(int v, int k);

    @Override
    public Iteration<Integer> successors(int v) {

        if (!vertices.containsKey(v)) {
            ArrayList<Integer> empty = new ArrayList<Integer>();
            return Iteration.iteration(empty);
        }

        return Iteration.iteration(vertices.get(v).get(1));

    }

    @Override
    public abstract Iteration<Integer> predecessors(int v);

    @Override
    public Iteration<int[]> edges() {


        ArrayList<int[]> edgesArray = new ArrayList<int[]>();
        for (ArrayList<Integer> edge : edges) {
            int[] edgeArray = new int[]{edge.get(0), edge.get(1)};
            edgesArray.add(edgeArray);
        }

        return Iteration.iteration(edgesArray);
    }

    @Override
    protected boolean mine(int v) {

        return vertices.containsKey(v);
    }

    @Override
    protected void checkMyVertex(int v) {

        if (!mine(v)) {
            throw  new RuntimeException("vertex is not mine");
        }
    }

    @Override
    protected int edgeId(int u, int v) {
        if (!isDirected()) {
            if (v < u) {
                int temp = u;
                u = v;
                v = temp;
            }
        }
        return v + ((u + v) * (u + v + 1) / 2);
    }

    /** Accessor method for vertices. Returns the Map of vertices. */
    TreeMap<Integer, ArrayList<ArrayList<Integer>>> getVertices() {
        return vertices;
    }

    /** The vertices of this graph and their predecessors/successors. */
    private TreeMap<Integer, ArrayList<ArrayList<Integer>>> vertices;

    /** The edges of this graph. */
    private HashSet<ArrayList<Integer>> edges;

    /** The base for edge numbering. */
    private final int base = 211;

    /** Deleted vertices. */
    private TreeSet<Integer> deletedVertices
        = new TreeSet<Integer>();
}
