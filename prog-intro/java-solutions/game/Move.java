package game;

public class Move {
    private final int row;
    private final int col;
    private final Cell value;
    private final GameResult offer;

    public Move(int row, int col, Cell value) {
        this.row = row;
        this.col = col;
        this.value = value;
        offer = GameResult.UNKNOWN;
    }

    public Move(GameResult offer) {
        row = -1;
        col = -1;
        value = Cell.E;
        this.offer = offer;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Cell getValue() {
        return value;
    }

    public GameResult getOffer() {
        return offer;
    }

    @Override
    public String toString() {
        return String.format("Move(%s, %d, %d)", value, row + 1, col + 1);
    }
}
