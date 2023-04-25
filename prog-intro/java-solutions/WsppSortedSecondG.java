import java.io.*;
import java.util.*;

class isWordCharChecker implements Checker {
    public boolean checkChar(char c) {
        return Character.isLetter(c) || c == '\'' || Character.getType(c) == Character.DASH_PUNCTUATION;
    }
}

public class WsppSortedSecondG {
    private static isWordCharChecker checker = new isWordCharChecker();

    public static void main(String[] args) {
        Map<String, MyPair> map = new TreeMap<>();
        try (MyScanner sc = new MyScanner(new File(args[0]), "utf-8", checker)) {
            int position = 1, count = 0;
            while (sc.hasNextLine()) {
                Set<String> oddCount = new HashSet<>();
                while (!sc.isEndOfLine()) {
                    String word = sc.next().toLowerCase();
                    if (!map.containsKey(word)) {
                        map.put(word, new MyPair(0, new IntList()));
                    }

                    if (oddCount.contains(word)) {
                        map.get(word).second.append(position);
                        oddCount.remove(word);
                    } else {
                        oddCount.add(word);
                    }
                    map.get(word).first++;

                    position++;
                }
            }
            
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "utf8"));
                try {
                    for (Map.Entry<String, MyPair> u : map.entrySet()) {
                        writer.write(u.getKey());
                        writer.write(" ");
                        writer.write(String.valueOf(u.getValue()));
                        writer.newLine();
                    }
                } finally {
                    writer.close();
                }
            } catch (FileNotFoundException e) {
                System.err.println("    Output file doesn't exist: " + e.getMessage());
            } catch (UnsupportedEncodingException e) {
                System.err.println("    Java doesn't support output file encoding format: " + e.getMessage());
            } catch (java.io.IOException e) {
                System.err.println("    Output error: " + e.getMessage());
            }
        } catch (java.io.IOException e) {
            System.err.println("    Input error: " + e.getMessage());
        }
    }
}