package expression;

import java.math.BigInteger;
import java.math.BigDecimal;

public class Subtract extends AbstractBinaryOperator {
    private static final int priority = 1;

    public Subtract(GeneralExpression right, GeneralExpression left) {
        super(right, left, "-");
    }

    public int evaluate(int x) {
        return right.evaluate(x) - left.evaluate(x);
    }

    public int evaluate(int x, int y, int z) {
        return right.evaluate(x, y, z) - left.evaluate(x, y, z);
    }

    public BigInteger evaluate(BigInteger x) {
        return right.evaluate(x).subtract(left.evaluate(x));
    }

    public BigDecimal evaluate(BigDecimal x) {
        return right.evaluate(x).subtract(left.evaluate(x));
    }

    public boolean getCommunicativity() {
        return false;
    }

    public int getPriority() {
        return priority;
    }
}