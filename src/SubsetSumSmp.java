//******************************************************************************
//
// File:    SubsetSumSmp.java
// 
// This Java source file is part of Team Research Investigation, Subset Sum 
// Problem.
//
// Team: Transcendence
// 
//******************************************************************************

import edu.rit.pj2.Loop;
import edu.rit.pj2.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Class SubsetSumSmp is parallel program that checks if there exists any subset
 * for the set S or n objects from input file using dynamic 2D table.
 * <P>
 * Usage: <TT>java pj2 SubsetSumSmp <I><input_file></I> <I>[p]</I>
 * 
 * <input_file> - file containing n objects for a set.
 * [p] - optional parameter p to print the final 2D table.
 * 
 * @author Harshit
 * @version 15-Nov-2015
 */
public class SubsetSumSmp extends Task {

    boolean printTable = false;
    int n = 0; // Total number of elements from input_file.

    /**
     * Main program.
     */
    @Override
    public void main(String[] args) throws Exception {

        // Check only one command-line argument is received.
        if (args.length < 1 | args.length > 2) {
            usage();
            return;
        }

        // Read input file and insert all Integer objects into an arraylist.
        File file = new File(args[0]);
        ArrayList<Integer> int_objects = new ArrayList<Integer>();
        try {
            Scanner s = new Scanner(file);
            while (s.hasNext()) {
                try {
                    int_objects.add(s.nextInt());
                } catch (InputMismatchException | NumberFormatException e) {
                    usage("Input file contents are invalid. Please insert positive integers only");
                }
            }
            s.close();
        } catch (FileNotFoundException e) {
            usage("The input file is not found in the current directory. Please recheck if the filename is entered correctly.");
            return;
        }

        // Check if second argument is received for printing table.
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("p"))
                printTable = true;
            else {
                usage();
                return;
            }
        }

        n = int_objects.size();

        // Set of n objects.
        int[] set = new int[n + 1];

        long sum = 0;
        // Set elements of a set starting from index 1.
        for (int i = 1; i < int_objects.size() + 1; i++) {
            set[i] = int_objects.get(i - 1);
            sum += set[i];
        }

        if ((sum % 2) != 0) {
            usage("No subset can be found as sum of all elements is odd. It must be even for the 2D table to be made.");
            return;
        }

        System.out.println("Total number of elements in a set: " + n);

        // To follow our variant of subset sum, target sum for subsets would be
        // half_sum.
        long half_sum = sum / 2;
        System.out.println("Target Sum: " + half_sum);

        // Total number of 64 bits of words required to store sum.
        int words = (int) (Math.ceil(((double) (1 + sum)) / 64));

        // Initialize dynamic programming 2D table.
        Table[] table = new Table[n + 1];
        parallelFor(0, n).exec(new Loop() {
            public void run(int i) throws Exception {
                table[i] = new Table(words);

                // Base case: If target sum is 0, then set bit to 1.
                table[i].a[0] = 1;
            }
        });

        // Fill the table.
        for (int k = 1; k <= n; k++) {

            int pos = set[k];
            final int i = k;

            // Copy previous row to current row.
            parallelFor(0, words - 1).schedule(guided).exec(new Loop() {
                /**
                 * Execute the parallel statements.
                 */
                @Override
                public void run(int j) throws Exception {
                    table[i].a[j] = table[i - 1].a[j];
                }
            });

            // If cell a[i-1][j] is 1, then the element a[i][j+set[i]] will be
            // 1.
            parallelFor(0, words - 1).schedule(guided).exec(new Loop() {
                /**
                 * Execute the parallel statements.
                 */
                @Override
                public void run(int w) throws Exception {
                    for (int j = 0; j <= 63; j++) {
                        int thisPos = w * 64 + j;
                        int thatPos = pos + thisPos;
                        if (((checkBitInArray(table[i - 1].a, thisPos)) == 1)
                                && ((thatPos) <= half_sum)) {
                            setBitInArray(table[i].a, thatPos);
                        }
                    }
                }
            });
        }

        // Check a[n][target sum] is 1 or not.
        if (checkBitInArray(table[n].a, (int) half_sum) == 1)
            System.out.println("Subset Sum exists.");
        else
            System.out.println("Subset Sum doesn't exist.");

        if (printTable) {
            printTable(set, half_sum, table, words);
        }
    }

    /**
     * Set conceptually a bit to 1 at given index in given 64 bit words array.
     * 
     * @param a
     *            64 bit words array.
     * @param index
     *            index to set a bit.
     */
    private void setBitInArray(long[] a, int index) {

        // Shifting 1 left by number of bits.
        int bit = index % 64;
        int t = (1 << bit);

        // Updating the current word
        int word = index / 64;
        a[word] = a[word] | t;
    }

    /**
     * Check if a bit is conceptually set to 1 at given index in given 64 bit
     * words array.
     * 
     * @param a
     *            64 bit words array.
     * @param index
     *            index to set a bit.
     * @return 1 or 0 based on bit set or not.
     */
    private int checkBitInArray(long[] a, int index) {

        // Shifting 1 left by number of bits.
        int bit = index % 64;
        int t = (1 << bit);

        // Check if conceptual bit is set or not at given index.
        int word = index / 64;
        return (a[word] & t) > 0 ? 1 : 0;
    }

    /**
     * Print the dynammic programming 2D table.
     * 
     * @param set
     *            a set containing n input elements.
     * @param half_sum
     *            target sum for this subset sum problem.
     * @param a
     *            array representing 2D table.
     * @param words
     *            total number of 64 bit words possible from the total sum.
     */
    private void printTable(int[] set, long half_sum, Table[] table, int words) {

        System.out.print("   S   W   ");
        for (int j = 0; j <= half_sum; j++) {
            System.out.print(" " + String.format("%3d", j));
        }
        System.out.println();
        int sum = 0;
        for (int i = 0; i <= n; i++) {
            sum = sum + set[i];
            System.out.print(" " + String.format("%3d", sum) + " "
                    + String.format("%3d", set[i]) + "  "
                    + String.format("%3d", i));
            for (int j = 0; j <= half_sum; j++) {
                if ((checkBitInArray(table[i].a, j)) == 1)
                    System.out.print(" 1  ");
                else
                    System.out.print(" 0  ");
            }
            System.out.println();
        }
    }

    /**
     * Print an error message.
     * 
     * @param msg
     *            error message
     */
    private static void usage(String msg) {
        System.err.printf("SubsetSumSeq: %s%n", msg);
        usage();
    }

    /**
     * Print a usage message.
     */
    private static void usage() {
        System.err.println("Usage: java pj2 SubsetSumSeq <input_file> [p]");
        System.err
                .println("<input_file> = file containing n objects for a set.");
        System.err
                .println("[p] - optinal parameter p to print the final 2D table.");
    }

    /**
     * Table class to store word array representing columns of the table.
     * 
     * @author Harshit
     */
    class Table {

        long[] a;

        public Table(int words) {
            a = new long[words];
        }
    }
}
