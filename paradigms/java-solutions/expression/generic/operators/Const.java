package expression.generic.operators;

public class Const<T extends Number> implements TripleExpression<T> {
    private final T value;

    public Const(final T value) {
        this.value = value;
    }


    @Override
    public T evaluate(final T x, final T y, final T z) {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public String toMiniString() {
        return value.toString();
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}