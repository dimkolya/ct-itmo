package game;

import java.util.Scanner;
import java.io.*;

public class Main {
    private static final int boardMaxSize = 50;

    private static int[] read(Scanner in, int numOfScan, int min, int max, String firstMsg, String errorMsg) {
        System.out.println(firstMsg);
        int[] result = new int[numOfScan];
        while (true) {
            try {
                for (int i = 0; i < numOfScan; i++) {
                    result[i] = Integer.valueOf(in.next());
                    if (result[i] < min || max < result[i]) {
                        throw new Exception("Incorrect input");
                    }
                }
                System.out.println();
                return result;
            } catch (Exception e) {
                System.out.println(errorMsg);
            }
        }
    }

    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            while (true) {
                final Board board;
                final Player[] players = new Player[2];
                int boardHeight, boardWidth, chainLength;

                int gameId = read(in, 1, 0, 2,
                        "Choose the game:\n0) Exit\n1) N, M, K - game\n2) HEXGame",
                        "Incorrect input, please enter a number from 0 to 2:")[0];
                if (gameId == 0) {
                    break;
                } else {
                    int[] input;
                    switch (gameId) {
                        case 1:
                            input = read(in, 3, 1, boardMaxSize,
                                    "Enter three numbers: N, M, K:",
                                    "Incorrect input, please enter three numbers from 1 to" + boardMaxSize +
                                            ": board height, board width and chain length for win");
                            boardHeight = input[0];
                            boardWidth = input[1];
                            chainLength = input[2];
                            board = new NMKBoard(boardHeight, boardWidth, chainLength);
                            break;
                        case 2:
                            input = read(in, 2, 1, boardMaxSize,
                                    "Enter two numbers: board size and chain length for win:",
                                    "Incorrect input, please enter two numbers from 1 to" + boardMaxSize +
                                            ": board size and chain length for win");
                            boardHeight = input[0];
                            boardWidth = input[0];
                            chainLength = input[1];
                            board = new HEXBoard(boardHeight, chainLength);
                            break;
                        default:
                            throw new AssertionError("Unknown error: gameId = " + gameId);
                    }
                }
                for (int i = 0; i < 2; i++) {
                    int playerId = read(in, 1, 1, 3,
                            "Choose the player " + (i + 1) + ":\n1) Human\n2) Random player\n3) Sequential player",
                            "Incorrect input, please enter a number from 1 to 3:")[0];
                    switch (playerId) {
                        case 1:
                            players[i] = new HumanPlayer(in);
                            break;
                        case 2:
                            players[i] = new RandomPlayer();
                            break;
                        case 3:
                            players[i] = new SequentialPlayer();
                            break;
                        default:
                            throw new AssertionError("Unknown playerId = " + playerId);
                    }
                }
                final int result = new TwoPlayerGame(board, players[0], players[1]).play(true);
                switch (result) {
                    case 1:
                        System.out.println("First player won");
                        break;
                    case 2:
                        System.out.println("Second player won");
                        break;
                    case 0:
                        System.out.println("Draw");
                        break;
                    default:
                        throw new AssertionError("Unknown result " + result);
                }
            }
        }
    }
}
