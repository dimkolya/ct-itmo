package expression;

import java.math.BigInteger;
import java.math.BigDecimal;

public class Divide extends AbstractBinaryOperator {
    public Divide(final GeneralExpression left, final GeneralExpression right) {
        super(left, right);
    }

    @Override
    public String getOperation() {
        return "/";
    }

    @Override
    public int getPriority(boolean isRight) {
        if (isRight) {
            return 1023;
        } else {
            return 1025;
        }
    }

    @Override
    public int apply(int left, int right) {
        return left / right;
    }
}