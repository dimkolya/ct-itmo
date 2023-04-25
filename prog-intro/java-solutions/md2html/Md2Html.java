package md2html;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;

public class Md2Html {
    private static final String[] tag;
    private static final String[] html;
    private static final Map<String, Integer> indexOfTag;
    private static final int tagMaxSize;
    static {
        tag = new String[]{ "*",  "_",  "**",     "__",     "--", "`",    "<<",  "}}"};
        html = new String[]{"em", "em", "strong", "strong", "s",  "code", "ins", "del"};

        indexOfTag = new HashMap<>();
        int tempTagMaxSize = 0;
        for (int i = 0; i < tag.length; i++) {
            indexOfTag.put(tag[i], i);
            tempTagMaxSize = Math.max(tempTagMaxSize, tag[i].length());
        }
        tagMaxSize = tempTagMaxSize;
        indexOfTag.put(">>", indexOfTag.get("<<"));
        indexOfTag.put("{{", indexOfTag.get("}}"));
    }

    private static boolean isHeader(StringBuilder token) {
        int it = 0;
        while (it < token.length() && token.charAt(it) == '#') {
            it++;
        }
        return 0 < it && it < token.length() && token.charAt(it) == ' ';
    }

    private static int levelOfHeader(StringBuilder token) {
        int it = 0;
        while (it < token.length() && token.charAt(it) == '#') {
            it++;
        }
        return it;
    }

    private static Integer getIndexOfTag(String token) {
        Integer index = null;
        int curSize = token.length();
        while (curSize > 0 && index == null) {
            index = indexOfTag.get(token.substring(0, curSize--));
        }
        return index;
    }

    private static StringBuilder parser(StringBuilder token, int start) {
        StringBuilder result = new StringBuilder();
        Stack<Integer> preTags = new Stack<>();
        boolean preIsSlash = false;
        for (int i = start; i < token.length(); i++) {
            char c = token.charAt(i);
            if (preIsSlash) {
                preIsSlash = false;
                switch (c) {
                    case '<':
                        result.append("&lt;");
                        break;
                    case '>':
                        result.append("&gt;");
                        break;
                    default:
                        result.append(c);
                }
                continue;
            }
            if ((c == '*' || c == '_')
                    && (0 < i && Character.isWhitespace(token.charAt(i - 1)))
                    && (i < token.length() - 1 && Character.isWhitespace(token.charAt(i + 1)))) {
                result.append(c);
                continue;
            }

            String cur = token.substring(i, Math.min(i + tagMaxSize, token.length()));
            Integer curTagIndex = getIndexOfTag(cur);
            if (curTagIndex == null) {
                switch (c) {
                    case '\\':
                        preIsSlash = true;
                        break;
                    case '<':
                        result.append("&lt;");
                        break;
                    case '>':
                        result.append("&gt;");
                        break;
                    case '&':
                        result.append("&amp;");
                        break;
                    default:
                        result.append(c);
                }
            } else {
                result.append("<");
                boolean isOpen = preTags.isEmpty() || (preTags.peek() != curTagIndex);
                if (isOpen) {
                    preTags.push(curTagIndex);
                } else {
                    result.append("/");
                    preTags.pop();
                }
                result.append(html[curTagIndex]).append(">");
                i += tag[curTagIndex].length() - 1;
            }
        }
        return result;
    }

    private static StringBuilder toHtml(StringBuilder token) {
        StringBuilder result = new StringBuilder();
        if (isHeader(token)) {
            int level = levelOfHeader(token);
            result.append("<h").append(level).append(">");
            result.append(parser(token, level + 1));
            result.append("</h").append(level).append(">");
        } else {
            result.append("<p>").append(parser(token, 0)).append("</p>");
        }
        return result;
    }

    public static void main(String[] args) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "utf8"));
            try {
                String line;
                StringBuilder token = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    while (line != null && !line.isEmpty()) {
                        token.append(line).append(System.lineSeparator());
                        line = in.readLine();
                    }
                    if (token.length() != 0) {
                        token.setLength(token.length() - System.lineSeparator().length());
                        result.append(toHtml(token)).append(System.lineSeparator());
                        token = new StringBuilder();
                    }
                }
            } finally {
                in.close();
            }
            try {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "utf8"));
                try {
                    out.write(result.toString());
                } finally {
                    out.close();
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