import java.util.*;

public class J {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            int numOfSpots = sc.nextInt();

            ArrayList<int[]> matrix = new ArrayList<>();
            for (int i = 0; i < numOfSpots; i++) {
                int[] row = new int[numOfSpots];
                String temp = sc.next();
                for (int j = 0; j < numOfSpots; j++) {
                    row[j] = temp.charAt(j) - '0';
                }
                matrix.add(row);
            }

            for (int i = 0; i < numOfSpots; i++) {
                for (int j = 0; j < numOfSpots; j++) {
                    if (matrix.get(i)[j] == 0) {
                        System.out.print(0);
                    } else {
                        System.out.print(1);
                        for (int k = j + 1; k < numOfSpots; k++) {
                            matrix.get(i)[k] -= matrix.get(j)[k];
                            matrix.get(i)[k] = (matrix.get(i)[k] + 10) % 10;
                        }
                    }
                }
                System.out.println();
            }
        }
    }
}
