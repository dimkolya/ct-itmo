public class MyPair {
    public int first;
    public IntList second;

    public MyPair(int first, IntList second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append(first);
        res.append(second);
        return res.toString();
    }
}