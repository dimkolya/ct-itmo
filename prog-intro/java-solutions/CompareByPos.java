import java.util.Comparator;

public class CompareByPos implements Comparator<Triple> {
    public int compare(Triple a, Triple b) {
        return Integer.compare(a.getPos(), b.getPos());
    }
}