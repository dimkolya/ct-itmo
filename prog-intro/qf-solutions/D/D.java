import java.util.Arrays;
import java.util.Scanner;

public class D {
    private static long binPow(long num, long degree, long mod) {
        if (degree == 0) {
            return 1;
        } else if (degree % 2 == 1) {
            return modMultiple(num, binPow(num,degree - 1, mod), mod);
        } else {
            long temp = binPow(num, degree / 2, mod);
            return modMultiple(temp, temp, mod);
        }
    }

    private static long modSubtract(long a, long b, long mod) {
        return ((a - b) % mod + mod) % mod;
    }

    private static long modAddition(long a, long b, long mod) {
        return ((a + b) % mod + mod) % mod;
    }

    private static long modMultiple(long a, long b, long mod) {
        return (a % mod) * (b % mod) % mod;
    }

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            long mod = 998_244_353;
            int n, k;
            n = sc.nextInt();
            k = sc.nextInt();

            long[] r = new long[n + 1];
            long[] d = new long[n + 1];
            long ans = 0;
            for (int num = 1; num <= n; num++) {
                if (num % 2 == 0) {
                    r[num] = modMultiple(num / 2, binPow(k, num / 2, mod), mod);
                    long temp = modMultiple(num / 2, binPow(k, num / 2 + 1, mod), mod);
                    r[num] = modAddition(temp, r[num], mod);
                } else {
                    r[num] = modMultiple(num, binPow(k, (num + 1) / 2, mod), mod);
                }

                d[num] = r[num];
                for (int divisor = 1; divisor * divisor <= num; divisor++) {
                    if (num % divisor == 0 && divisor < num) {
                        r[num] = modSubtract(r[num], modMultiple(num / divisor - 1, d[divisor], mod), mod);
                        d[num] = modSubtract(d[num], modMultiple(num / divisor, d[divisor], mod), mod);
                        if (divisor * divisor < num && divisor != 1) {
                            r[num] = modSubtract(r[num], modMultiple(divisor - 1, d[num / divisor], mod), mod);
                            d[num] = modSubtract(d[num], modMultiple(divisor, d[num / divisor], mod), mod);
                        }
                    }
                }

                ans = modAddition(ans, r[num], mod);
            }

            System.out.println(ans);
        }
    }
}