package game;

interface Player {
    Move makeMove(Position position, boolean wasOffer);

    boolean ansToOffer(Position position, GameResult offer);
}
