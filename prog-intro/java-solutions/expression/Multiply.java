package expression;

import java.math.BigInteger;
import java.math.BigDecimal;

public class Multiply extends AbstractBinaryOperator {
    private static final int priority = 2;

    public Multiply(GeneralExpression right, GeneralExpression second) {
        super(right, second, "*");
    }

    public int evaluate(int x) {
        return right.evaluate(x) * left.evaluate(x);
    }

    public int evaluate(int x, int y, int z) {
        return right.evaluate(x, y, z) * left.evaluate(x, y, z);
    }

    public BigInteger evaluate(BigInteger x) {
        return right.evaluate(x).multiply(left.evaluate(x));
    }

    public BigDecimal evaluate(BigDecimal x) {
        return right.evaluate(x).multiply(left.evaluate(x));
    }

    public boolean getCommunicativity() {
        return true;
    }

    public int getPriority() {
        return priority;
    }
}