package Parallel_Sudoku_Solver;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Perf {
    public static double ONE_MIL = 1000000.0;
    public static double ONE_BIL = 1000000000.0;

    public static String[] getPuzzles(InputStream is) throws IOException {
        List<String> puzzles = new LinkedList<String>();
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        while (in.ready()) {
            String s = in.readLine().trim();
            if (!s.isEmpty())
                puzzles.add(s);
        }
        return puzzles.toArray(new String[0]);
    }

    /**
     * @param boards Each inner array of SudokuBoards represents all the solutions to a single puzzle.
     *               Any inner array (e.g. SudokuBoard[i]) may be null, indicating we failed to solve
     *               the puzzle (invalid input puzzle, badly-formatted input, bugs, etc).
     * @return An int[]{nBadSolutions, nUnsolvable} where nUnsolvable is the number of puzzles
     *         for which no solutions were found, and nBadSolutions indicates a "solution" was given
     *         that is not actually a valid solution.
     */
    public static int[] verify(SudokuBoard[][] boards) {
        int nBadSolutions = 0;
        int nUnsolvable = 0;
        for (SudokuBoard[] solns: boards) {
            if (solns == null) {
                nUnsolvable += 1;
            } else {
                for (SudokuBoard b: solns) {
                    if (!b.isSolution())
                        nBadSolutions += 1;
                }
            }
        }
        return new int[]{nBadSolutions, nUnsolvable};
    }

    public static void main(String args[]) throws IOException {
        DecimalFormat df = new DecimalFormat("#.##");
        // read in sudokus
        long start = System.nanoTime();
        System.out.println("------------- Loading -------------");
        InputStream inputstream = new FileInputStream("src\\main\\java\\Sudoku\\Puzzles\\puzzles.txt");
        String[] puzzles = getPuzzles(inputstream);
        String duration = df.format((System.nanoTime() - start) / ONE_MIL);
        System.out.println("Loading sudokus into memory completed:");
        System.out.println("    - Loading took " + duration + " ms");
        
        // solve puzzles
        System.out.println("------------- Solving -------------");
        System.out.println("------------- Sequential -------------");
        System.out.println("Solving " + puzzles.length + " puzzles - Sequential");
        SudokuBoard[][] solutions = null;
        start = System.nanoTime();
        boolean findAll = true;
        solutions = SudokuSolver.run(puzzles, findAll);
        double end = System.nanoTime() - start;
        duration = df.format(end / ONE_BIL);
        String durationPerPuzzle = df.format(end / ONE_MIL / puzzles.length);
        System.out.println("Solving completed!");
        System.out.println("    - Solving took " + duration + " seconds");
        System.out.println("    - Time per puzzle: " + durationPerPuzzle + " ms");
        System.out.println("Verifying results:");
        int[] counts = verify(solutions);
        int nWrong = counts[0];
        int nUnsolvable = counts[1];
        if (nWrong > 0 || nUnsolvable > 0) {
            System.out.println("    - Amount of incorrect solutions: " + nWrong);
            System.out.println("    - Amount of unsolvable sudokus: " + nUnsolvable);
        } else {
            System.out.println("    - No unsolvable sudokus or incorrect solutions!");
        }
        System.out.println("");
        System.out.println("------------- Parallel -------------");
        int numThreads = Runtime.getRuntime().availableProcessors();
        System.out.println("    This cpu has " + numThreads + " threads, we will run up to: " + (numThreads - 1) + " threads!");
        System.out.println("    Solving " + puzzles.length + " puzzles - Parallel");
        System.out.println("");
        for (int currentThreadCount = 1; currentThreadCount < numThreads; currentThreadCount++) {
            //System.out.println("* Upping threadcount with 1 *");            
            System.out.println("Running in parallel with " + currentThreadCount + " threads:");
            solutions = null;
            start = System.nanoTime();
            findAll = true;
            solutions = ParallelSudokuSolver.run(puzzles, currentThreadCount, findAll);
            end = System.nanoTime() - start;
            duration = df.format(end / ONE_BIL);
            durationPerPuzzle = df.format(end / ONE_MIL / puzzles.length);
            System.out.println("    Solving completed!");
            System.out.println("        - Solving took " + duration + " seconds");
            System.out.println("        - Time per puzzle: " + durationPerPuzzle + " ms");
            System.out.println("    Verifying results:");
            counts = verify(solutions);
            nWrong = counts[0];
            nUnsolvable = counts[1];
            if (nWrong > 0 || nUnsolvable > 0) {
                System.out.println("        - Amount of incorrect solutions: " + nWrong);
                System.out.println("        - Amount of unsolvable sudokus: " + nUnsolvable);
            } else {
                System.out.println("        - No unsolvable sudokus or incorrect solutions!");
            }
            System.out.println("");
        }
    }
}
