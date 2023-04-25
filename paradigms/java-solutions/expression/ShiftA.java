package expression;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ShiftA extends AbstractBinaryOperator {
    public ShiftA(final GeneralExpression left, final GeneralExpression right) {
        super(left, right);
    }

    @Override
    public String getOperation() {
        return ">>>";
    }

    @Override
    public int getPriority(boolean isRight) {
        if (isRight) {
            return -1024;
        } else {
            return -1023;
        }
    }

    @Override
    public int apply(int left, int right) {
        return left >>> right;
    }
}