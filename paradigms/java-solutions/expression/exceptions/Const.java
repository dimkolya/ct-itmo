package expression.exceptions;

import expression.TripleExpression;

public class Const implements TripleExpression {
    private final int value;

    public Const(final int value) {
        this.value = value;
    }

    @Override
    public int evaluate(final int x, final int y, final int z) {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public String toMiniString() {
        return toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj != null && obj.getClass() == Const.class) {
            return value == ((Const) obj).value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value;
    }
}