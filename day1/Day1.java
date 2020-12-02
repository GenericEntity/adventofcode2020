import java.util.HashSet;
import java.util.Scanner;

public class Day1 {
    static void sum2() {
        HashSet<Integer> amts = new HashSet<>();
        final int TARGET = 2020;

        Scanner sc = new Scanner(System.in);
        
        while (sc.hasNextInt()) {
            int i = sc.nextInt();
            amts.add(i);
        }

        sc.close();

        boolean found = false;
        for (int amt : amts) {
            int other = TARGET - amt;
            if (amts.contains(other)) {
                System.out.println(String.format("%d, %d. Product: %d", amt, other, amt * other));
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println(String.format("No pair summing to %d found!", TARGET));
        }
    }

    static void sum3() {
        HashSet<Integer> amts = new HashSet<>();
        final int TARGET = 2020;

        Scanner sc = new Scanner(System.in);
        
        while (sc.hasNextInt()) {
            int i = sc.nextInt();
            amts.add(i);
        }

        sc.close();

        boolean found = false;
        for (int amt1 : amts) {
            for (int amt2 : amts) {
                int other = TARGET - amt1 - amt2;
                if (amts.contains(other)) {
                    System.out.println(String.format("%d, %d, %d. Product: %d", amt1, amt2, other, amt1 * amt2 * other));
                    found = true;
                    break;
                }
            }
            if (found) {
                break;
            }
        }

        if (!found) {
            System.out.println(String.format("No pair summing to %d found!", TARGET));
        }
    }

    public static void main(String[] args) {
        sum3();
    }
}
