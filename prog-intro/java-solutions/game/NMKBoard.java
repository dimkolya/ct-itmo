package game;

public class NMKBoard extends TwoDimBoard {
    private static final Direction[] DIRECTIONS = {
            new Direction(1, 0),
            new Direction(0, 1),
            new Direction(1, 1),
            new Direction(1, -1)
    };

    public NMKBoard(int n, int m, int k) {
        super(n, m, k, DIRECTIONS);
    }
}