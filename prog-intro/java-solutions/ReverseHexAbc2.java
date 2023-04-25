import java.lang.StringBuilder;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.IOException;

class isNotWhitespaceChecker implements Checker {
    public boolean checkChar(char c) {
        return !Character.isWhitespace(c);
    }
}

public class ReverseHexAbc2 {

    private static isNotWhitespaceChecker checker = new isNotWhitespaceChecker();

    private static String toLetterFormat(int n) {
        StringBuilder ans = new StringBuilder();
        String num = new String(Integer.toString(n));
        if (n < 0) {
            ans.append('-');
            num = num.substring(1, num.length());
        }
        for (int i = 0; i < num.length(); i++) {
            ans.append((char)(num.charAt(i) - '0' + 'a'));
            n /= 10;
        }
        return ans.toString();
    }

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
                        System.out.print(toLetterFormat(row[j]));
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