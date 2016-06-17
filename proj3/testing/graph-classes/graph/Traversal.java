package graph;

/* See restrictions in Graph.java. */

import java.util.Arrays;
import java.util.Collection;
import java.util.Queue;
import java.util.ArrayList;
import java.util.Stack;

/** Implements a generalized traversal of a graph.  At any given time,
 *  there is a particular collection of untraversed vertices---the "fringe."
 *  Traversal consists of repeatedly removing an untraversed vertex
 *  from the fringe, visting it, and then adding its untraversed
 *  successors to the fringe.
 *
 *  Generally, the client will extend Traversal.  By overriding the visit
 *  method, the client can determine what happens when a node is visited.
 *  By supplying an appropriate type of Queue object to the constructor,
 *  the client can control the behavior of the fringe. By overriding the
 *  shouldPostVisit and postVisit methods, the client can arrange for
 *  post-visits of a node (as in depth-first search).  By overriding
 *  the reverseSuccessors and processSuccessor methods, the client can control
 *  the addition of neighbor vertices to the fringe when a vertex is visited.
 *
 *  Traversals may be interrupted or restarted, remembering the previously
 *  marked vertices.
 *  @author Adam Shi
 */
public abstract class Traversal {

    /** A Traversal of G, using FRINGE as the fringe. */
    protected Traversal(Graph G, Queue<Integer> fringe) {
        _G = G;
        _fringe = fringe;
        _visited = new ArrayList<Integer>();
        _visitedStack = new Stack<Integer>();
    }

    /** Unmark all vertices in the graph. */
    public void clear() {

        _visited.clear();
    }

    /** Initialize the fringe to V0 and perform a traversal. */
    public void traverse(Collection<Integer> V0) {

        for (int v : V0) {
            _fringe.add(v);
        }
        int toVisit;
        int successorIndex;
        int temp;

        while (!_fringe.isEmpty()) {
            toVisit = _fringe.remove();

            if (!marked(toVisit)) {
                if (!visit(toVisit)) {
                    break;
                }
                mark(toVisit);
                _visitedStack.push(toVisit);

                Iteration<Integer> currentSuccessors = _G.successors(toVisit);

                temp = _visitedStack.peek();
                while (allSuccessorsVisited(temp) && !_visitedStack.empty()) {
                    _visitedStack.pop();
                    if (shouldPostVisit(temp)) {
                        postVisit(temp);
                    }
                    if (!_visitedStack.empty()) {
                        temp = _visitedStack.peek();
                    }
                }

                if (reverseSuccessors(toVisit)) {
                    ArrayList<Integer> reversal = new ArrayList<Integer>();
                    for (int s : currentSuccessors) {
                        reversal.add(0, s);
                    }
                    currentSuccessors = Iteration.iteration(reversal);
                }

                for (int s : currentSuccessors) {
                    if (processSuccessor(toVisit, s)) {
                        _fringe.add(s);
                    }
                }
            }
        }
    }

    /** Initialize the fringe to { V0 } and perform a traversal. */
    public void traverse(int v0) {
        traverse(Arrays.<Integer>asList(v0));
    }

    /** Returns true iff V has been marked. */
    protected boolean marked(int v) {
        return _visited.contains(v);

    }

    /** Mark vertex V. */
    protected void mark(int v) {

        _visited.add(v);

    }

    /** Perform a visit on vertex V.  Returns false iff the traversal is to
     *  terminate immediately. */
    protected boolean visit(int v) {
        return true;
    }

    /** Return true if we should postVisit V after traversing its
     *  successors.  (Post-visiting generally is useful only for depth-first
     *  traversals, although we define it for all traversals.) */
    protected boolean shouldPostVisit(int v) {
        return true;
    }

    /** Revisit vertex V after traversing its successors.  Returns false iff
     *  the traversal is to terminate immediately. */
    protected boolean postVisit(int v) {
        return true;
    }

    /** Return true if we should schedule successors of V in reverse order. */
    protected boolean reverseSuccessors(int v) {
        return false;
    }

    /** Process successor V to U.  Returns true iff V is then to
     *  be added to the fringe.  By default, returns true iff V is unmarked. */
    protected boolean processSuccessor(int u, int v) {
        return !marked(v);
    }

    /** Returns true if all successors of V have been visited, false
     *  otherwise. */
    private boolean allSuccessorsVisited(int v) {
        for (int s : _G.successors(v)) {
            if (!marked(s)) {
                return false;
            }
        }
        return true;
    }

    /** The graph being traversed. */
    private final Graph _G;
    /** The fringe. */
    protected Queue<Integer> _fringe;

    /** Already visited vertices. */
    private ArrayList<Integer> _visited;

    /** Visited vertices in order of visitation. */
    private Stack<Integer> _visitedStack;

}
