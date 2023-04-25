package expression.generic.operators;

import expression.generic.modes.Mode;

public class Log<T extends Number> extends AbstractBinaryOperator<T> {
    public Log(final Mode<T> mode, final TripleExpression<T> number, final TripleExpression<T> base) {
        super(mode, number, base);
    }

    @Override
    public String getOperation() {
        return "//";
    }

    @Override
    public int getPriority(boolean isRight) {
        if (isRight) {
            return 2048;
        } else {
            return 2047;
        }
    }

    @Override
    public T apply(T number, T base) {
        return mode.log(number, base);
    }
}