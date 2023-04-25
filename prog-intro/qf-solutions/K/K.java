import java.io.*;
import java.security.KeyPair;
import java.util.*;

public class K {
    public static void fillSon(ArrayList<char[]> map, int u, int d, int l, int r, int ySon, int xSon) {
        for (int i = u; i < d; i++) {
            for (int j = l; j < r; j++) {
                map.get(i)[j] = 'a';
            }
        }
        map.get(ySon)[xSon] = 'A';
    }

    public static void fillRect(ArrayList<char[]> map, int u, int d, int l, int r) {
        for (int i = u + 1; i < d; i++) {
            for (int j = l; j < r; j++) {
                if (map.get(i)[j] == '.') {
                    map.get(i)[j] = Character.toLowerCase(map.get(i - 1)[j]);
                }
            }
        }
        for (int i = d - 2; i >= u; i--) {
            for (int j = l; j < r; j++) {
                if (map.get(i)[j] == '.') {
                    map.get(i)[j] = Character.toLowerCase(map.get(i + 1)[j]);
                }
            }
        }
        for (int i = u; i < d; i++) {
            for (int j = l + 1; j < r; j++) {
                if (map.get(i)[j] == '.') {
                    map.get(i)[j] = Character.toLowerCase(map.get(i)[j - 1]);
                }
            }
        }
        for (int i = u; i < d; i++) {
            for (int j = r - 2; j >= l; j--) {
                if (map.get(i)[j] == '.') {
                    map.get(i)[j] = Character.toLowerCase(map.get(i)[j + 1]);
                }
            }
        }
    }

    private static boolean containsSon(int u, int d, int l, int r, int xA, int yA) {
        return (u <= xA && xA < d) && (l <= yA && yA < r);
    }

    public static void main(String[] args) {
        try (MyScanner sc = new MyScanner(System.in, new isNotWhitespace())) {
            int n, m;
            n = sc.nextInt();
            m = sc.nextInt();

            ArrayList<char[]> map = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                map.add(sc.next().toCharArray());
            }

            int ySon = 0, xSon = 0;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (map.get(i)[j] == 'A') {
                        ySon = i;
                        xSon = j;
                        break;
                    }
                }
            }
            map.get(ySon)[xSon] = '.';

            int maxSquare = Integer.MIN_VALUE;
            int bestL = 0, bestR = 0;
            int bestU = 0, bestD = 0;
            int[] notSon = new int[m + 1];
            for (int i = 0; i < n; i++) {
                Stack<Integer> first = new Stack<>();
                Stack<Integer> second = new Stack<>();
                for (int x = 0; x <= m; x++) {
                    if (x == m || map.get(i)[x] != '.') {
                        notSon[x] = i + 1;
                    }
                    int goodX = x;
                    while (!first.isEmpty() && second.peek() < notSon[x]) {
                        int left = first.peek(), right = x;
                        int up = second.peek(), down = i + 1;
                        if (containsSon(up, down, left, right, ySon, xSon)) {
                            int Square = (right - left) * (down - up);
                            if (Square > maxSquare) {
                                maxSquare = Square;
                                bestL = left;
                                bestR = right;
                                bestU = up;
                                bestD = down;
                            }
                        }
                        goodX = first.peek();
                        first.pop();
                        second.pop();
                    }
                    first.push(goodX);
                    second.push(notSon[x]);
                }
            }

            fillSon(map, bestU, bestD, bestL, bestR, ySon, xSon);

            fillRect(map, 0, bestU, 0, m);
            fillRect(map, bestD, n, 0, m);
            fillRect(map, bestU, bestD, 0, bestL);
            fillRect(map, bestU, bestD, bestR, m);

            for (int i = 0; i < n; i++) {
                System.out.print(map.get(i));
                System.out.println();
            }
        } catch (IOException e) {
            System.err.println("Input error: " + e.getMessage());
        }
    }

    interface Checker {
        boolean checkChar(char c);
    }

    private static class isNotWhitespace implements Checker {
        public boolean checkChar(char c) {
            return !Character.isWhitespace(c);
        }
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
}
