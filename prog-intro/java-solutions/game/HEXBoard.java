package game;

import java.util.Arrays;
import java.util.Map;

public class HEXBoard extends TwoDimBoard {
    private final int boardSize;
    private final int chainLength;
    private final int numSize;
    private static final Direction[] directions = {
            new Direction(1, 0),
            new Direction(0, 1),
            new Direction(1, -1)
    };

    public HEXBoard(final int boardSize, int chainLength) {
        super(boardSize, boardSize, chainLength, directions);
        this.boardSize = boardSize;
        this.chainLength = chainLength;
        numSize = Integer.toString(boardSize).length();
    }

    @Override
    public int getBoardHeight() {
        return boardSize;
    }

    @Override
    public int getBoardWidth() {
        return boardSize;
    }

    @Override
    public int getChainLength() {
        return chainLength;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize - i - 1; j++) {
                result.append(' ');
            }
            for (int j = 0; j < numSize - Integer.toString(i + 1).length(); j++) {
                result.append(' ');
            }
            result.append(i + 1).append(' ');
            for (int j = 0; j < i; j++) {
                result.append(CELL_TO_STRING.get(super.getCell(i - j - 1, j))).append(' ');
            }
            result.setLength(result.length() - 1);
            result.append(' ').append(i + 1).append(System.lineSeparator());
        }
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < numSize + i; j++) {
                result.append(' ');
            }
            for (int j = 0; j < boardSize - i; j++) {
                result.append(CELL_TO_STRING.get(super.getCell(boardSize - 1 - j, j + i))).append(' ');
            }
            result.setLength(result.length() - 1);
            result.append(' ').append(System.lineSeparator());
        }
        return result.toString();
    }
}
