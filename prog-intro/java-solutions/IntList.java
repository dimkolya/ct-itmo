import java.util.Arrays;

public class IntList {
    private int size;
    private int[] arr;

    public IntList() {
        arr = new int[2];
        size = 0;
    }

    public void append(int value) {
        if (size == arr.length) {
            arr = Arrays.copyOf(arr, 3 * arr.length / 2);
        }
        arr[size++] = value;
    }

    public int get(int position) {
        return arr[position];
    }

    // public void set(int position, int newValue) {
    //     arr[position] = newValue;
    // }

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