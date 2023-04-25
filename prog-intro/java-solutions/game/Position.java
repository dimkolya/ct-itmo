package game;

public interface Position {
    Cell getTurn();

    Cell getCell(int row, int col);

    int getBoardHeight();

    int getBoardWidth();

    int getChainLength();

    boolean isValid(Move move);
}
