package expression;

import java.math.BigInteger;
import java.math.BigDecimal;

public class Multiply extends AbstractBinaryOperator {
    public Multiply(final GeneralExpression left, final GeneralExpression right) {
        super(left, right);
    }

    @Override
    public String getOperation() {
        return "*";
    }

    @Override
    public int getPriority(boolean isRight) {
        return 1024;
    }

    @Override
    public int apply(int left, int right) {
        return left * right;
    }
}