import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Day10 {
    private static List<Integer> readData() {
        Scanner sc = new Scanner(System.in);
        List<Integer> data = new ArrayList<>();

        while (sc.hasNextInt()) {
            data.add(sc.nextInt());
        }

        sc.close();
        return data;
    }
    
    private static void task1(List<Integer> data) {
        List<Integer> copy = new ArrayList<>(data);

        // socket has value 0
        copy.add(0);

        Collections.sort(copy);
        int diff1Count = 0;
        int diff3Count = 0;
        for (int i = 0; i < copy.size() - 1; i++) {
            int diff = copy.get(i + 1) - copy.get(i);
            switch(diff) {
                case 1:
                    diff1Count++;
                    break;
                case 3:
                    diff3Count++;
                    break;
                default:
                    System.out.println(String.format("Difference not 1 or 3 found: %d", diff));
                    break;
            }
        }
        
        // highest adapter to device is always difference of 3
        diff3Count++;

        System.out.println(String.format("Task 1: %d", diff1Count * diff3Count));
    }

    private static void task2(List<Integer> data) {
        final int MAX_DIFF = 3;
        List<Integer> copy = new ArrayList<>(data);

        // socket has value 0
        copy.add(0);

        // we don't add the device value because the device can always only connect
        // to the highest adapter directly so this creates no additional distinct configs

        // get the numbers in topological order
        Collections.sort(copy);

        /**
         * Convert the numbers into an unweighted DAG
         * 
         * Vertices: represent the positions (not values) of the numbers in copy
         * Edges: an edge (u, v) exists if copy[v] - MAX_DIFF <= copy[u] < copy[v]
         */
        ArrayList<ArrayList<Integer>> adjList = new ArrayList<>();
        for (int i = 0; i < copy.size(); i++) {
            adjList.add(new ArrayList<>());
            int num = copy.get(i);

            // Make an edge from num only to numbers within +MAX_DIFF of num
            for (int j = 1; j <= MAX_DIFF; j++) {
                if (i + j >= copy.size()) {
                    break;
                }

                if (copy.get(i + j) <= num + MAX_DIFF) {
                    adjList.get(i).add(i + j);
                }
            }
        }

        // contains number of distinct ways to get from a vertex to the last vertex
        long[] distinctWays = new long[copy.size()];
        Arrays.fill(distinctWays, 0);

        // 1 way from last vertex to itself
        distinctWays[copy.size() - 1] = 1;

        // go in reverse topological order, skipping last (already computed)
        for (int u = copy.size() - 2; u >= 0; u--) {
            // the number of distinct ways to get from u to last is the sum
            // of the number of ways to get from each v (v is a neighbor of u) to last
            for (int v : adjList.get(u)) {
                distinctWays[u] += distinctWays[v];
            }
        }

        System.out.println(String.format("Task 2: %d", distinctWays[0]));
    }
    

    public static void main(String[] args) {
        List<Integer> data = readData();
        task1(data);
        task2(data);
    }
}
