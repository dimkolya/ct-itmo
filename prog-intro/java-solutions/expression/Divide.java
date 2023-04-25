package expression;

import java.math.BigInteger;
import java.math.BigDecimal;

public class Divide extends AbstractBinaryOperator {
    private static final int PRIORITY = 2;

    public Divide(final GeneralExpression right, final GeneralExpression left) {
        super(right, left, "/");
    }

    @Override
    public int evaluate(final int x) {
        return right.evaluate(x) / left.evaluate(x);
    }

    @Override
    public int evaluate(final int x, final int y, final int z) {
        return right.evaluate(x, y, z) / left.evaluate(x, y, z);
    }

    @Override
    public BigInteger evaluate(final BigInteger x) {
        return right.evaluate(x).divide(left.evaluate(x));
    }

    @Override
    public BigDecimal evaluate(final BigDecimal x) {
        return right.evaluate(x).divide(left.evaluate(x));
    }

    @Override
    public boolean getCommunicativity() {
        return false;
    }

    @Override
    public int getPriority() {
        return 2;
    }
}