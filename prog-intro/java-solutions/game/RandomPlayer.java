package game;

import java.util.Random;

public class RandomPlayer implements Player {
    private final Random random = new Random();

    public Move makeMove(Position position, boolean wasOffer) {
        while (true) {
            final Move move = new Move(
                    random.nextInt(position.getBoardHeight()),
                    random.nextInt(position.getBoardWidth()),
                    position.getTurn()
            );
            if (position.isValid(move)) {
                return move;
            }
        }
    }

    public boolean ansToOffer(Position position, GameResult offer) {
        return random.nextInt(2) == 1 ? true : false;
    }
}
