package expression;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Log extends AbstractBinaryOperator {
    public Log(GeneralExpression number, GeneralExpression base) {
        super(number, base);
    }

    @Override
    public String getOperation() {
        return "//";
    }

    @Override
    public int getPriority(boolean isRight) {
        if (isRight) {
            return 2048;
        } else {
            return 2047;
        }
    }

    @Override
    public int apply(int number, int base) {
        int ans = 0;
        while (number > 1) {
            number /= base;
            ans++;
        }
        return ans;
    }
}