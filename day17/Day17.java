import java.util.ArrayList;
import java.util.Scanner;

class Coord {
    public final int x, y, z;

    public Coord(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Coord) {
            Coord other = (Coord)obj;
            return other.x == this.x && other.y == this.y && other.z == this.z;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (int)(Math.pow(2, x) + Math.pow(3, y) + Math.pow(5, z));
    }
}

class Data {
    public final ArrayList<ArrayList<Boolean>> plane;

    public Data(ArrayList<ArrayList<Boolean>> plane) {
        this.plane = plane;
    }
}

public class Day17 {
    private static Data readData() {
        final char ACTIVE_CHAR = '#';
        // final char INACTIVE_CHAR = '.';

        Scanner sc = new Scanner(System.in);
        ArrayList<ArrayList<Boolean>> plane = new ArrayList<>();

        while (sc.hasNextLine()) {
            ArrayList<Boolean> row = new ArrayList<>();
            String line = sc.nextLine();
            for (char c : line.toCharArray()) {
                row.add(c == ACTIVE_CHAR);
            }
            plane.add(row);
        }

        sc.close();
        return new Data(plane);
    }

    private static int countActiveNeighbors(boolean[][][][] grid, int w, int z, int y, int x) {
        int activeNeighCount = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    for (int dw = -1; dw <= 1; dw++) {
                        int newX = x + dx;
                        int newY = y + dy;
                        int newZ = z + dz;
                        int newW = w + dw;
                        
                        // If it's me, don't count
                        if (newX == x && newY == y && newZ == z && newW == w) {
                            continue;
                        }
    
                        // If out of bounds, ignore
                        if (newW < 0 || newW >= grid.length ||
                            newZ < 0 || newZ >= grid[0].length ||
                            newY < 0 || newY >= grid[0][0].length ||
                            newX < 0 || newX >= grid[0][0][0].length) {
                            continue;
                        }
    
                        if (grid[newW][newZ][newY][newX]) {
                            activeNeighCount++;
                        }
                    }
                }
            }
        }
        return activeNeighCount;
    }

    private static int solve(ArrayList<ArrayList<Boolean>> startingPlane, boolean is4D, int cycleCount) {
        /**
         * w, z, y, x dimensions of starting plane
         */
        final int STARTING_W_DIM = 1;
        final int STARTING_Z_DIM = 1;
        final int STARTING_Y_DIM = startingPlane.size();
        final int STARTING_X_DIM = startingPlane.get(0).size();

        /**
         * w, z, y, x dimensions of final hypercube 
         * Each dimension expands by 2 every cycle
         */
        final int W_DIM = is4D ? STARTING_W_DIM + 2*cycleCount : 1;
        final int Z_DIM = STARTING_Z_DIM + 2*cycleCount;
        final int Y_DIM = STARTING_Y_DIM + 2*cycleCount;
        final int X_DIM = STARTING_X_DIM + 2*cycleCount;

        // Holds previous cycle's state, to compute current cycle's state
        boolean[][][][] prev = new boolean[W_DIM]
                                          [Z_DIM]
                                          [Y_DIM]
                                          [X_DIM];

        // Holds current cycle's state
        boolean[][][][] curr = new boolean[W_DIM]
                                          [Z_DIM]
                                          [Y_DIM]
                                          [X_DIM];

        // Initialize prev with starting plane's values (everything else is inactive)
        final int startingW = is4D ? cycleCount : 0;
        final int startingZ = cycleCount;
        for (int w = 0; w < W_DIM; w++) {
            for (int z = 0; z < Z_DIM; z++) {
                for (int y = 0; y < Y_DIM; y++) {
                    for (int x = 0; x < X_DIM; x++) {
                        if (w == startingW &&
                        z == startingZ && 
                        y >= cycleCount && y < cycleCount + STARTING_Y_DIM &&
                        x >= cycleCount && x < cycleCount + STARTING_X_DIM) {
                            // If within starting plane, use its values
                            prev[w][z][y][x] = startingPlane.get(y - cycleCount).get(x - cycleCount);
                        } else {
                            // Otherwise, inactive
                            prev[w][z][y][x] = false;
                        }
                    }
                }
            }
        }

        // Simulate for given cycle count
        for (int cycle = 1; cycle <= cycleCount; cycle++) {
            for (int w = 0; w < W_DIM; w++) {
                for (int z = 0; z < Z_DIM; z++) {
                    for (int y = 0; y < Y_DIM; y++) {
                        for (int x = 0; x < X_DIM; x++) {
                            int activeNeighCount = countActiveNeighbors(prev, w, z, y, x);
                            boolean isActive = prev[w][z][y][x];
                            boolean willBeActive = false;
                            if (isActive) {
                                if (activeNeighCount == 2 || activeNeighCount == 3) {
                                    willBeActive = true;
                                }
                            } else {
                                if (activeNeighCount == 3) {
                                    willBeActive = true;
                                }
                            }
    
                            curr[w][z][y][x] = willBeActive;
                        }
                    }
                }
            }

            // Swap arrays (curr's values will be overwritten)
            boolean[][][][] temp = prev;
            prev = curr;
            curr = temp;
        }

        // Count number of active cells after simulation
        int activeCount = 0;
        for (int w = 0; w < W_DIM; w++) {
            for (int z = 0; z < Z_DIM; z++) {
                for (int y = 0; y < Y_DIM; y++) {
                    for (int x = 0; x < X_DIM; x++) {
                        if (prev[w][z][y][x]) {
                            activeCount++;
                        }
                    }
                }
            }
        }
        return activeCount;
    }

    private static void task1(Data data) {
        final int CYCLE_COUNT = 6;
        final boolean is4D = false;
        int activeCount = solve(data.plane, is4D, CYCLE_COUNT);
        System.out.println(String.format("Task 1: %d", activeCount));
    }

    private static void task2(Data data) {
        final int CYCLE_COUNT = 6;
        final boolean is4D = true;
        int activeCount = solve(data.plane, is4D, CYCLE_COUNT);
        System.out.println(String.format("Task 2: %d", activeCount));
    }

    public static void main(String[] args) {
        Data data = readData();
        task1(data);
        task2(data);
    }
}
