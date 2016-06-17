package graph;

import org.junit.Test;
import static org.junit.Assert.*;
import static java.util.Arrays.asList;

/** Unit tests for the Graph class.
 *  @author Adam Shi
 */
public class PathsTesting {

    class VideoGraphSP extends SimpleShortestPaths {
        public VideoGraphSP(Graph G, int source, int dest) {
            super(G, source, dest);
        }

        @Override
        protected double estimatedDistance(int v) {
            if (v == 3) {
                return 0;
            } else if (v == 2) {
                return 4;
            } else if (v == 4) {
                return 102;
            } else if (v == 5) {
                return 5.1;
            } else if (v == 6) {
                return 40;
            }
            return 0;
        }

        @Override
        protected double getWeight(int u, int v) {
            if (u == 2 & v == 3) {
                return 6.5;
            } else if (u == 4 & v == 2) {
                return 12.2;
            } else if (u == 4 & v == 3) {
                return 102;
            } else if (u == 4 & v == 5) {
                return 11.2;
            } else if (u == 5 & v == 3) {
                return 9.1;
            } else if (u == 5 & v == 6) {
                return 30;
            }
            return 0;
        }
    }

    /** Test for stuff. */
    @Test
    public void testVideo() {
        DirectedGraph g = new DirectedGraph();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add();
        g.add();
        g.remove(1);
        g.add(2, 3);
        g.add(4, 2);
        g.add(4, 3);
        g.add(4, 5);
        g.add(5, 3);
        g.add(5, 6);

        VideoGraphSP v = new VideoGraphSP(g, 4, 3);
        v.setPaths();
        assertEquals(asList(4, 2, 3), v.pathTo(3));

    }

}
