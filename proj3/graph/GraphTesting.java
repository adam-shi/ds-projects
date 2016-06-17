package graph;

import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests for the Graph class.
 *  @author Adam Shi
 */
public class GraphTesting {

    /** Test for an empty graph. */
    @Test
    public void emptyGraph() {
        DirectedGraph g = new DirectedGraph();
        assertEquals("Initial graph has vertices", 0, g.vertexSize());
        assertEquals("Initial graph has edges", 0, g.edgeSize());
    }

    /** Tests basic functions of directed graph. */
    @Test
    public void testDirectedGraph() {
        DirectedGraph g = new DirectedGraph();
        assertEquals(1, g.add());
        assertEquals(2, g.add());
        assertEquals(2, g.maxVertex());
        assertEquals(2, g.vertexSize());

        g.add(1, 2);
        assertEquals(1, g.edgeSize());
        assertTrue(g.contains(1, 2));
        assertTrue(g.contains(1));

        g.remove(1);
        assertEquals(1, g.vertexSize());
        assertEquals(2, g.maxVertex());
        assertTrue(!g.contains(1));

        assertEquals(1, g.add());
    }

    /** Tests other functions of directed graph. */
    @Test
    public void testDirectedGraphOther() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add(1, 2);
        assertTrue(g.contains(1, 2));
        g.remove(2, 1);
        assertTrue(g.contains(1, 2));
        g.remove(1, 2);
        assertTrue(!g.contains(1, 2));

        DirectedGraph d = new DirectedGraph();
        d.add();
        d.add();
        d.add();
        d.add();
        d.add(1, 4);
        d.add(1, 1);
        d.remove(1, 1);
        d.add(1, 1);
        d.remove(1);

        d.add(2, 3);
        d.add(3, 2);
        assertEquals(18, d.edgeId(2, 3));
        assertEquals(17, d.edgeId(3, 2));
    }

    /** Tests basic functions of undirected graph. */
    @Test
    public void testUndirectedGraph() {
        UndirectedGraph u = new UndirectedGraph();
        u.add();
        u.add();
        u.add();
        u.add();
        u.add();
        u.remove(2);
        u.remove(4);
        assertEquals(2, u.add());
        assertEquals(4, u.add());
        assertEquals(6, u.add());
        u.add(2, 3);
        u.add(3, 2);
        assertEquals(18, u.edgeId(2, 3));
        assertEquals(18, u.edgeId(3, 2));
    }

}
