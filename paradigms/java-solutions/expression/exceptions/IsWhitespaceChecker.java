package expression.exceptions;

public class IsWhitespaceChecker implements Checker {
    @Override
    public boolean checkChar(char x) {
        return Character.isWhitespace(x);
    }

    @Override
    public String getName() {
        return "whitespace";
    }
}
