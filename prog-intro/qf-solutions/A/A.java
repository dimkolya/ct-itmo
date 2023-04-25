import java.util.Scanner;

public class A {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            int a, b, n, ans;
            a = sc.nextInt();
            b = sc.nextInt();
            n = sc.nextInt();
            ans = (n - a - 1) / (b - a) * 2 + 1;
            System.out.println(ans);
        }
    }
}