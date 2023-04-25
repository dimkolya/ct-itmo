import java.io.*;
import java.util.*;
import java.lang.*;

public class M {
    public static void main(String[] args) {
        try (MyScanner sc = new MyScanner(System.in, new isNumber())) {
            int t;
            t = sc.nextInt();
            for (int it = 0; it < t; it++) {
                int days;
                days = sc.nextInt();

                int[] diffOfProblems = new int[days];
                Map<Integer, Integer> count = new HashMap<>();
                for (int i = 0; i < days; i++) {
                    diffOfProblems[i] = sc.nextInt();
                }

                int ans = 0;
                for (int j = days - 1; j >= 0; j--) {
                    for (int i = 0; i < j; i++) {
                        ans += count.getOrDefault(2 * diffOfProblems[j] - diffOfProblems[i], 0);
                    }
                    count.merge(diffOfProblems[j], 1, Integer::sum);
                }
                System.out.println(ans);
            }
        } catch (IOException e) {
            System.err.println("Input error: " + e.getMessage());
        }
    }

    interface Checker {
        boolean checkChar(char c);
    }

    private static class MyScanner implements AutoCloseable {
        private final Reader reader;
        private char[] buffer;
        private int bufferSize;
        private int bufferIterator;
        private boolean hasBuffer = false;
        private final char[] newLine = { '\n', '\u000B', '\u000C', '\r', '\u0085', '\u2028', '\u2029' };
        private final Checker checker;

        public MyScanner(String fileSource, String charsetName, Checker checker) throws IOException {
            this.checker = checker;
            reader = new InputStreamReader(new FileInputStream(fileSource), charsetName);
        }

        public MyScanner(String fileSource, Checker checker) throws IOException {
            this.checker = checker;
            reader = new InputStreamReader(new FileInputStream(fileSource));
        }

        public MyScanner(InputStream source, String charsetName, Checker checker) throws IOException {
            this.checker = checker;
            reader = new InputStreamReader(source, charsetName);
        }

        public MyScanner(InputStream source, Checker checker) throws IOException {
            this.checker = checker;
            reader = new InputStreamReader(source);
        }

        public MyScanner(File fileName, String charsetName, Checker checker) throws IOException {
            this.checker = checker;
            reader = new InputStreamReader(new FileInputStream(fileName), charsetName);
        }

        public MyScanner(File fileName, Checker checker) throws IOException {
            this.checker = checker;
            reader = new InputStreamReader(new FileInputStream(fileName));
        }

        private boolean isNewLineChar(char a) {
            for (char c : newLine) {
                if (a == c) {
                    return true;
                }
            }
            return false;
        }

        private void skipSeparatorCharsExceptNewLine() throws IOException {
            while (checkInput() && !checker.checkChar(buffer[bufferIterator]) && !isNewLineChar(buffer[bufferIterator])) {
                skip();
            }
        }

        private void skipSeparatorChars() throws IOException, NullPointerException {
            while (checkInput() && !checker.checkChar(buffer[bufferIterator])) {
                skip();
            }
        }

        private void skip() throws IOException {
            bufferIterator++;
            if (bufferIterator == bufferSize) {
                hasBuffer = false;
            }
        }

        private void skipLine() throws IOException {
            char prev = buffer[bufferIterator++];
            if (bufferIterator == bufferSize) {
                hasBuffer = false;
            }
            if (checkInput()
                    && (prev == '\r' && buffer[bufferIterator] == '\n')
                    || (prev == '\n' && buffer[bufferIterator] == '\r')) {
                bufferIterator++;
                if (bufferIterator == bufferSize) {
                    hasBuffer = false;
                }
            }
        }

        private boolean checkInput() throws IOException {
            if (!hasBuffer) {
                buffer = new char[1024];
                bufferSize = reader.read(buffer);
                if (bufferSize == -1) {
                    hasBuffer = false;
                } else {
                    hasBuffer = true;
                    bufferIterator = 0;
                }
            }
            return hasBuffer;
        }

        private String buildNext() throws IOException {
            skipSeparatorChars();
            StringBuilder token = new StringBuilder();
            boolean isEndOfBuild = false;
            while (!isEndOfBuild) {
                int it = bufferIterator;
                int len = 0;
                while (checker.checkChar(buffer[it + len])) {
                    len++;
                    if (it + len == bufferSize) {
                        token.append(buffer, it, len);

                        hasBuffer = false;
                        if (!checkInput()) {
                            isEndOfBuild = true;
                        }
                        break;
                    }
                }
                if (it + len < bufferSize) {
                    token.append(buffer, it, len);

                    isEndOfBuild = true;
                    bufferIterator = it + len;
                }
            }
            return token.toString();
        }

        private String letterToNumber(String token) {
            StringBuilder ans = new StringBuilder();
            for (int i = 0; i < token.length(); i++) {
                if (Character.getType(token.charAt(i)) == Character.LOWERCASE_LETTER) {
                    ans.append((char)(token.charAt(i) - 'a' + '0'));
                } else {
                    ans.append(token.charAt(i));
                }
            }
            return ans.toString();
        }

        public String next() throws IOException {
            return buildNext();
        }

        public int nextInt() throws IOException {
            return Integer.parseInt(buildNext());
        }

        public int nextModInt() throws IOException {
            String token = buildNext();
            if (token.toLowerCase().startsWith("0x")) {
                token = token.substring(2, token.length());
                return (int)Integer.parseUnsignedInt(token, 16);
            } else {
                token = letterToNumber(token);
                return Integer.parseInt(token);
            }
        }

        public boolean isEndOfInput() throws IOException {
            skipSeparatorChars();
            return !checkInput();
        }

        public boolean hasNextLine() throws IOException {
            skipSeparatorCharsExceptNewLine();
            return checkInput();
        }

        public boolean isEndOfLine() throws IOException {
            if (checkInput()) {
                skipSeparatorCharsExceptNewLine();
                if (bufferIterator < bufferSize && isNewLineChar(buffer[bufferIterator])) {
                    skipLine();
                    return true;
                }
            }
            return false;
        }

        public void close() throws IOException {
            reader.close();
        }
    }

    private static class isNumber implements Checker {
        public boolean checkChar(char c) {
            return !Character.isWhitespace(c);
        }
    }
}
