import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Day9 {
    private static List<Long> readData() {
        Scanner sc = new Scanner(System.in);
        List<Long> data = new ArrayList<>();

        while (sc.hasNextLong()) {
            data.add(sc.nextLong());
        }

        sc.close();
        return data;
    }
    
    private static void task1(List<Long> data) {
        final int PREAMBLE_SIZE = 25;
        long firstInvalid = data.get(PREAMBLE_SIZE);
        for (int i = PREAMBLE_SIZE; i < data.size(); i++) {
            long num = data.get(i);
            boolean valid = false;

            for (int j = i - PREAMBLE_SIZE; j < i - 1; j++) {
                long n1 = data.get(j);
                for (int k = j + 1; k < i; k++) {
                    long n2 = data.get(k);
                    if (num == n1 + n2) {
                        valid = true;
                        break;
                    }
                }
                if (valid) {
                    break;
                }
            }

            if (!valid) {
                firstInvalid = data.get(i);
                break;
            }
        }

        System.out.println(String.format("First invalid: %d", firstInvalid));
    }

    private static void task2(List<Long> data) {
        final long TARGET = 3199139634L;
        boolean done = false;
        long weakness = 0;
        
        for (int start = 0; start < data.size() - 1; start++) {
            long min = Long.MAX_VALUE;
            long max = Long.MIN_VALUE;
            long sum = data.get(start);
            for (int end = start + 1; end < data.size(); end++) {
                long num = data.get(end);
                min = Math.min(min, num);
                max = Math.max(max, num);
                sum += num;
                if (sum == TARGET) {
                    done = true;
                    weakness = max + min;
                    break;
                } else if (sum > TARGET) {
                    break;
                }
            }

            if (done) {
                break;
            }
        }

        System.out.println(String.format("Encryption Weakness: %d", weakness));
    }
    

    public static void main(String[] args) {
        List<Long> data = readData();
        task1(data);
        task2(data);
    }
}
