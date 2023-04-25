import java.util.Comparator;

public class CompareByWord implements Comparator<Triple> {
    public int compare(Triple a, Triple b) {
        if (a.getWord().equals(b.getWord())) {
                return Integer.compare(a.getPos(), b.getPos());
        } else {
            return a.getWord().compareTo(b.getWord());
        }
    }
}