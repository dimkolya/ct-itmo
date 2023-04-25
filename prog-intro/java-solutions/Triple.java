public class Triple {
    private final String word;
    private final int pos;
    private final int count;
    public Triple(final String word, final int pos, final int count) {
        this.word = word;
        this.pos = pos;
        this.count = count;
    }
    public String getWord() {
        return word;
    }
    public int getPos() {
        return pos;
    }
    public int getCount() {
        return count;
    }
}