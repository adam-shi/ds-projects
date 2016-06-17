package game2048;

import ucb.util.CommandArgs;

import game2048.gui.Game;
import static game2048.Main.Side.*;

/** The main class for the 2048 game.
 *  @author Adam Shi
 */
public class Main {

    /** Maximum possible score. */
    static final int MAX_SCORE = 2048;

    /** Size of the board: number of rows and of columns. */
    static final int SIZE = 4;
    /** Number of squares on the board. */
    static final int SQUARES = SIZE * SIZE;

    /** Symbolic names for the four sides of a board. */
    static enum Side { NORTH, EAST, SOUTH, WEST };

    /** The main program.  ARGS may contain the options --seed=NUM,
     *  (random seed); --log (record moves and random tiles
     *  selected.); --testing (take random tiles and moves from
     *  standard input); and --no-display. */
    public static void main(String... args) {
        CommandArgs options =
            new CommandArgs("--seed=(\\d+) --log --testing --no-display",
                            args);
        if (!options.ok()) {
            System.err.println("Usage: java game2048.Main [ --seed=NUM ] "
                               + "[ --log ] [ --testing ] [ --no-display ]");
            System.exit(1);
        }

        Main game = new Main(options);

        while (game.play()) {
            /* No action */
        }

        System.exit(0);
    }

    /** A new Main object using OPTIONS as options (as for main). */
    Main(CommandArgs options) {
        boolean log = options.contains("--log"),
            display = !options.contains("--no-display");
        long seed = !options.contains("--seed") ? 0 : options.getLong("--seed");
        _testing = options.contains("--testing");
        _game = new Game("2048", SIZE, seed, log, display, _testing);
    }

    /** Reset the score for the current game to 0 and clear the board. */
    void clear() {
        _score = 0;
        _count = 0;
        _game.clear();
        _game.setScore(_score, _maxScore);
        for (int r = 0; r < SIZE; r += 1) {
            for (int c = 0; c < SIZE; c += 1) {
                _board[r][c] = 0;
            }
        }
    }

    /** Play one game of 2048, updating the maximum score. Return true
     *  iff play should continue with another game, or false to exit. */
    boolean play() {
        _score = 0;
        setRandomPiece();
        while (true) {
            if (!gameOver()) {
                setRandomPiece();
            }
            if (gameOver()) {
                if (_score > _maxScore) {
                    _maxScore = _score;
                    _game.setScore(_score, _maxScore);
                }
                _game.endGame();
            }

        GetMove:
            while (true) {
                String key = _game.readKey();
                switch (key) {
                case "Up": case "Down": case "Left": case "Right":
                    if (!gameOver() && tiltBoard(keyToSide(key))) {
                        break GetMove;
                    }
                    if (gameOver()) {
                        break GetMove;
                    }
                    break;
                case "New Game":
                    _game.clear();
                    clear();
                    return true;
                case "Quit":
                    return false;
                default:
                    break;
                }
            }
        }

    }

    /** Return true iff the current game is over (no more moves
     *  possible). */

    boolean gameOver() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (_board[r][c] == MAX_SCORE) {
                    return true;
                }
            }
        }

        if (_count < SQUARES) {
            return false;
        }

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (r > 0 && c > 0 && r < 3 && c < 3) {
                    if (_board[r][c] == _board[r - 1][c] || _board[r][c]
                        == _board[r + 1][c] || _board[r][c] == _board[r][c - 1]
                        || _board[r][c] == _board[r][c + 1]) {
                        return false;
                    }
                }
                if ((r == 0 | r == 3) && (c == 1 | c == 2)) {
                    if (_board[r][c] == _board[r][c - 1]
                        || _board[r][c] == _board[r][c + 1]) {
                        return false;
                    }
                }
                if ((c == 0 | c == 3) && (r == 1 | r == 2)) {
                    if (_board[r][c] == _board[r - 1][c]
                        || _board[r][c] == _board[r + 1][c]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /** Add a tile to a random, empty position, choosing a value (2 or
     *  4) at random.  Has no effect if the board is currently full. */

    void setRandomPiece() {
        if (_count == SQUARES) {
            return;
        }
        int[] randomTile = new int[3];
        randomTile = _game.getRandomTile();
        while (_board[randomTile[1]][randomTile[2]] != 0) {
            randomTile = _game.getRandomTile();
        }
        _game.addTile(randomTile[0], randomTile[1], randomTile[2]);
        _board[randomTile[1]][randomTile[2]] = randomTile[0];
        _count += 1;
    }

    /** Perform the result of tilting the board toward SIDE.
     *  Returns true iff the tilt changes the board. **/

    boolean tiltBoard(Side side) {
        int[][] board = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r += 1) {
            for (int c = 0; c < SIZE; c += 1) {
                board[r][c] =
                    _board[tiltRow(side, r, c)][tiltCol(side, r, c)];
            }
        }
        boolean change = false;
        for (int r = 0; r < SIZE; r += 1) {
            for (int c = 0; c < SIZE; c += 1) {
                int tempRow = r;
                boolean isMerge = false;
                boolean isMove = false;
                if (board[tempRow][c] != 0) {
                    while (tempRow > 0 && board[tempRow - 1][c] == 0) {
                        tempRow = tempRow - 1;
                        change = true;
                        isMove = true;
                    }
                    if (tempRow > 0 && board[tempRow - 1][c] == board[r][c]) {
                        isMerge = true;
                        change = true;
                    }
                    if (isMerge) {
                        _game.mergeTile(board[r][c], 2 * board[r][c],
                                        tiltRow(side, r, c),
                                        tiltCol(side, r, c),
                                        tiltRow(side, tempRow - 1, c),
                                        tiltCol(side, tempRow - 1, c));
                        int mergeScore = 2 * board[r][c];
                        board[tempRow - 1][c] = 2 * board[r][c] + 1;
                        board[r][c] = 0;
                        _count -= 1;
                        _score = _score += mergeScore;
                        _game.setScore(_score, _maxScore);
                    } else if (isMove) {
                        _game.moveTile(board[r][c], tiltRow(side, r, c),
                                       tiltCol(side, r, c),
                                       tiltRow(side, tempRow, c),
                                       tiltCol(side, tempRow, c));
                        board[tempRow][c] = board[r][c];
                        if (tempRow != r) {
                            board[r][c] = 0;
                        }
                    }
                }
            }
        }
        _game.displayMoves();
        for (int r = 0; r < SIZE; r += 1) {
            for (int c = 0; c < SIZE; c += 1) {
                if (board[r][c] % 2 == 1) {
                    board[r][c] = board[r][c] - 1;
                }
                _board[tiltRow(side, r, c)][tiltCol(side, r, c)] = board[r][c];
            }
        }
        return change;
    }

    /** Return the row number on a playing board that corresponds to row R
     *  and column C of a board turned so that row 0 is in direction SIDE (as
     *  specified by the definitions of NORTH, EAST, etc.).  So, if SIDE
     *  is NORTH, then tiltRow simply returns R (since in that case, the
     *  board is not turned).  If SIDE is WEST, then column 0 of the tilted
     *  board corresponds to row SIZE - 1 of the untilted board, and
     *  tiltRow returns SIZE - 1 - C. */

    int tiltRow(Side side, int r, int c) {
        switch (side) {
        case NORTH:
            return r;
        case EAST:
            return c;
        case SOUTH:
            return SIZE - 1 - r;
        case WEST:
            return SIZE - 1 - c;
        default:
            throw new IllegalArgumentException("Unknown direction");
        }
    }

    /** Return the column number on a playing board that corresponds to row
     *  R and column C of a board turned so that row 0 is in direction SIDE
     *  (as specified by the definitions of NORTH, EAST, etc.). So, if SIDE
     *  is NORTH, then tiltCol simply returns C (since in that case, the
     *  board is not turned).  If SIDE is WEST, then row 0 of the tilted
     *  board corresponds to column 0 of the untilted board, and tiltCol
     *  returns R. */
    int tiltCol(Side side, int r, int c) {
        switch (side) {
        case NORTH:
            return c;
        case EAST:
            return SIZE - 1 - r;
        case SOUTH:
            return SIZE - 1 - c;
        case WEST:
            return r;
        default:
            throw new IllegalArgumentException("Unknown direction");
        }
    }

    /** Return the side indicated by KEY ("Up", "Down", "Left",
     *  or "Right"). */
    Side keyToSide(String key) {
        switch (key) {
        case "Up":
            return NORTH;
        case "Down":
            return SOUTH;
        case "Left":
            return WEST;
        case "Right":
            return EAST;
        default:
            throw new IllegalArgumentException("unknown key designation");
        }
    }

    /** Represents the board: _board[r][c] is the tile value at row R,
     *  column C, or 0 if there is no tile there. */
    private int[][] _board = new int[SIZE][SIZE];

    /** True iff --testing option selected. */
    private boolean _testing;
    /** The current input source and output sink. */
    private Game _game;
    /** The score of the current game, and the maximum final score
     *  over all games in this session. */
    private int _score, _maxScore;
    /** Number of tiles on the board. */
    private int _count;
}
