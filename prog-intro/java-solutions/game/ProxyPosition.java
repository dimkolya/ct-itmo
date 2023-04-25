package game;

public class ProxyPosition implements Position {
    private Position position;

    ProxyPosition(Position board) {
        this.position = board;
    }

    @Override
    public Cell getTurn() {
        return position.getTurn();
    }

    @Override
    public Cell getCell(int row, int col) {
        return position.getCell(row, col);
    }

    @Override
    public int getBoardHeight() {
        return position.getBoardHeight();
    }

    @Override
    public int getBoardWidth() {
        return position.getBoardWidth();
    }

    @Override
    public int getChainLength() {
        return position.getChainLength();
    }

    @Override
    public boolean isValid(Move move) {
        return position.isValid(move);
    }

    @Override
    public String toString() {
        return position.toString();
    }
}