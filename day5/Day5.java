import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day5 {
    private static List<String> getInput() {
        ArrayList<String> passes = new ArrayList<>();
        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            passes.add(sc.nextLine());
        }

        sc.close();
        return passes;
    }

    private static int computeId(String pass) {
        /**
         * Treat the boarding pass like two binary numbers concatenated.
         * The first 7 digits are the row number: B is 1, F is 0.
         * The last 3 digits are the col number: R is 1, F is 0.
         * 
         * Convert both separately to decimal then compute seat ID.
         */
        final int ROW_LEN = 7;
        final int COL_LEN = 3;

        int pow = ROW_LEN - 1;
        int row = 0;
        for (int i = 0; i < ROW_LEN; i++) {
            char c = pass.charAt(i);
            switch(c) {
                case 'B':
                    row += 1 << pow;
                    break;
                case 'F':
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
            pow--;
        }

        pow = COL_LEN - 1;
        int col = 0;
        for (int i = ROW_LEN; i < ROW_LEN + COL_LEN; i++) {
            char c = pass.charAt(i);
            switch(c) {
                case 'R':
                    col += 1 << pow;
                    break;
                case 'L':
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
            pow--;
        }

        return row * (1 << COL_LEN) + col;
    }

    private static void task1(List<String> passes) {
        int maxId = passes.stream().mapToInt(Day5::computeId).max().getAsInt();
        System.out.println(String.format("Highest Seat ID: %d", maxId));
    }


    private static void task2(List<String> passes) {
        /**
         * Find min, max and sum. Since seat IDs are a consecutive sequence of 
         * numbers, we can get the sum from min to max if all seat IDs were there
         * and subtract the sum we actually have to find the missing seat ID
         * in one pass: O(n) where n is the number of boarding passes.
         * 
         * We assume the input is valid.
         */
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int sum = 0;
        for (String pass : passes) {
            int id = computeId(pass);
            min = Math.min(id, min);
            max = Math.max(id, max);
            sum += id;
        }

        int range = max - min;
        /**
         * This will never have integer division issues because the numerator 
         * can be proven to be always even:
         * Let x = range + 1
         * Let y = 2min + range
         * 
         * The parity of min doesn't matter because 2min is always even
         * If range is odd, then x is even and y is odd. So xy is even.
         * If range is even, then x is odd and y is even. So xy is even.
         */
        int expectedSum = (range + 1) * (2 * min + range) / 2;
        int missingId = expectedSum - sum;
        System.out.println(String.format("Missing Seat Id: %d", missingId));
    }

    public static void main(String[] args) {
        List<String> passes = getInput();
        task1(passes);
        task2(passes);
    }
}
