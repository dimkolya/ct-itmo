package expression;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Abs extends AbstractUnaryOperator {
    public Abs(GeneralExpression expression) {
        super(expression);
    }

    @Override
    public String getOperation() {
        return "abs";
    }

    @Override
    public int apply(int value) {
        return Math.abs(value);
    }
}
