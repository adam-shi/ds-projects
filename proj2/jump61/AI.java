
package jump61;

import java.util.ArrayList;

/** An automated Player.
 *  @author Adam Shi
 */
class AI extends Player {

    /** Time allotted to all but final search depth (milliseconds). */
    private static final long TIME_LIMIT = 15000;

    /** Number of calls to minmax between checks of elapsed time. */
    private static final long TIME_CHECK_INTERVAL = 10000;

    /** Number of milliseconds in one second. */
    private static final double MILLIS = 1000.0;

    /** A new player of GAME initially playing COLOR that chooses
     *  moves automatically.
     */
    AI(Game game, Side color) {
        super(game, color);
    }

    @Override
    void makeMove() {
        ArrayList<Integer> legalMoves = findMoves(getSide(),
                                                  getGame().getBoard());
        minmax(getSide(), getGame().getBoard(), 3, Integer.MIN_VALUE + 1,
               legalMoves);
        int selectedMove = legalMoves.get(getGame().randInt(legalMoves.size()));
        getGame().makeMove(selectedMove);
        getGame().reportMove(getSide(), getGame().getBoard().row(selectedMove),
                             getGame().getBoard().col(selectedMove));

    }

    /** Return the minimum of CUTOFF and the minmax value of board B
     *  (which must be mutable) for player P to a search depth of D
     *  (where D == 0 denotes statically evaluating just the next move).
     *  If MOVES is not null and CUTOFF is not exceeded, set MOVES to
     *  a list of all highest-scoring moves for P; clear it if
     *  non-null and CUTOFF is exceeded. the contents of B are
     *  invariant over this call. */
    private int minmax(Side p, Board b, int d, int cutoff,
                       ArrayList<Integer> moves) {

        if (b.numOfSide(p) == b.size() * b.size()) {
            return Integer.MAX_VALUE - 1;
        } else if (b.numOfSide(p.opposite()) == b.size() * b.size()) {
            return Integer.MIN_VALUE + 1;
        }
        int bestMoveResult = cutoff;
        MutableBoard mb = new MutableBoard(b);
        ArrayList<Integer> bestMoves = new ArrayList<Integer>();
        boolean pruned = false;
        if (d == 0) {
            for (int m : moves) {
                mb.addSpot(p, m);
                if (-staticEval(p, mb) < cutoff) {
                    mb.undo();
                    pruned = true;
                    break;
                }

                if (staticEval(p, mb) > bestMoveResult) {

                    bestMoveResult = staticEval(p, mb);
                    bestMoves.clear();
                    bestMoves.add(m);
                } else if (staticEval(p, mb) == bestMoveResult) {
                    bestMoves.add(m);
                }

                mb.undo();
            }
        } else {
            int temp;
            for (int m: moves) {
                mb.addSpot(p, m);
                temp = -minmax(p.opposite(), mb, d - 1, bestMoveResult,
                               findMoves(p.opposite(), mb));
                if (temp > bestMoveResult) {
                    bestMoveResult = temp;
                    bestMoves.clear();
                    bestMoves.add(m);
                } else if (!moves.isEmpty() && temp == bestMoveResult) {
                    bestMoves.add(m);
                }
                mb.undo();
            }

        }

        moves.clear();
        if (!pruned) {
            for (int best : bestMoves) {
                moves.add(best);
            }
        }
        return bestMoveResult;
    }

    /** Returns all legal moves for a certain board B and player P. */
    private ArrayList<Integer> findMoves(Side p, Board b) {
        ArrayList<Integer> legalMoves = new ArrayList<Integer>();
        for (int i = 0; i < b.size() * b.size(); i++) {
            if (b.isLegal(p, i)) {
                legalMoves.add(i);
            }
        }
        return legalMoves;
    }

    /** Returns heuristic value of board B for player P.
     *  Higher is better for P. */
    private int staticEval(Side p, Board b) {
        return b.numOfSide(p) - b.numOfSide(p.opposite());
    }

}
