package expression;

import java.math.BigInteger;
import java.math.BigDecimal;

public class TZeroes extends AbstractUnaryOperator {
    public TZeroes(final GeneralExpression expression) {
        super(expression);
    }

    @Override
    public String getOperation() {
        return "t0";
    }

    @Override
    public int apply(int value) {
        return Integer.numberOfTrailingZeros(value);
    }
}