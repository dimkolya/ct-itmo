import java.util.Scanner;
import java.util.Arrays;

public class ReverseSum2 {
    public static void main(String[] args) {
        Scanner lineScan = new Scanner(System.in);
        int lineCount = 0;
        int[] column = new int[1];
        int[][] data = new int[1][];
        while (lineScan.hasNextLine()) {
            String line = lineScan.nextLine();

            int[] row = new int[1];
            Scanner numScan = new Scanner(line);
            int it = 0;
            int sumNow = 0;
            while (numScan.hasNextInt()) {
                int num = numScan.nextInt();
                if (it == column.length) {
                    column = Arrays.copyOf(column, column.length * 2);
                }
                if (it == row.length) {
                    row = Arrays.copyOf(row, row.length * 2);
                }
                row[it] = num;
                column[it] += num;
                sumNow += column[it];
                System.out.print(sumNow + " ");
                it++;
            }

            if (lineCount == data.length) {
                data = Arrays.copyOf(data, data.length * 2);
            }
            data[lineCount++] = Arrays.copyOf(row, it);

            System.out.println();
        }
    }
}