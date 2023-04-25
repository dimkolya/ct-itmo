import java.math.BigInteger;
public class SumBigIntegerHex {
    public static void main(final String[] args) {
        BigInteger sum = BigInteger.ZERO;
        for (int i = 0; i < args.length; ++i) {
            String str_num = args[i], x;
            for (int left = 0; left < str_num.length(); ++left) {
                if (!Character.isWhitespace(str_num.charAt(left))) {
                    int right = left;
                    while (right < str_num.length() - 1 && !Character.isWhitespace(str_num.charAt(right + 1))) {
                        ++right;
                    }
                    x = str_num.substring(left, right + 1);
                    if (x.toLowerCase().startsWith("0x")) {
                        sum = sum.add(new BigInteger(x.substring(2, x.length()), 16));
                    } else {
                        sum = sum.add(new BigInteger(x));
                    }
                    left = right;
                }
            }
        }
        System.out.println(sum);
    }
}