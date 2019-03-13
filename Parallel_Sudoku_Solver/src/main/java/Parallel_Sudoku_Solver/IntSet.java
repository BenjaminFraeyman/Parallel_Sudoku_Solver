package Parallel_Sudoku_Solver;

/*
 * This class is specifically designed to store the numbers 1-9.
 * It has constant-time add, remove, contains, and clear methods.
 */
public class IntSet {
    /*
     * Each bit represents the presence of a number as follows:
     *    bits = .... .... .... ....
     *                  98 7654 321
     * That is, the n-th bit is one if the set contains n.
     * For example, to represent the set {1, 2, 6, 8, 9}:
     *    bits = 0000 0011 0100 0110
     *                  98 7654 321
     */
    private short bits;
    private short size;

    public IntSet() {
        this.bits = 0;
        this.size = 0;
    }

    public void add(int n) {
//        this.size += ~(this.bits >> n) & 1;
        if (!this.contains(n)) {
            this.bits |= (1 << n);
            this.size += 1;
        }
    }

    public void remove(int n) {
//        this.size -= (this.bits >> n) & 1;
        if (this.contains(n)) {
            this.bits &= ~(1 << n);
            this.size -= 1;
        }
    }

    public void clear() {
        this.bits = 0;
        this.size = 0;
    }

    public boolean contains(int n) {
        return (this.bits & (1 << n)) > 0;
    }

    public int[] toArray() {
        int[] result = new int[this.size];
        short b = this.bits;
        int k = 0, n = 1;
        while (b != 0) {
            b >>= 1;
            if ((b & 1) > 0)
                result[k++] = n;
            n += 1;
        }
        return result;
    }

    public int size() {
        return size;
    }

    public int getFirst() {
        short b = this.bits;
        int k = 0, n = 1;
        while (b != 0) {
            b >>= 1;
            if ((b & 1) > 0)
                return n;
            n += 1;
        }
        return -1;
    }

}