package expression.exceptions;

import expression.TripleExpression;

public class ExpressionParser extends BaseParser implements TripleParser {
    private final static Checker isWhitespaceChecker = new IsWhitespaceChecker();

    public ExpressionParser() {
        super();
    }

    @Override
    public TripleExpression parse(final String expression) {
        setSource(new StringSource(expression));
        TripleExpression result = parseShifts();
        if (!eof()) {
            throw new ParserException("Expected eof at index " + getCurrentPosition() + ", actual: '" + take() + "'");
        }
        return result;
    }

    private TripleExpression parseShifts() {
        TripleExpression bufferedExpression = parseAddSub();
        skipWhitespace();
        while (true) {
            if (take(">>>")) {
                bufferedExpression = new ShiftA(bufferedExpression, parseAddSub());
            } else if (take("<<")) {
                bufferedExpression = new ShiftL(bufferedExpression, parseAddSub());
            } else if (take(">>")) {
                bufferedExpression = new ShiftR(bufferedExpression, parseAddSub());
            } else {
                break;
            }
        }
        return bufferedExpression;
    }

    private TripleExpression parseAddSub() {
        TripleExpression bufferedExpression = parseMulDiv();
        while (true) {
            skipWhitespace();
            if (take("+")) {
                bufferedExpression = new CheckedAdd(bufferedExpression, parseMulDiv());
            } else if (take("-")) {
                bufferedExpression = new CheckedSubtract(bufferedExpression, parseMulDiv());
            } else {
                break;
            }
        }
        return bufferedExpression;
    }

    private TripleExpression parseMulDiv() {
        TripleExpression bufferedExpression = parsePowLog();
        while (true) {
            skipWhitespace();
            if (take("*")) {
                bufferedExpression = new CheckedMultiply(bufferedExpression, parsePowLog());
            } else if (take("/")) {
                bufferedExpression = new CheckedDivide(bufferedExpression, parsePowLog());
            } else {
                break;
            }
        }
        return bufferedExpression;
    }

    private TripleExpression parsePowLog() {
        TripleExpression bufferedExpression = parseUnaryOperator();
        while (true) {
            skipWhitespace();
            if (take("**")) {
                bufferedExpression = new CheckedPow(bufferedExpression, parseUnaryOperator());
            } else if (take("//")) {
                bufferedExpression = new CheckedLog(bufferedExpression, parseUnaryOperator());
            } else {
                break;
            }
        }
        return bufferedExpression;
    }

    private TripleExpression parseUnaryOperator() {
        TripleExpression bufferedExpression = parseBracket();
        if (bufferedExpression != null) {
            return bufferedExpression;
        }
        skipWhitespace();
        if (take("-")) {
            if (between('1', '9')) {
                return new Const(takeInteger(new StringBuilder("-")));
            }
            return new CheckedNegate(parseUnaryOperator());
        } else if (take("t0")) {
            if (!test('(')) {
                expect(isWhitespaceChecker);
            }
            return new TZeroes(parseUnaryOperator());
        } else if (take("l0")) {
            if (!test('(')) {
                expect(isWhitespaceChecker);
            }
            return new LZeroes(parseUnaryOperator());
        } else if (take("abs")) {
            if (!test('(')) {
                expect(isWhitespaceChecker);
            }
            return new Abs(parseUnaryOperator());
        }
        return parseNumber();
    }

    private TripleExpression parseBracket() {
        skipWhitespace();
        TripleExpression bufferedExpression = null;
        if (take('(')) {
            bufferedExpression = parseShifts();
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

    private TripleExpression parseNumber() {
        skipWhitespace();
        if (take("x")) {
            return new Variable("x");
        } else if (take("y")) {
            return new Variable("y");
        } else if (take("z")) {
            return new Variable("z");
        } else {
            return new Const(takeInteger(new StringBuilder()));
        }
    }

    private void skipWhitespace() {
        while (takeSpecialChar(isWhitespaceChecker)) {
            // skip
        }
    }
}
