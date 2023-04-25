package search;

public class BinarySearch {

    // Pred: a.length == 0 || ∀ i, j ∈ [0 : a.length-1], i < j: a[i] >= a[j]
    // Let P1 = Pred
    // Post: R = i && (min i ∈ [0 : a.length-1]): a[i] <= a[0], ∃ i ∈ [0 : a.length-1]: a[i] <= x
    //       R = a.length, else
    private static int iterativeBinarySearch(final int[] a, int x) {
        // true
        int left = -1;
        // left = -1
        // left = -1
        int right = a.length;
        // left = -1 && right = a.length
        // I: (left' == -1 || a[left'] > x) && (right' == a.length || a[right'] <= x)
        //     && left <= left' < right' <= right && right' - left' < right - left
        //     && P1
        while (left + 1 != right) {
            // I && left + 1 != right
            // left + 1 < right && (left == -1 || a[left] > x) && (right == a.length || a[right] <= x)
            int mid = (left + right) / 2;
            // mid = ⌊(left + right) / 2⌋ && (left == -1 || a[left] > x) && (right == a.length || a[right] <= x)
            // left < mid < right && a[left] > x >= a[right]
            //      && (left == -1 || a[left] > x) && (right == a.length || a[right] <= x)
            if (a[mid] > x) {
                // a[mid] > x && left < mid < right
                //      && (left == -1 || a[left] > x) && (right == a.length || a[right] <= x)
                //      && P1
                left = mid;
                // left' = mid && right' = right && a[mid] > x && left < mid < right
                //      && (left == -1 || a[left] > x) && (right == a.length || a[right] <= x)
                //      && P1
                // a[left'] > x && right' - left' < right - left && (right == a.length || a[right] <= x)
                //      && P1
            } else {
                // a[mid] <= x && left < mid < right
                //      && (left == -1 || a[left] > x) && (right == a.length || a[right] <= x)
                //      && P1
                right = mid;
                // left' = left && right' = mid && a[mid] <= x && left < mid < right
                //      && (left == -1 || a[left] > x) && (right == a.length || a[right] <= x)
                //      && P1
                // a[right'] <= x && right' - left' < right - left && (left == -1 || a[left] > x)
                //      && P1
            }
            // (left' == -1 || a[left'] > x) && (right' == a.length || a[right'] <= x)
            //      && left <= left' < right' <= right && right' - left' < right - left
            //      && P1
        }
        // (left' == -1 || a[left'] > x) && (right' == a.length || a[right'] <= x)
        //     && left <= left' < right' <= right && right' - left' < right - left
        //     && P1 && left + 1 == right
        // Post: right = 0, a.length == 0 || a[0] > x
        //       a[right] <= x < a[right - 1] && 0 <= right <= a.length-1
        return right;
    }

    // Pred: a.length == 0 || ∀ i, j ∈ [0 : a.length-1], i < j: a[i] >= a[j]
    // Post: R = i && (min i ∈ [0 : a.length-1]): a[i] <= a[0], ∃ i ∈ [0 : a.length-1]: a[i] <= x
    //       R = a.length, else
    private static int recursiveBinarySearch(final int[] a, int x) {
        // a.length == 0 || ∀ i, j ∈ [0 : a.length-1], i < j: a[i] >= a[j] && left = -1 && right = a.length
        return recursiveBinarySearch(a, x, -1, a.length);
    }

    // Pred: a.length == 0 || ∀ i, j ∈ [0 : a.length-1], i < j: a[i] >= a[j]
    //        && (left' == -1 || a[left'] > x) && (right' == a.length || a[right'] <= x) && left < right
    // Let P2 = Pred
    // Post: R = i && (min i ∈ [0 : a.length-1]): a[i] <= a[0], ∃ i ∈ [0 : a.length-1]: a[i] <= x
    //       R = a.length, else
    private static int recursiveBinarySearch(final int[] a, int x, int left, int right) {
        // P2
        if (left + 1 == right) {
            // left + 1 == right && P2
            // Post: right = 0, a.length == 0 || a[0] > x
            //       a[right] <= x && a[right - 1] > x && 0 <= right <= a.length-1 && P2
            return right;
        }
        // left + 1 != right && P2
        // left + 1 < right && P2
        int mid = (left + right) / 2;
        // mid = ⌊(left + right) / 2⌋ && (left == -1 || a[left] > x) && (right == a.length || a[right] <= x) && P2
        // left < mid < right && a[left] > x >= a[right]
        //      && (left == -1 || a[left] > x) && (right == a.length || a[right] <= x) && P2
        if (a[mid] > x) {
            // a[mid] > x && left < mid < right
            //      && (left == -1 || a[left] > x) && (right == a.length || a[right] <= x) && P2
            // left' = mid && right' = right && a[mid] > x && left < mid < right
            //      && (left == -1 || a[left] > x) && (right == a.length || a[right] <= x) && P2
            // a[left'] > x && right' - left' < right - left && (right == a.length || a[right] <= x) && P2
            return recursiveBinarySearch(a, x, mid, right);
        } else {
            // a[mid] <= x && left < mid < right
            //      && (left == -1 || a[left] > x) && (right == a.length || a[right] <= x) && P2
            // left' = left && right' = mid && a[mid] <= x && left < mid < right
            //      && (left == -1 || a[left] > x) && (right == a.length || a[right] <= x) && P2
            // a[right'] <= x && right' - left' < right - left && (left == -1 || a[left] > x) && P2
            return recursiveBinarySearch(a, x, left, mid);
        }
    }

    // Pred: args.length >= 1 && ∀ i ∈ [0 : args.length-1]: args[i] is integer
    //       && ∀ i, j ∈ [1 : args.length-1], i < j: args[i] >= args[j]
    // Let P = Pred
    // Post: R = i && (min i ∈ [1 : args.length-1]): args[i] <= args[0], ∃ i ∈ [1 : args.length-1]: args[i] <= args[0]
    //       R = args.length, else
    public static void main(String[] args) {
        // args.length >= 1 && args[0] is integer
        int x = Integer.parseInt(args[0]);
        // x = args[0]
        // P
        int[] a = new int[args.length - 1];
        // a.length == args.length-1 && x = args[0] && P
        // I: (i' = 1 || i' = i + 1) && i ∈ [1 : args.length-1]) && a.length == args.length-1 && x = args[0] && P
        for (int i = 1; i < args.length; i++) {
            // i ∈ [1 : args.length-1] && a.length == args.length-1 && x = args[0] && P
            // i - 1 ∈ [0 : a.length-1] && a.length == args.length-1 && x = args[0] && P
            a[i - 1] = Integer.parseInt(args[i]);
            // i - 1 ∈ [0 : a.length-1]: a[i] = args[i + 1] && a.length == args.length-1 && x = args[0] && P
            // i ∈ [1 : args.length-1]: a[i - 1] = args[i] && a.length == args.length-1 && x = args[0] && P
        }
        // Post: ∀ i ∈ [1 : args.length-1]: a[i - 1] = args[i] && a.length == args.length-1 && x = args[0] && P
        // ∀ i ∈ [0 : a.length-1]: a[i] = args[i + 1] && a.length == args.length-1 && x = args[0] && P
        // a.length==0 || ∀ i,j ∈ [0:a.length-1], i<j: a[i]>=a[j] && ∀ i ∈ [0:a.length-1]: a[i]=args[i+1] && x=args[0]
        int ans = recursiveBinarySearch(a, x);
        // Post: ans = i && (min i ∈ [0 : a.length-1]): a[i] <= x, ∃ i ∈ [0 : a.length-1]: a[i] <= x
        //       ans = a.length, else
        //       && ∀ i ∈ [0 : a.length-1]: a[i] = args[i + 1] && x = args[0]
        // sout: ans = i && (min i ∈ [1 : args.length-1]): args[i] <= args[0], ∃ i ∈ [1 : args.length-1]: args[i] <= args[0]
        //       ans = args.length, else
        System.out.println(ans);
    }
}