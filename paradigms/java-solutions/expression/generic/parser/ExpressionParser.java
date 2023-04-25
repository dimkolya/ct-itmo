package expression.generic.parser;

import expression.exceptions.*;
import expression.generic.modes.Mode;
import expression.generic.operators.*;
import expression.generic.operators.Abs;
import expression.generic.operators.Const;
import expression.generic.operators.LZeroes;
import expression.generic.operators.ShiftA;
import expression.generic.operators.ShiftL;
import expression.generic.operators.ShiftR;
import expression.generic.operators.TZeroes;
import expression.generic.operators.Variable;

public class ExpressionParser<T extends Number> extends BaseParser implements Parser<T> {
    private final static Checker isWhitespaceChecker = new IsWhitespaceChecker();
    private final Mode<T> mode;

    public ExpressionParser(Mode<T> mode) {
        this.mode = mode;
    }

    @Override
    public TripleExpression<T> parse(final String expression) {
        setSource(new StringSource(expression));
        TripleExpression<T> result = parseMinMax();
        if (!eof()) {
            throw new ParserException("Expected eof at index " + getCurrentPosition() + ", actual: '" + take() + "'");
        }
        return result;
    }

    private TripleExpression<T> parseMinMax() {
        TripleExpression<T> bufferedExpression = parseShifts();
        skipWhitespace();
        while (true) {
            if (take("min")) {
                bufferedExpression = new Min<>(mode, bufferedExpression, parseShifts());
            } else if (take("max")) {
                bufferedExpression = new Max<>(mode, bufferedExpression, parseShifts());
            } else {
                break;
            }
        }
        return bufferedExpression;
    }

    private TripleExpression<T> parseShifts() {
        TripleExpression<T> bufferedExpression = parseAddSub();
        skipWhitespace();
        while (true) {
            if (take(">>>")) {
                bufferedExpression = new ShiftA<>(mode, bufferedExpression, parseAddSub());
            } else if (take("<<")) {
                bufferedExpression = new ShiftL<>(mode, bufferedExpression, parseAddSub());
            } else if (take(">>")) {
                bufferedExpression = new ShiftR<>(mode, bufferedExpression, parseAddSub());
            } else {
                break;
            }
        }
        return bufferedExpression;
    }

    private TripleExpression<T> parseAddSub() {
        TripleExpression<T> bufferedExpression = parseMulDiv();
        while (true) {
            skipWhitespace();
            if (take("+")) {
                bufferedExpression = new Add<>(mode, bufferedExpression, parseMulDiv());
            } else if (take("-")) {
                bufferedExpression = new Subtract<>(mode, bufferedExpression, parseMulDiv());
            } else {
                break;
            }
        }
        return bufferedExpression;
    }

    private TripleExpression<T> parseMulDiv() {
        TripleExpression<T> bufferedExpression = parsePowLog();
        while (true) {
            skipWhitespace();
            if (take("*")) {
                bufferedExpression = new Multiply<>(mode, bufferedExpression, parsePowLog());
            } else if (take("/")) {
                bufferedExpression = new Divide<>(mode, bufferedExpression, parsePowLog());
            } else {
                break;
            }
        }
        return bufferedExpression;
    }

    private TripleExpression<T> parsePowLog() {
        TripleExpression<T> bufferedExpression = parseUnaryOperator();
        while (true) {
            skipWhitespace();
            if (take("**")) {
                bufferedExpression = new Pow<>(mode, bufferedExpression, parseUnaryOperator());
            } else if (take("//")) {
                bufferedExpression = new Log<>(mode, bufferedExpression, parseUnaryOperator());
            } else {
                break;
            }
        }
        return bufferedExpression;
    }

    private TripleExpression<T> parseUnaryOperator() {
        TripleExpression<T> bufferedExpression = parseBracket();
        if (bufferedExpression != null) {
            return bufferedExpression;
        }
        skipWhitespace();
        if (take("-")) {
            if (between('1', '9')) {
                return new Const<>(mode.valueOf(takeInteger(new StringBuilder("-"))));
            }
            return new Negate<>(mode, parseUnaryOperator());
        } else if (take("t0")) {
            if (!test('(')) {
                expect(isWhitespaceChecker);
            }
            return new TZeroes<>(mode, parseUnaryOperator());
        } else if (take("l0")) {
            if (!test('(')) {
                expect(isWhitespaceChecker);
            }
            return new LZeroes<>(mode, parseUnaryOperator());
        } else if (take("abs")) {
            if (!test('(')) {
                expect(isWhitespaceChecker);
            }
            return new Abs<>(mode, parseUnaryOperator());
        } else if (take("count")) {
            if (!test('(')) {
                expect(isWhitespaceChecker);
            }
            return new Count<>(mode, parseUnaryOperator());
        }
        return parseNumber();
    }

    private TripleExpression<T> parseBracket() {
        skipWhitespace();
        TripleExpression<T> bufferedExpression = null;
        if (take('(')) {
            bufferedExpression = parseMinMax();
            expect(')');
        }
        return bufferedExpression;
    }

    private void takeDigits(final StringBuilder sb) {
        expect('0', '9');
        while (between('0', '9')) {
            sb.append(take());
        }
    }

    private int takeInteger(final StringBuilder sb) {
        if (take('-')) {
            sb.append('-');
        }
        if (take('0')) {
            sb.append('0');
        } else {
            takeDigits(sb);
        }
        return Integer.parseInt(sb.toString());
    }

    private TripleExpression<T> parseNumber() {
        skipWhitespace();
        if (take("x")) {
            return new Variable<>("x");
        } else if (take("y")) {
            return new Variable<>("y");
        } else if (take("z")) {
            return new Variable<>("z");
        } else {
            return new Const<>(mode.valueOf(takeInteger(new StringBuilder())));
        }
    }

    private void skipWhitespace() {
        while (takeSpecialChar(isWhitespaceChecker)) {
            // skip
        }
    }
}
