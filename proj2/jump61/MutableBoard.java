
package jump61;

import static jump61.Side.*;
import static jump61.Square.square;
import java.util.Stack;
import java.util.ArrayList;

/** A Jump61 board state that may be modified.
 *  @author Adam Shi
 */
class MutableBoard extends Board {

    /** An N x N board in initial configuration. */
    MutableBoard(int N) {
        this._board = new Square[N * N];
        this._size = N;
        for (int i = 0; i < N * N; i++) {
            this._board[i] = Square.square(Side.WHITE, 1);
        }
        markUndo();
    }

    /** A board whose initial contents are copied from BOARD0, but whose
     *  undo history is clear. */
    MutableBoard(Board board0) {
        this._board = new Square[board0.size() * board0.size()];
        this._size = board0.size();
        for (int i = 0; i < _size * _size; i++) {
            this._board[i] = board0.get(i);
        }
        markUndo();
    }

    @Override
    void clear(int N) {
        MutableBoard newBoard = new MutableBoard(N);
        this._board = newBoard._board;
        this._size = N;
        markUndo();
        announce();
    }

    @Override
    void copy(Board board) {
        internalCopy((MutableBoard) board);
        markUndo();
    }

    /** Copy the contents of BOARD into me, without modifying my undo
     *  history.  Assumes BOARD and I have the same size. */
    private void internalCopy(MutableBoard board) {
        for (int i = 0; i < _size * _size; i++) {
            this._board[i] = board._board[i];
        }
    }

    @Override
    int size() {
        return _size;
    }

    @Override
    Square get(int n) {

        return _board[n];
    }

    @Override
    int numOfSide(Side side) {
        int count = 0;
        for (int i = 0; i < _board.length; i++) {
            if (_board[i].getSide() == side) {
                count += 1;
            }
        }

        return count;
    }

    @Override
    int numPieces() {
        int countSpots = 0;
        for (int i = 0; i < _board.length; i++) {
            countSpots += _board[i].getSpots();
        }

        return countSpots;
    }

    @Override
    void addSpot(Side player, int r, int c) {


        addSpot(player, sqNum(r, c));
    }

    @Override
    void addSpot(Side player, int n) {

        MutableBoard copyOfCurrent = new MutableBoard(this);
        undoableMoves.push(copyOfCurrent);

        _board[n] = square(player, _board[n].getSpots() + 1);
        ArrayList<Integer> fulls;
        while ((numOfSide(player) != _board.length)
               && !(findOverfull().isEmpty())) {
            fulls = findOverfull();
            for (int i : fulls) {
                propogate(player, i);
            }
        }


        announce();
    }

    /** Assumes square at N is overfull and spreads to its neighbors,
     *  coloring them all with the same color as PLAYER.
     */
    private void propogate(Side player, int n) {
        _board[n] = square(player, _board[n].getSpots() - neighbors(n));
        int tempRow = row(n);
        int tempCol = col(n);


        if (tempCol != 1) {
            _board[n - 1] = square(player, _board[n - 1].getSpots() + 1);
        }
        if (tempCol != _size) {
            _board[n + 1] = square(player, _board[n + 1].getSpots() + 1);
        }
        if (tempRow != 1) {
            _board[n - _size] = square(player,
                                       _board[n - _size].getSpots() + 1);
        }
        if (tempRow != _size) {
            _board[n + _size] = square(player,
                                       _board[n + _size].getSpots() + 1);
        }
    }


    /** Returns true if the square at position N is overfull. */
    private boolean isOverfull(int n) {
        return (_board[n].getSpots() > neighbors(n));
    }

    /** Returns any overfull squares. */
    private ArrayList<Integer> findOverfull() {
        ArrayList<Integer> fulls = new ArrayList<Integer>();
        for (int i = 0; i < _board.length; i++) {
            if (isOverfull(i)) {
                fulls.add(i);
            }
        }
        return fulls;
    }

    @Override
    void set(int r, int c, int num, Side player) {
        internalSet(sqNum(r, c), square(player, num));
    }

    @Override
    void set(int n, int num, Side player) {

        internalSet(n, square(player, num));
        markUndo();
        announce();
    }

    @Override
    void undo() {
        MutableBoard tempBoard = undoableMoves.pop();
        _board = tempBoard._board;

    }

    /** Record the beginning of a move in the undo history. */
    private void markUndo() {
        undoableMoves = new Stack<MutableBoard>();
    }

    /** Set the contents of the square with index IND to SQ. Update counts
     *  of numbers of squares of each color.  */
    private void internalSet(int ind, Square sq) {
        _board[ind] = sq;

    }

    /** Notify all Observers of a change. */
    private void announce() {
        setChanged();
        notifyObservers();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MutableBoard)) {
            return obj.equals(this);
        } else {
            for (int i = 0; i < _board.length; i++) {
                if (((MutableBoard) obj)._board[i] != _board[i]) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public int hashCode() {
        return 0;
    }

    /** The status of the current board, stored as an array of Squares. */
    private Square[] _board;

    /** The dimensions of the square board. */
    private int _size;

    /** The undo history. */
    private Stack<MutableBoard> undoableMoves;
}
