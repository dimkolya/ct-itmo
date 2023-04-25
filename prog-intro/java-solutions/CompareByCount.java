import java.util.Comparator;

public class CompareByCount implements Comparator<Triple> {
    public int compare(Triple a, Triple b) {
        if (a.getCount() == b.getCount()) {
            return Integer.compare(a.getPos(), b.getPos());
        } else {
            return Integer.compare(a.getCount(), b.getCount());
        }
    }
}