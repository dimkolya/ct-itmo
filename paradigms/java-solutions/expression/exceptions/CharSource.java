package expression.exceptions;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface CharSource {

    boolean hasNext();

    String getNextLen(final int len);

    char next();

    int getCurrentPosition();

    IllegalArgumentException error(final String message);
}
