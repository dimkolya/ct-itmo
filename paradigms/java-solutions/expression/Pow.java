package expression;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Pow extends AbstractBinaryOperator {
    public Pow(GeneralExpression base, GeneralExpression power) {
        super(base, power);
    }

    @Override
    public String getOperation() {
        return "**";
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
    public int apply(int base, int power) {
        int ans = 1;
        while (power != 0) {
            if (power % 2 == 1) {
                ans *= base;
            }
            power /= 2;
            base *= base;
        }
        return ans;
    }
}
