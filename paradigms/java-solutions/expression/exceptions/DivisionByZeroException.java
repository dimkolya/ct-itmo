package expression.exceptions;

public class DivisionByZeroException extends ExpressionEvaluateException {
    public DivisionByZeroException(String message) {
        super(message);
    }
}
