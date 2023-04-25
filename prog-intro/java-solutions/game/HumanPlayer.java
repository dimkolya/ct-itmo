package game;

import java.util.Scanner;
import java.util.Map;

public class HumanPlayer implements Player {
    private final Scanner in;

    public HumanPlayer(Scanner in) {
        this.in = in;
    }

    public Move makeMove(Position position, boolean wasOffer) {
        if (wasOffer) {
            System.out.println("The rival refused. Please, enter your move:");
        } else {
            System.out.println("Current position");
            System.out.println(position);
            System.out.println("Enter your move for " + position.getTurn() + " or enter 0 to suggest a draw:");
        }

        Move move;
        while (true) {
            try {
                // :NOTE: Руками
                int row = Integer.valueOf(in.next()) - 1;
                if(!wasOffer && row == -1) {
                    return new Move(GameResult.DRAW);
                }
                int col = Integer.valueOf(in.next()) - 1;
                move = new Move(row, col, position.getTurn());
                if (!position.isValid(move)) {
                    // :NOTE: throw new Exception
                    throw new Exception("Incorrect input");
                }
                System.out.println();
                return move;
                // :NOTE: Exception
            } catch (Exception e) {
                System.out.println("Not valid move, please enter correct move:");
            }
        }
    }

    public boolean ansToOffer(Position position, GameResult offer) {
        System.out.println("Current position");
        System.out.println(position);
        System.out.println("Rival's suggest " + offer + ". Enter if you're:");
        System.out.println("1) Agree");
        System.out.println("2) Don't agree");
        while (true) {
            try {
                int ans = Integer.valueOf(in.next());
                if (ans != 1 && ans != 2) {
                    throw new Exception("Incorrect input");
                }
                System.out.println();
                return (ans == 1) ? true : false;
            } catch (Exception e) {
                System.out.println("Not valid ans, please enter 1 or 2:");
            }
        }
    }
}
