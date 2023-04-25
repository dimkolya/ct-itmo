package game;

public class Direction {
    private final int rowDir;
    private final int colDir;

    public Direction(int rowDir, int colDir) {
        this.rowDir = rowDir;
        this.colDir = colDir;
    }

    public int getRowDir() {
        return rowDir;
    }

    public int getColDir() {
        return colDir;
    }
}