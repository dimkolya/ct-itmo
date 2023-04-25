package expression;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.lang.StringBuilder;

abstract class AbstractBinaryOperator implements GeneralExpression {
    protected final GeneralExpression right;
    protected final GeneralExpression left;
    // :NOTE: Убрать
    protected final String operation;

    protected AbstractBinaryOperator(final GeneralExpression right, final GeneralExpression left, final String operation) {
        this.right = right;
        this.left = left;
        this.operation = operation;
    }

    public abstract int evaluate(int x);

    public abstract int evaluate(int x, int y, int z);

    public abstract BigInteger evaluate(BigInteger x);

    public abstract BigDecimal evaluate(BigDecimal x);

    public abstract int getPriority();

    public abstract boolean getCommunicativity();

    @Override
    public boolean equals(final Object obj) {
        if (obj != null && obj.getClass() == getClass()) {
            final AbstractBinaryOperator that = (AbstractBinaryOperator) obj;
            return right.equals(that.right)
                    && left.equals(that.left)
                    && operation.equals(that.operation);
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + right.toString() + " " + operation + " " + left.toString() + ")";
    }

    private String getMiniString(final ToMiniString expression, final boolean isLeft) {
        Integer operationPriority = null;
        if (expression instanceof AbstractBinaryOperator) {
            operationPriority = ((AbstractBinaryOperator) expression).getPriority();
        }
        if (operationPriority == null
                || operationPriority > getPriority()
                || (operationPriority == getPriority()
                    // :NOTE: Анализ подтипов
                    && (!isLeft || (getCommunicativity() && expression.getClass() != Divide.class)))) {
            return expression.toMiniString();
        } else {
            return "(" + expression.toMiniString() + ")";
        }
    }

    @Override
    public String toMiniString() {
        return getMiniString(right, false) + " " + operation + " " + getMiniString(left, true);
    }

    @Override
    public int hashCode() {
        return (right.hashCode() * 17 + left.hashCode()) * 17 + operation.hashCode();
    }
}
