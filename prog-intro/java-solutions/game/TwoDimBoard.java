package game;

import java.util.Arrays;
import java.util.Map;

abstract class TwoDimBoard implements Board, Position {
    private final int boardHeight, boardWidth;
    private final int heightSize, widthSize;
    private final int chainLength;
    private final Cell[][] field;
    private Cell turn;
    private int movesToDraw;
    private final Direction[] directions;
    protected static final Map<Cell, String> CELL_TO_STRING = Map.of(
            Cell.E, ".",
            Cell.X, "X",
            Cell.O, "0"
    );

    public TwoDimBoard(int boardHeight, int boardWidth, int chainLength, Direction[] directions) {
        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;
        this.chainLength = chainLength;
        movesToDraw = boardHeight * boardWidth;

        heightSize = Integer.toString(boardHeight).length();
        widthSize = Integer.toString(boardWidth).length();

        field = new Cell[boardHeight][boardWidth];
        for (Cell[] row : field) {
            Arrays.fill(row, Cell.E);
        }

        this.directions = directions;

        turn = Cell.X;
    }

    public Cell getTurn() {
        return turn;
    }

    public Cell getCell(int row, int col) {
        return field[row][col];
    }

    public int getBoardHeight() {
        return boardHeight;
    }

    public int getBoardWidth() {
        return boardHeight;
    }

    public int getChainLength() {
        return chainLength;
    }

    public Position getPosition() {
        return new ProxyPosition(this);
    }

    @Override
    public GameResult makeMove(Move move) {
        if (!isValid(move)) {
            return GameResult.LOSE;
        }

        int row = move.getRow();
        int col = move.getCol();
        field[row][col] = move.getValue();
        if (checkWin(row, col)) {
            return GameResult.WIN;
        }

        if (--movesToDraw == 0) {
            return GameResult.DRAW;
        }

        turn = ((turn == Cell.X) ? Cell.O : Cell.X);
        return GameResult.UNKNOWN;
    }

    public boolean isValid(final Move move) {
        int row = move.getRow();
        int col = move.getCol();
        return inBoard(row, col) && field[row][col] == Cell.E && turn == move.getValue();
    }

    private boolean inBoard(int row, int col) {
        return (0 <= row && row < boardHeight && 0 <= col && col < boardWidth);
    }

    private int countIterate(int curRow, int curCol, int rowDir, int colDir) {
        int curChainLength = 0;
        while (inBoard(curRow, curCol) && field[curRow][curCol] == turn) {
            curChainLength++;
            curRow = curRow + rowDir;
            curCol = curCol + colDir;
        }
        return curChainLength;
    }

    private boolean checkWin(int row, int col) {
        int curChainLength = 0;
        for (Direction dir : directions) {
            curChainLength = Math.max(curChainLength,
                    countIterate(row, col, dir.getRowDir(), dir.getColDir())
                            + countIterate(row, col, -dir.getRowDir(), -dir.getColDir()) - 1);
        }
        if (curChainLength >= chainLength) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < heightSize + 1; i++) {
            result.append(' ');
        }
        for (int i = 1; i <= boardWidth; i++) {
            result.append(i);
            for (int j = 0; j < widthSize - Integer.toString(i + 1).length() + 1; j++) {
                result.append(' ');
            }
        }
        result.append(System.lineSeparator());

        for (int i = 1; i <= boardHeight; i++) {
            for (int j = 0; j < heightSize - Integer.toString(i).length(); j++) {
                result.append(' ');
            }
            result.append(i).append(' ');
            for (int j = 0; j < boardWidth; j++) {
                result.append(CELL_TO_STRING.get(field[i - 1][j]));
                for (int k = 0; k < widthSize; k++) {
                    result.append(' ');
                }
            }
            result.append(System.lineSeparator());
        }

        return result.toString();
    }
}