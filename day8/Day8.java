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

class Cmd {
    public final String instruction;
    public final int arg;

    public Cmd(String instr, int arg) {
        this.instruction = instr;
        this.arg = arg;
    }
}

public class Day8 {
    static final String NOP_STR = "nop";
    static final String ACC_STR = "acc";
    static final String JMP_STR = "jmp";
    static HashMap<Integer, Cmd> indexToCmd = new HashMap<>();

    private static ArrayList<ArrayList<Edge>> buildGraph() {
        /**
         * Reads input and builds a directed, weighted graph.
         * 
         * Vertices: instructions (each assigned an index)
         * Edges: an edge (src, dest, weight) exists if instruction src jumps to
         *  instruction dest, and instruction increments accumulator by weight
         */

        ArrayList<ArrayList<Edge>> adjList = new ArrayList<>();
        int index = 0;
        Scanner sc = new Scanner(System.in);

        Pattern cmdRegex = Pattern.compile("(\\w+) ([+|-]\\d+)");

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            Matcher m = cmdRegex.matcher(line);
            if (m.find()) {
                String instr = m.group(1);
                int arg = Integer.parseInt(m.group(2));
                Cmd cmd = new Cmd(instr, arg);
                adjList.add(new ArrayList<>());

                /**
                 * Note: if last instruction is not a jmp instruction, then
                 * it might point to an invalid next instruction but if the input
                 * is valid, then it should never be used
                 */
                if (instr.equals(NOP_STR)) {
                    // ignore arg
                    adjList.get(index).add(new Edge(index + 1, 0));
                } else if (instr.equals(ACC_STR)) {
                    adjList.get(index).add(new Edge(index + 1, arg));
                } else if (instr.equals(JMP_STR)) {
                    adjList.get(index).add(new Edge(index + arg, 0));
                } else {
                    System.err.println(String.format("Unrecognized command: '%s'", line));
                    break;
                }
                indexToCmd.put(index, cmd);
                index++;
            } else {
                System.err.println(String.format("Bad input: '%s'", line));
                break;
            }
        }

        sc.close();
        return adjList;
    }

    

    private static void task1(ArrayList<ArrayList<Edge>> adjList) {
        int acc = 0;

        int n = adjList.size();
        boolean[] visited = new boolean[n];

        Arrays.fill(visited, false);
        int curr = 0;    // start from first instruction always
        
        while (!visited[curr]) {
            visited[curr] = true;
            // there should be exactly 1 edge out of every vertex
            Edge e = adjList.get(curr).get(0);
            acc += e.weight;
            curr = e.dest;
        }
        
        System.out.println(String.format("Task 1 acc: %d", acc));
    }

    private static ArrayList<ArrayList<Edge>> buildLayeredGraph(ArrayList<ArrayList<Edge>> adjList) {
        int n = adjList.size();

        ArrayList<ArrayList<Edge>> layered = new ArrayList<>();
        for (int i = 0; i < 2 * n + 1; i++) {
            layered.add(new ArrayList<>());
        }

        for (int i = 0; i < n; i++) {
            Cmd cmd = indexToCmd.get(i);
            String instr = cmd.instruction;
            int arg = cmd.arg;
            Edge e = adjList.get(i).get(0);
            
            // take existing edge
            layered.get(i).add(e);

            // duplicate edge in 2nd layer
            layered.get(i + n + 1).add(new Edge(e.dest + n + 1, e.weight));

            if (instr.equals(NOP_STR)) {
                // create jump edge from 2nd layer to first
                layered.get(i + n + 1).add(new Edge(i + arg, 0));
            } else if (instr.equals(ACC_STR)) {
                // do nothing
            } else if (instr.equals(JMP_STR)) {
                // create nop edge from 2nd layer to first
                layered.get(i + n + 1).add(new Edge(i + 1, 0));
            } else {
                System.err.println(String.format("Unrecognized command: '%s'", instr));
                break;
            }
        }

        return layered;
    }

    private static void task2(ArrayList<ArrayList<Edge>> adjList) {
        int n = adjList.size();
        /**
         * We transform the graph into a 2-layer graph then do a simple BFS, tallying
         * the accumulator values along the way.
         * 
         * The first layer (indices 0 to n-1) has the same edges as the original graph
         * Index n is the destination (exists solely to detect that the program has terminated)
         * 
         * The second layer (indices n+1 to 2n) duplicates the first layer but with
         * additional directed edges to layer 1 that represent changing a jmp to nop 
         * or vice versa.
         * 
         * The idea is that we search from the first instruction in layer 2 to the destination
         * and crossing over from layer 2 to layer 1 means we have used the corresponding changed 
         * instruction.
         * This works because we do not need to consider cycles so we will not encounter
         * the 'old' layer 1 instruction again even though the edge exists in layer 1
         */
        ArrayList<ArrayList<Edge>> layered = buildLayeredGraph(adjList);

        int src = n + 1;                // start at first instruction in 2nd layer
        int dest = n;                   // last instruction is n (2nd layer starts at n+1)
        int[] accs = new int[2*n + 1];  // accs stores the accumulator value at a given instruction
        boolean[] visited = new boolean[2*n + 1];   // we cannot use a special value of accs to denote
                                                    //  not visited because negative acc values are possible
        Queue<Integer> q = new LinkedList<>();      // Queue for BFS

        // BFS init
        Arrays.fill(visited, false);
        q.offer(src);
        accs[src] = 0;

        // BFS main loop
        while (!q.isEmpty()) {
            int curr = q.poll();
            
            for (Edge e : layered.get(curr)) {
                // Skip if visited previously, we do not consider cycles (infinite loops)
                if (visited[e.dest]) {
                    continue;
                }

                accs[e.dest] = accs[curr] + e.weight;
                visited[e.dest] = true;
                q.offer(e.dest);
            }
        }

        // After BFS, accumulator value when last instruction is reached is in accs[dest]
        System.out.println(String.format("Task 2 acc: %d", accs[dest]));
    }

    public static void main(String[] args) {
        ArrayList<ArrayList<Edge>> adjList = buildGraph();
        task1(adjList);
        task2(adjList);
    }
}
