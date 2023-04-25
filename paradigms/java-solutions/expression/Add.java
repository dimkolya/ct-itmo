package expression;

public class Add extends AbstractBinaryOperator {
    public Add(final GeneralExpression left, final GeneralExpression right) {
        super(left, right);
    }

    @Override
    public String getOperation() {
        return "+";
    }

    @Override
    public int getPriority(boolean isRight) {
        return 0;
    }

    @Override
    public int apply(int left, int right) {
        return left + right;
    }
}