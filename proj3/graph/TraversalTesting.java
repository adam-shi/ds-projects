package graph;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import static java.util.Arrays.asList;

/** Unit tests for the Graph class.
 *  @author Adam Shi
 */
public class TraversalTesting {

    class DFTExtension extends DepthFirstTraversal {

        public DFTExtension(Graph G) {
            super(G);
            visitOrder = new ArrayList<Integer>();
            postVisitOrder = new ArrayList<Integer>();
        }

        @Override
        protected boolean visit(int v) {
            visitOrder.add(v);
            return true;
        }

        @Override
        protected boolean postVisit(int v) {
            postVisitOrder.add(v);
            return true;
        }

        @Override
        protected boolean shouldPostVisit(int v) {
            return true;
        }

        public ArrayList<Integer> getVisitOrder() {
            return visitOrder;
        }

        public ArrayList<Integer> getPostVisitOrder() {
            return postVisitOrder;
        }

        private ArrayList<Integer> visitOrder;

        private ArrayList<Integer> postVisitOrder;

        public void go(int start) {
            traverse(start);
        }

    }

    class BFTExtension extends BreadthFirstTraversal {

        public BFTExtension(Graph G) {
            super(G);
            visitOrder = new ArrayList<Integer>();
        }

        @Override
        protected boolean visit(int v) {
            visitOrder.add(v);
            return true;
        }

        public ArrayList<Integer> getVisitOrder() {
            return visitOrder;
        }

        private ArrayList<Integer> visitOrder;

        public void go(int start) {
            traverse(start);
        }

    }

    /** Test for breadth first traversal. */
    @Test
    public void testBFT() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(5, 4);
        g.add(5, 3);
        g.add(4, 1);
        g.add(1, 5);
        g.add(3, 2);
        BFTExtension b = new BFTExtension(g);
        b.go(5);
        try {
            assertEquals(asList(5, 4, 3, 1, 2), b.getVisitOrder());
        } catch (AssertionError a) {
            try {
                assertEquals(asList(5, 3, 4, 1, 2), b.getVisitOrder());
            } catch (AssertionError a1) {
                try {
                    assertEquals(asList(5, 3, 4, 2, 1), b.getVisitOrder());
                } catch (AssertionError a2) {
                    assertEquals(asList(5, 4, 3, 2, 1), b.getVisitOrder());
                }
            }
        }
    }

    /** Test for depth first traversal. */
    @Test
    public void testDFT() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(5, 4);
        g.add(5, 3);
        g.add(4, 1);
        g.add(1, 5);
        g.add(3, 2);
        DFTExtension d = new DFTExtension(g);
        d.go(5);

        try {
            assertEquals(asList(5, 4, 1, 3, 2), d.getVisitOrder());
        } catch (AssertionError a) {
            assertEquals(asList(5, 3, 2, 4, 1), d.getVisitOrder());
        }

        try {
            assertEquals(asList(1, 4, 2, 3, 5), d.getPostVisitOrder());
        } catch (AssertionError a) {
            assertEquals(asList(2, 3, 1, 4, 5), d.getPostVisitOrder());
        }

        DirectedGraph g2 = new DirectedGraph();
        g2.add();
        g2.add();
        g2.add();
        g2.add();
        g2.add();
        g2.add();
        g2.add();
        g2.add();
        g2.add();
        g2.add();
        g2.add(1, 4);
        g2.add(1, 2);
        g2.add(1, 3);
        g2.add(2, 5);
        g2.add(2, 3);
        g2.add(2, 6);
        g2.add(3, 7);
        g2.add(3, 8);
        g2.add(8, 1);
        g2.add(8, 9);
        g2.add(8, 10);
        g2.add(10, 7);
        DFTExtension d2 = new DFTExtension(g2);
        System.out.println("new graph");
        d2.go(1);
    }
}
