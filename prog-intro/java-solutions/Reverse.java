import java.lang.StringBuilder;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.IOException;

public class Reverse {

    private static isNotWhitespaceChecker checker = new isNotWhitespaceChecker();

    public static void main(String args[]) {
        ArrayList<int[]> data = new ArrayList<>();
        try {
            MyScanner sc = new MyScanner(System.in, checker);
            try {
                while (sc.hasNextLine()) {
                    int[] row = new int[1];
                    int count = 0;
                    while (!sc.isEndOfLine()) {
                        int num = sc.nextModInt();

                        if (count == row.length) {
                            row = Arrays.copyOf(row, row.length * 2);
                        }
                        row[count++] = num;
                    }

                    data.add(Arrays.copyOf(row, count));
                }

                for (int i = data.size() - 1; i >= 0; i--) {
                    int[] row = data.get(i);
                    for (int j = row.length - 1; j >= 0; j--) {
                        System.out.print(row[j]);
                        System.out.print(' ');
                    }
                    System.out.println();
                }
            } finally {
                sc.close();
            }
        } catch (IOException e) {
            System.err.println("Input error: " + e.getMessage());
        }
    }
}