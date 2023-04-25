package expression.generic.parser;

import expression.exceptions.ExpressionEvaluateException;
import expression.generic.operators.*;

public interface Parser<T> {
    TripleExpression<T> parse(String expression) throws ExpressionEvaluateException;
}
