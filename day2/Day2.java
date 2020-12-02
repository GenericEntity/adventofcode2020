import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Datum {
    public final int lower;
    public final int upper;
    public final char letter;
    public final String pw;

    public Datum(int lower, int upper, char letter, String pw) {
        this.lower = lower;
        this.upper = upper;
        this.letter = letter;
        this.pw = pw;
    }
}

public class Day2 {
    private static List<Datum> readInput() {
        final Pattern p = Pattern.compile("(\\d+)-(\\d+) ([a-z]): ([a-z]+)");
        Scanner sc = new Scanner(System.in);
        ArrayList<Datum> input = new ArrayList<>();
        
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            Matcher m = p.matcher(line);
            if (m.find()) {
                int lower = Integer.parseInt(m.group(1));
                int upper = Integer.parseInt(m.group(2));
                char letter = m.group(3).charAt(0);
                String pw = m.group(4);
                input.add(new Datum(lower, upper, letter, pw));
            }
        }
        
        sc.close();

        return input;
    }

    private static void policy1(List<Datum> data) {
        int validCount = 0;

        for (Datum d : data) {
            int lower = d.lower;
            int upper = d.upper;
            char letter = d.letter;
            String pw = d.pw;

            int occurCount = 0;
            for (int i = 0; i < pw.length(); i++) {
                char c = pw.charAt(i);
                if (c == letter) {
                    occurCount++;
                    // Optimization: invalid once it goes beyond the upper bound
                    if (occurCount > upper) {
                        break;
                    }
                }
            }

            if (occurCount >= lower && occurCount <= upper) {
                validCount++;
            }
        }

        System.out.println(String.format("Policy 1 valid count: %d", validCount));
    }

    private static void policy2(List<Datum> data) {
        int validCount = 0;

        for (Datum d : data) {
            // One-based indexing
            int pos1 = d.lower - 1;
            int pos2 = d.upper - 1;
            char letter = d.letter;
            String pw = d.pw;

            int occurCount = 0;

            if (pw.charAt(pos1) == letter) {
                occurCount++;
            }
            if (pw.charAt(pos2) == letter) {
                occurCount++;
            }

            if (occurCount == 1) {
                validCount++;
            }
        }

        System.out.println(String.format("Policy 2 valid count: %d", validCount));
    }

    public static void main(String[] args) {
        List<Datum> data = readInput();
        policy1(data);
        policy2(data);
    }
}
