package game;

public class TwoPlayerGame {
    private final Board board;
    private final Player[] players;

    public TwoPlayerGame(final Board board, final Player player1, final Player player2) {
        this.board = board;
        players = new Player[]{ player1, player2 };
    }

    public int play(final boolean log) {
        while (true) {
            final int result1 = makeMove(0, log);
            if (result1 != -1)  {
                return result1;
            }
            final int result2 = makeMove(1, log);
            if (result2 != -1)  {
                return result2;
            }
        }
    }

    private int makeMove(final int no, final boolean log) {
        final GameResult result;
        Move move;
        final Position position = board.getPosition();
        move = players[no].makeMove(position, false);
        final GameResult offer = move.getOffer();
        if (offer != GameResult.UNKNOWN) {
            if (players[1 - no].ansToOffer(position, offer)) {
                result = offer;
            } else {
                move = players[no].makeMove(position, true);
                result = board.makeMove(move);
            }
        } else {
            result = board.makeMove(move);
        }
        if (log) {
            System.out.println();
            System.out.println("Player: " + (no + 1));
            System.out.println(move);
            System.out.println(board);
            System.out.println("Result: " + result);
        }
        switch (result) {
            case WIN:
                return no + 1;
            case LOSE:
                return 2 - no;
            case DRAW:
                return 0;
            case UNKNOWN:
                return -1;
            default:
                throw new AssertionError("Unknown makeMove result " + result);
        }
    }
}
