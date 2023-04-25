package game;

public class SequentialPlayer implements Player {
    public Move makeMove(Position position, boolean wasOffer) {
        for (int row = 0; row < position.getBoardHeight(); row++) {
            for (int col = 0; col < position.getBoardWidth(); col++) {
                final Move move = new Move(row, col, position.getTurn());
                if (position.isValid(move)) {
                    return move;
                }
            }
        }
        throw new AssertionError("No valid moves");
    }

    public boolean ansToOffer(Position position, GameResult offer) {
        return false;
    }
}
