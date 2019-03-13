package Parallel_Sudoku_Solver;

import java.util.Arrays;

public class SudokuBoard {

    public static int EMPTY_ENTRY = 0;

    protected static int toSubmatrixIndex(int i, int j) {
        return (i / 3) * 3 + (j / 3);
    }

    protected static int[] fromSubmatrixIndex(int k) {
        return new int[]{3 * (k / 3), 3 * (k % 3)};
    }

    public static class SudokuException extends Exception {
        public SudokuException(String message) {
            super(message);
        }
    }

    protected int[][] entries;      /* The 9x9 grid of numbers. */
    protected int nEntriesFilled;   /* Stores the number of entries filled */
    protected IntSet[][] options;   /* options[i][j] is the set of numbers that might fill entries[i][j] */
    protected IntSet[] rowOptions;  /* rowOptions[i] gives the set of numbers that can be placed in row i */
    protected IntSet[] colOptions;  /* colOptions[j] gives the set of numbers that can be placed in column j */

    /* A submatrix is one of the nine 3x3 grids on the board that must contain the numbers 1-9.
     * Submatrices are indexed as follows:
     *   0 1 2
     *   3 4 5
     *   6 7 8
     * So `submatrixOptions[0]` gives the set of remaining numbers that need to be placed in the top-left submatrix.
     */
    protected IntSet[] submatrixOptions;

    public SudokuBoard() {
        this.init();
        assert(this.isValid());
        this.updateComponentOptions();
        this.updateOptions();
    }

    public SudokuBoard(int[][] array) {
        this.init();
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                // anything not in the range [1, 9] is assumed to be a blank space
                if (1 <= array[i][j] && array[i][j] <= 9) {
                    this.entries[i][j] = array[i][j];
                    this.nEntriesFilled++;
                }
            }
        }
        this.updateComponentOptions();
        this.updateOptions();
    }

    public SudokuBoard(String s) throws SudokuException {
        this.init();
        if (s.length() < 81)
            throw new SudokuException("Not enough characters in string to construct a board.");
        for (int i = 0; i < Math.min(81, s.length()); ++i) {
            int c = s.charAt(i);
            if ('1' <= c && c <= '9') {
                this.entries[i / 9][i % 9] = c - '0';
                this.nEntriesFilled++;
            }
        }
        this.updateComponentOptions();
        this.updateOptions();
    }

    public SudokuBoard(SudokuBoard other)  {
        this.init();
        this.copyFrom(other);
    }

    protected void init() {
        /* Java arrays are initialized to a default value -- zero for ints.  */
        this.entries = new int[9][9];
        this.nEntriesFilled = 0;

        this.options = new IntSet[9][9];
        this.submatrixOptions = new IntSet[9];
        this.colOptions = new IntSet[9];
        this.rowOptions = new IntSet[9];

        for (int i = 0; i < 9; ++i) {
            this.rowOptions[i] = new IntSet();
            this.colOptions[i] = new IntSet();
            this.submatrixOptions[i] = new IntSet();
            for (int j = 0; j < 9; ++j) {
                this.options[i][j] = new IntSet();
            }
        }
    }

    public void copyFrom(SudokuBoard other) {
        this.nEntriesFilled = 0;
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                // anything not in the range [1, 9] is assumed to be a blank space
                int n = other.getEntry(i, j);
                if (1 <= n && n <= 9) {
                    this.entries[i][j] = n;
                    this.nEntriesFilled++;
                } else {
                    this.entries[i][j] = EMPTY_ENTRY;
                }
            }
        }
        this.updateComponentOptions();
        this.updateOptions();
    }

    public boolean equals(SudokuBoard other) {
        return Arrays.equals(this.entries, other.entries);
    }

    public boolean isFilled() {
        return this.nEntriesFilled == 81;
    }

    public int getEntry(int i, int j) {
        return this.entries[i][j];
    }

    /** Set the entry at (i, j) to n only if the value has not been set **/
    public void setEntry(int i, int j, int n) {
        if (this.entries[i][j] > 0)
            return;

        this.entries[i][j] = n;
        this.nEntriesFilled++;

        this.rowOptions[i].remove(n);
        this.colOptions[j].remove(n);
        this.submatrixOptions[toSubmatrixIndex(i, j)].remove(n);

        this.updateOptions();
    }

    /** Update the `options` array of numbers that can go in each space **/
    protected void updateOptions() {
        for (int i = 0; i < 9 ; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.options[i][j].clear();
                // If the space is unfilled, check the row, col, and submatrix
                // to see which numbers can be placed there
                if (this.entries[i][j] < 1) {
                    for (int n = 1; n <= 9; ++n) {
                        if (this.colOptions[j].contains(n)
                                && this.rowOptions[i].contains(n)
                                && this.submatrixOptions[toSubmatrixIndex(i, j)].contains(n)) {
                            this.options[i][j].add(n);
                        }
                    }
                }
            }
        }
    }

    /** Update the row, column, and submatrix options arrays **/
    protected void updateComponentOptions() {
        for (int k = 0; k < 9; ++k) {
            this.rowOptions[k].clear();
            this.colOptions[k].clear();
            this.submatrixOptions[k].clear();
            for (int n = 1; n <= 9; ++n) {
                if (this.countInCol(k, n) == 0)
                    this.colOptions[k].add(n);
                if (this.countInRow(k, n) == 0)
                    this.rowOptions[k].add(n);
                if (this.countInSubmatrix(k, n) == 0)
                    this.submatrixOptions[k].add(n);
            }
        }
    }

    /** Count the number of occurrences of n in the given submatrix **/
    protected int countInSubmatrix(int submatrix, int n) {
        final int[] ijCoords = fromSubmatrixIndex(submatrix);
        int count = 0;
        for (int i = ijCoords[0]; i < ijCoords[0] + 3; ++i) {
            for (int j = ijCoords[1]; j < ijCoords[1] + 3; ++j) {
                if (this.entries[i][j] == n)
                    count++;
            }
        }
        return count;
    }

    /** Count the number of occurrences of n in the given row **/
    protected int countInRow(int row, int n) {
        int count = 0;
        for (int j = 0; j < 9; ++j) {
            if (this.entries[row][j] == n)
                count++;
        }
        return count;
    }

    /** Count the number of occurrences of n in the given column **/
    protected int countInCol(int col, int n) {
        int count = 0;
        for (int i = 0; i < 9; ++i) {
            if (this.entries[i][col] == n)
                count++;
        }
        return count;
    }

    /** Return false if a number is in any row, column, or submatrix more than once. **/
    public boolean isValid() {
        for (int n = 1; n <= 9; ++n) {
            for (int i = 0; i < 9; ++i)
                if (this.countInRow(i, n) > 1)
                    return false;
            for (int j = 0; j < 9; ++j)
                if (this.countInCol(j, n) > 1)
                    return false;
            for (int k = 0; k < 9; ++k)
                if (this.countInSubmatrix(k, n) > 1)
                    return false;
        }
        return true;
    }

    /**
     * Return true if the current state of the board is a valid solution.
     * Specifically, this checks that there is exactly one occurrence of each
     * number in each row, in each column, and in each submatrix.
     */
    public boolean isSolution() {
        for (int i = 0; i < 9; ++i) {
            for (int n = 1; n <= 9; ++n) {
                if (this.countInCol(i, n) != 1)
                    return false;
                if (this.countInRow(i, n) != 1)
                    return false;
                if (this.countInSubmatrix(i, n) != 1)
                    return false;
            }
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.entries.length; ++i) {
            for (int j = 0; j < this.entries[i].length; ++j) {
                if (this.entries[i][j] < 1)
                    sb.append('.');
                else
                    sb.append(this.entries[i][j]);
            }
            if (i < this.entries.length - 1)
                sb.append('\n');
        }
        return sb.toString();
    }

    /** Same as toString(), but with no whitespace. **/
    public String rawString() {
        return this.toString().replaceAll("\\s+", "");
    }

    public void printData() {
        System.out.println("-----------");
        System.out.println("Filled squares: " + this.nEntriesFilled);

        System.out.println("----Possibilities---");
        for (IntSet[] option: this.options) {
            for (IntSet anOption : option) {
                if (anOption.size() == 0) {
                    System.out.print('.');
                } else {
                    for (int n : anOption.toArray()) {
                        System.out.print(n);
                    }
                }
                System.out.print(" ");
            }
            System.out.println();
        }

        System.out.println("-----Submatrix Options-----");
        for(int k = 0; k < this.submatrixOptions.length; k++){
            System.out.print("Box " + k + ": ");
            for (int n: this.submatrixOptions[k].toArray())
                System.out.print(n);
            System.out.println();
        }

        System.out.println("-----Row Options-----");
        for(int i = 0; i < this.rowOptions.length; i++){
            System.out.print("Row " + i + ": ");
            for (int n: this.rowOptions[i].toArray())
                System.out.print(n);
            System.out.println();

        }

        System.out.println("-----Columns Options-----");
        for(int j = 0; j < this.colOptions.length; j++){
            System.out.print("Col " + j + ": ");
            for (int n: this.colOptions[j].toArray())
                System.out.print(n);
            System.out.println();
        }
    }

}
