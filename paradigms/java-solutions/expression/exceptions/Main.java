package expression.exceptions;

import expression.TripleExpression;

public class Main {
    public static void main(String[] args) {
        String source = "1000000*x*x*x*x*x/(x-1)";
        try {
            TripleParser parser = new ExpressionParser();
            TripleExpression expression = parser.parse(source);
            System.out.println("f\tx");
            for (int x = 0; x < 11; x++) {
                try {
                    int result = expression.evaluate(x, 0, 0);
                    System.out.println(x + "\t" + result);
                } catch (ExpressionEvaluateException e) {
                    System.out.println(x + "\t" + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}