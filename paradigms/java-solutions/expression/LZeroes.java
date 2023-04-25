package expression;

import java.math.BigInteger;
import java.math.BigDecimal;

public class LZeroes extends AbstractUnaryOperator {
    public LZeroes(final GeneralExpression expression) {
        super(expression);
    }

    @Override
    public String getOperation() {
        return "l0";
    }

    @Override
    public int apply(int value) {
        return Integer.numberOfLeadingZeros(value);
    }
}