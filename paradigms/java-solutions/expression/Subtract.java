package expression;

import java.math.BigInteger;
import java.math.BigDecimal;

public class Subtract extends AbstractBinaryOperator {
    public Subtract(final GeneralExpression left, final GeneralExpression right) {
        super(left, right);
    }

    @Override
    public String getOperation() {
        return "-";
    }

    @Override
    public int getPriority(boolean isRight) {
        if (isRight) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int apply(int left, int right) {
        return left - right;
    }
}