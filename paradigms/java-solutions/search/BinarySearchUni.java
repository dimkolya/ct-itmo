package search;

public class BinarySearchUni {

    // Pred: a.length >= 1
    //          && ∃ t: ∀ i, j ∈ [0 : t], i < j: args[i] > args[j]
    //                  ∀ i, j ∈ [t+1 : args.length-1], i < j: args[i] < args[j])
    // Let P1 = Pred
    // Post: R = t && (min t ∈ [0 : a.length-1]): ∀ i, j ∈ [t : a.length-1], i < j: a[i] < a[j]
    private static int iterativeBinarySearchUni(final int[] a) {
        // true
        int left = -1;
        // left = -1
        // a.length >= 1
        int right = a.length - 1;
        // left = -1 && right = a.length-1 && right >= left + 1
        // I: (right' == a.length-1 || a[right'] < a[right'+1]) && (left' == -1 || a[left'] >= a[left'+1])
        //     && left <= left' < right' <= right && right' - left' < right - left
        while (left + 1 != right) {
            // I && left + 1 != right
            // left + 1 < right
            int mid = (left + right) / 2;
            // mid = ⌊(left + right) / 2⌋
            //      && (right == a.length-1 || a[right] < a[right+1]) && (left == -1 || a[left] >= a[left+1])
            // left < mid < right
            //      && (right == a.length-1 || a[right] < a[right+1]) && (left == -1 || a[left] >= a[left+1])
            if (a[mid] < a[mid + 1]) {
                // a[mid] < a[mid+1] && left < mid < right
                //      && (right == a.length || a[right] < a[right+1]) && (left == -1 || a[left] >= a[left+1])
                //      && P1
                right = mid;
                // left' = left && right' = mid && a[mid] < a[mid+1] && left < mid < right
                //      && (right == a.length || a[right] < a[right+1] && (left == -1 || a[left] >= a[left+1])
                //      && P1
                // a[right'] <= a[right'+1] && right' - left' < right - left && (left' == -1 || a[left'] >= a[left+1])
                //      && P1
            } else {
                // a[mid] >= a[mid+1] && left < mid < right
                //      && (right == a.length || a[right] < a[right+1]) && (left == -1 || a[left] >= a[left+1])
                //      && P1
                left = mid;
                // left' = mid && right' = right && a[mid] >= a[mid+1] && left < mid < right
                //      && (right == a.length || a[right] < a[right+1] && (left == -1 || a[left] >= a[left+1])
                //      && P1
                // (right' == a.length-1 || a[right'] < a[right'+1]) && right' - left' < right - left && a[left'] >= a[left+1]
                //      && P1
            }
            // (right' == a.length-1 || a[right'] < a[right'+1]) && (left' == -1 || a[left'] >= a[left'+1])
            //      && left <= left' < right' <= right && right' - left' < right - left
            //      && P1
        }
        // (right == a.length-1 || a[right] < a[right+1]) && (left == -1 || a[left] >= a[left+1])
        //      && P1 && left' + 1 == right'
        // Post: right = t && (min t ∈ [0 : a.length-1]): ∀ i, j ∈ [t : a.length-1], i < j: a[i] < a[j]
        return right;
    }

    // Pred: a.length >= 1
    //          && ∃ t: ∀ i, j ∈ [0 : t], i < j: args[i] > args[j]
    //                  ∀ i, j ∈ [t+1 : args.length-1], i < j: args[i] < args[j])
    // Post: R = t && (min t ∈ [0 : a.length-1]): ∀ i, j ∈ [t : a.length-1], i < j: a[i] < a[j]
    private static int recursiveBinarySearchUni(final int[] a) {
        // a.length >= 1
        //      && ∃ t: ∀ i, j ∈ [0 : t], i < j: args[i] > args[j]
        //              ∀ i, j ∈ [t+1 : args.length-1], i < j: args[i] < args[j])
        //      && left = -1 && right = a.length-1
        return recursiveBinarySearchUni(a, -1, a.length - 1);
    }

    // Pred: a.length >= 1
    //          && ∃ t: ∀ i, j ∈ [0 : t], i < j: args[i] > args[j]
    //                  ∀ i, j ∈ [t+1 : args.length-1], i < j: args[i] < args[j])
    //          && (right == a.length-1 || a[right] < a[right+1]) && (left == -1 || a[left] >= a[left+1])
    // Let P2 = Pred
    // Post: R = t && (min t ∈ [0 : a.length-1]): ∀ i, j ∈ [t : a.length-1], i < j: a[i] < a[j]
    private static int recursiveBinarySearchUni(final int[] a, int left, int right) {
        // P2
        if (left + 1 == right) {
            // (right == a.length-1 || a[right] < a[right+1]) && (left == -1 || a[left] >= a[left+1])
            //      && P1 && left' + 1 == right'
            // Post: right = t && (min t ∈ [0 : a.length-1]): ∀ i, j ∈ [t : a.length-1], i < j: a[i] < a[j]
            return right;
        }
        // left + 1 != right && P2
        // left + 1 < right
        int mid = (left + right) / 2;
        // mid = ⌊(left + right) / 2⌋
        //      && (right == a.length-1 || a[right] < a[right+1]) && (left == -1 || a[left] >= a[left+1])
        // left < mid < right &&
        //      && (right == a.length-1 || a[right] < a[right+1]) && (left == -1 || a[left] >= a[left+1])
        if (a[mid] < a[mid + 1]) {
            // a[mid] < a[mid+1] && left < mid < right
            //      && (right == a.length-1 || a[right] < a[right+1]) && (left == -1 || a[left] >= a[left+1])
            //      && P2
            // a[right'] <= a[right'+1] && right' - left' < right - left && (left' == -1 || a[left'] >= a[left+1])
            //      && P2
            return recursiveBinarySearchUni(a, left, mid);
        } else {
            // a[mid] >= a[mid+1] && left < mid < right
            //      && (right == a.length-1 || a[right] < a[right+1]) && (left == -1 || a[left] >= a[left+1])
            //      && P2
            // (right' == a.length-1 || a[right'] < a[right'+1]) && right' - left' < right - left && a[left'] >= a[left+1]
            //      && P2
            return recursiveBinarySearchUni(a, mid, right);
        }
    }

    // Pred: args.length >= 1 && ∀ i ∈ [0 : args.length-1]: args[i] is integer
    //          && ∃ t: ∀ i, j ∈ [0 : t], i < j: args[i] > args[j]
    //                  ∀ i, j ∈ [t+1 : args.length-1], i < j: args[i] < args[j]
    // Let P = Pred
    // Post: R = t && (min t ∈ [0 : args.length-1]): ∀ i, j ∈ [t : args.length-1], i < j: args[i] < args[j]
    public static void main(String[] args) {
        // P
        int[] a = new int[args.length];
        // a.length == args.length && P
        // I: (i' = 0 || i' = i + 1) && i ∈ [0 : args.length-1]) && a.length == args.length && P
        for (int i = 0; i < args.length; i++) {
            // i ∈ [0 : args.length-1] && a.length == args.length && P
            a[i] = Integer.parseInt(args[i]);
            // i ∈ [0 : args.length-1]: a[i] = args[i] && a.length == args.length && P
        }
        // Post: ∀ i ∈ [0 : args.length-1]: a[i] = args[i] && a.length == args.length && P
        // ∃ t: ∀ i, j ∈ [0 : t], i < j: a[i] > a[j]
        //      ∀ i, j ∈ [t+1: args.length-1], i < j: a[i] < a[j]
        int ans = iterativeBinarySearchUni(a);
        // Post: ans = t && (min t ∈ [0 : a.length-1]): ∀ i, j ∈ [t : a.length-1], i < j: a[i] < a[j]
        //       && a = args
        // sout: ans = t && (min t ∈ [0 : args.length-1]): ∀ i, j ∈ [t : args.length-1], i < j: args[i] < args[j]
        System.out.println(ans);
    }
}