import java.io.*;
import java.util.*;
import java.lang.*;

public class E {
    private static int maxDist = Integer.MIN_VALUE, teamInTheMaxDist;
    private static IntList[] graph;
    private static int[] distance;
    private static boolean[] isTeamCity;

    private static void dfs(int v, int dist) {
        distance[v] = dist;
        if (isTeamCity[v] && maxDist < dist) {
            maxDist = dist;
            teamInTheMaxDist = v;
        }
        for (int i = 0; i < graph[v].size(); i++) {
            if (distance[graph[v].get(i)] == -1) {
                dfs(graph[v].get(i), dist + 1);
            }
        }
    }

    private static int wayBackDfs(int v) {
        int dist = distance[v];
        if (dist == maxDist / 2) {
            return v;
        }
        for (int i = 0; i < graph[v].size(); i++) {
            if (distance[graph[v].get(i)] == dist - 1) {
                return wayBackDfs(graph[v].get(i));
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        try (MyScanner sc = new MyScanner(System.in, new isNumber())) {
            int numOfCity, numOfTeam;
            numOfCity = sc.nextInt();
            numOfTeam = sc.nextInt();
            graph = new IntList[numOfCity];
            for (int i = 0; i < numOfCity; i++) {
                graph[i] = new IntList();
            }
            int[] teams = new int[numOfTeam];
            isTeamCity = new boolean[numOfCity];
            Arrays.fill(isTeamCity, false);
            for (int i = 0; i < numOfCity - 1; i++) {
                int city1, city2;
                city1 = sc.nextInt() - 1;
                city2 = sc.nextInt() - 1;
                graph[city1].append(city2);
                graph[city2].append(city1);
            }
            for (int i = 0; i < numOfTeam; i++) {
                teams[i] = sc.nextInt() - 1;
                isTeamCity[teams[i]] = true;
            }

            distance = new int[numOfCity];
            Arrays.fill(distance, -1);
            dfs(teams[0], 0);

            boolean hasAns = true;
            int ans = teamInTheMaxDist;
            if (maxDist % 2 == 1) {
                hasAns = false;
            } else {
                ans = wayBackDfs(teamInTheMaxDist);
                int halfMaxDist = maxDist / 2;
                Arrays.fill(distance, -1);
                dfs(ans, 0);
                for (int i = 0; i < numOfTeam; i++) {
                    if (distance[teams[i]] != halfMaxDist) {
                        hasAns = false;
                        break;
                    }
                }
            }

            if (hasAns) {
                System.out.println("YES");
                System.out.println(ans + 1);
            } else {
                System.out.println("NO");
            }
        } catch (IOException e) {
            System.err.println("Input error: " + e.getMessage());
        }
    }

    private static class IntList {
        private int size;
        private int[] arr;

        public IntList() {
            arr = new int[1];
            size = 0;
        }

        public void append(int value) {
            if (size == arr.length) {
                arr = Arrays.copyOf(arr, 2 * arr.length);
            }
            arr[size++] = value;
        }

        public int get(int position) {
            return arr[position];
        }

        public int size() {
            return size;
        }

        @Override
        public String toString() {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < size; i++) {
                res.append(' ');
                res.append(arr[i]);
            }
            return res.toString();
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
