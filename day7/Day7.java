import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Edge {
    public final int dest, weight;

    public Edge(int dest, int weight) {
        this.dest = dest;
        this.weight = weight;
    }
}

public class Day7 {
    static HashMap<String, Integer> colorToIndex = new HashMap<>();

    private static ArrayList<ArrayList<Edge>> parseRules() {
        /**
         * Reads input and builds a directed, weighted graph.
         * 
         * Vertices: bag colors (each assigned an index mapped to by colorToIndex)
         * Edges: an edge (src, dest, weight) exists if bag dest contains weight number
         *  of bags src
         */
        final String NO_BAGS = "no other bags";

        ArrayList<ArrayList<Edge>> adjList = new ArrayList<>();
        int index = 0;
        Scanner sc = new Scanner(System.in);

        Pattern destRegex = Pattern.compile("(\\w+ \\w+) bags contain");
        Pattern srcRegex = Pattern.compile("(" + NO_BAGS + "|(\\d+) (\\w+ \\w+) bags?)");

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            Matcher mDest = destRegex.matcher(line);
            if (mDest.find()) {
                String destColor = mDest.group(1);
                if (!colorToIndex.containsKey(destColor)) {
                    colorToIndex.put(destColor, index++);
                    adjList.add(new ArrayList<>());
                }
                int dest = colorToIndex.get(destColor);
                Matcher mSrc = srcRegex.matcher(line);
                while (mSrc.find()) {
                    String test = mSrc.group(1);
                    if (test.equals(NO_BAGS)) {
                        break;
                    }
                    int weight = Integer.parseInt(mSrc.group(2));
                    String srcColor = mSrc.group(3);
    
                    if (!colorToIndex.containsKey(srcColor)) {
                        colorToIndex.put(srcColor, index++);
                        adjList.add(new ArrayList<>());
                    }
    
                    int src = colorToIndex.get(srcColor);
                    adjList.get(src).add(new Edge(dest, weight));
                }
            }
        }

        sc.close();
        return adjList;
    }

    

    private static void task1(ArrayList<ArrayList<Edge>> adjList) {
        final String TARGET_COLOR = "shiny gold";

        /**
         * BFS starting from target bag color will find all bags that can contain
         * it directly or indirectly
         */
        int n = adjList.size();
        boolean[] visited = new boolean[n];
        Queue<Integer> q = new LinkedList<>();

        Arrays.fill(visited, false);
        int src = colorToIndex.get(TARGET_COLOR);
        visited[src] = true;
        q.offer(src);
        
        int bagCount = 0;

        while (!q.isEmpty()) {
            int curr = q.poll();

            for (Edge e : adjList.get(curr)) {
                if (visited[e.dest]) {
                    continue;
                }
                q.offer(e.dest);
                visited[e.dest] = true;
                bagCount++;
            }
        }
        
        System.out.println(String.format("Task 1: %d bags", bagCount));
    }

    private static int countBags(ArrayList<ArrayList<Edge>> adjList, int src) {
        int count = 1;

        for (Edge e : adjList.get(src)) {
            count += e.weight * countBags(adjList, e.dest);
        }

        return count;
    }

    private static void task2(ArrayList<ArrayList<Edge>> adjList) {
        /**
         * We need the graph to have edges (u,v) where bag u contains bag v
         * So reverse the adjacency list
         */
        ArrayList<ArrayList<Edge>> transpose = new ArrayList<>();
        for (int i = 0; i < adjList.size(); i++) {
            transpose.add(new ArrayList<>());
        }
        for (int src = 0; src < adjList.size(); src++) {
            for (int j = 0; j < adjList.get(src).size(); j++) {
                Edge orig = adjList.get(src).get(j);
                transpose.get(orig.dest).add(new Edge(src, orig.weight));
            }
        }
        
        final String TARGET_COLOR = "shiny gold";
        
        int src = colorToIndex.get(TARGET_COLOR);
        
        // -1 because we don't count the target bag itself
        int bagCount = countBags(transpose, src) - 1;

        System.out.println(String.format("Task 2: %d bags", bagCount));
    }

    public static void main(String[] args) {
        ArrayList<ArrayList<Edge>> adjList = parseRules();
        task1(adjList);
        task2(adjList);
    }
}
