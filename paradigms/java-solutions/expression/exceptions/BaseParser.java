package expression.exceptions;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class BaseParser {
    private static final char END = '\0';
    private CharSource source;
    private char ch = 0xffff;

    protected BaseParser() {
        source = null;
    }

    protected void setSource(CharSource source) {
        this.source = source;
        take();
    }

    protected char take() {
        final char result = ch;
        ch = source.hasNext() ? source.next() : END;
        return result;
    }

    protected boolean take(final String expected) {
        if (expected.equals(ch + source.getNextLen(expected.length() - 1))) {
            for (int i = 0; i < expected.length(); i++) {
                take();
            }
            return true;
        }
        return false;
    }

    protected boolean test(final char expected) {
        return ch == expected;
    }

    protected boolean take(final char expected) {
        if (test(expected)) {
            take();
            return true;
        }
        return false;
    }

    protected void expect(final char expected) {
        if (!take(expected)) {
            throw new ParserException("Expected '" + expected + "' at index "
                    + getCurrentPosition() + ", found '" + ch + "'");
        }
    }

    protected void expect(final char from, final char to) {
        if (!between(from, to)) {
            throw new ParserException("Expected symbol from '" + from + "' to '" + to + "' at index "
                    + getCurrentPosition() + ", found '" + ch + "'");
        }
    }

    protected boolean takeSpecialChar(final Checker checker) {
        if (checker.checkChar(ch)) {
            take();
            return true;
        }
        return false;
    }

    protected void expect(final Checker checker) {
        if (!takeSpecialChar(checker)) {
            throw new ParserException("Expected special symbol: \"" + checker.getName() + "\" at index "
                    + getCurrentPosition() + ", actual: '" + ch + "'");
        }
    }

    protected void expect(final String value) {
        for (final char c : value.toCharArray()) {
            expect(c);
        }
    }

    protected int getCurrentPosition() {
        return source.getCurrentPosition() - 1;
    }

    protected boolean eof() {
        return take(END);
    }

    protected boolean between(final char from, final char to) {
        return from <= ch && ch <= to;
    }
}
