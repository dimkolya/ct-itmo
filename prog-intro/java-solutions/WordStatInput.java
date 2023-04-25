import java.io.*;
import java.util.Arrays;
import java.nio.charset.StandardCharsets;

public class WordStatInput {
    public static void main(String[] args) {
        Triple[] arr = new Triple[1];
        int wordCount = 0;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "utf8"));
            try {
                char[] word = new char[1];
                int wordSize = 0;
                while (true) {
                    int c = reader.read();
                    if (Character.isLetter(c) || c == '\'' || Character.getType(c) == Character.DASH_PUNCTUATION) {
                        if (wordSize == word.length) {
                            word = Arrays.copyOf(word, word.length * 2);
                        }
                        word[wordSize++] = (char)c;
                    } else {
                        if (wordSize > 0) {
                            if (wordCount == arr.length) {
                                arr = Arrays.copyOf(arr, arr.length * 2);
                            }
                            arr[wordCount] = new Triple(new String(word, 0, wordSize).toLowerCase(), wordCount, 1);
                            wordCount++;
                            wordSize = 0;
                        }
                        if (c == -1) {
                            break;
                        }
                    }
                }
            } finally {
                reader.close();
            }

            Arrays.sort(arr, 0, wordCount, new CompareByWord());

            int it = 0;
            int minPos = arr[0].getPos();
            int count = 1;
            String pre = arr[0].getWord();
            for (int i = 1; i < wordCount; i++) {
                if (!arr[i].getWord().equals(pre)) {
                    arr[it++] = new Triple(pre, minPos, count);
                    pre = arr[i].getWord();
                    minPos = arr[i].getPos();
                    count = 1;
                } else {
                    count++;
                }
            }
            arr[it++] = new Triple(pre, minPos, count);

            arr = Arrays.copyOf(arr, it);
            Arrays.sort(arr, new CompareByPos());

            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "utf8"));
                try {
                    for (int i = 0; i < it; i++) {
                        writer.write(arr[i].getWord() + " " + arr[i].getCount());
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
        } catch (FileNotFoundException e) {
            System.err.println("    Input file doesn't exist: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            System.err.println("    Java doesn't support input file encoding format: " + e.getMessage());
        } catch (java.io.IOException e) {
            System.err.println("    Input error: " + e.getMessage());
        }
    }
}