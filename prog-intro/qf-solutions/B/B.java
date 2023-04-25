import java.util.Scanner;

public class B {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            int n;
            n = sc.nextInt();
            for (int cnt = 0, ans = -710 * 25_000; cnt < n; cnt++, ans += 710) {
                System.out.println(ans);
            }
        }
    }
}

