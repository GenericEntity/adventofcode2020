import java.util.ArrayList;
import java.util.Scanner;

class Coord {
    public final int r, c;
    public Coord(int r, int c) {
        this.r = r;
        this.c = c;
    }
}

enum SeatState {
    Floor,
    Empty,
    Occupied,
}

public class Day11 {
    private static SeatState[][] readData() {
        Scanner sc = new Scanner(System.in);
        ArrayList<ArrayList<SeatState>> data = new ArrayList<>();

        while (sc.hasNextLine()) {
            ArrayList<SeatState> row = new ArrayList<>();
            data.add(row);
            String line = sc.nextLine();
            for (char cell : line.toCharArray()) {
                switch (cell) {
                    case '.':
                        row.add(SeatState.Floor);
                        break;
                    case 'L':
                        row.add(SeatState.Empty);
                        break;
                    case '#':
                        row.add(SeatState.Occupied);
                        break;
                    default:
                        System.err.println(String.format("Invalid character: %c", cell));
                        break;
                }
            }
        }

        sc.close();

        SeatState[][] grid = new SeatState[data.size()][data.get(0).size()];
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.get(i).size(); j++) {
                grid[i][j] = data.get(i).get(j);
            }
        }

        return grid;
    }
    
    private static SeatState[][] copyGrid(SeatState[][] grid) {
        SeatState[][] copy = new SeatState[grid.length][grid[0].length];
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                copy[r][c] = grid[r][c];
            }
        }
        return copy;
    }

    private static void copyGridValues(SeatState[][] src, SeatState[][] dest) {
        for (int r = 0; r < src.length; r++) {
            for (int c = 0; c < src[r].length; c++) {
                dest[r][c] = src[r][c];
            }
        }
    }

    private static boolean gridsEqual(SeatState[][] g1, SeatState[][] g2) {
        if (g1.length != g2.length) {
            return false;
        }
        for (int r = 0; r < g1.length; r++) {
            if (g1[r].length != g2[r].length) {
                return false;
            }
            for (int c = 0; c < g1[r].length; c++) {
                if (g1[r][c] != g2[r][c]) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isInBounds(Coord coord, SeatState[][] grid) {
        return coord.r >= 0 && 
               coord.r < grid.length &&
               coord.c >= 0 &&
               coord.c < grid[coord.r].length;
    }

    private static SeatState task1GetNextState(Coord coord, SeatState[][] grid) {
        int r = coord.r;
        int c = coord.c;

        Coord[] adj = new Coord[]{
            new Coord(r - 1, c    ), // up
            new Coord(r - 1, c + 1), // up-right
            new Coord(r    , c + 1), // right
            new Coord(r + 1, c + 1), // down-right
            new Coord(r + 1, c    ), // down
            new Coord(r + 1, c - 1), // down-left
            new Coord(r    , c - 1), // left
            new Coord(r - 1, c - 1), // up-left
        };
        SeatState curr = grid[r][c];

        int occCount = 0;
        for (Coord neigh : adj) {
            if (!isInBounds(neigh, grid)) {
                continue;
            }

            if (grid[neigh.r][neigh.c] == SeatState.Occupied) {
                occCount++;
            }
        }

        final int TOO_MANY_PPL = 4;
        if (curr == SeatState.Empty && occCount == 0) {
            return SeatState.Occupied;
        } else if (curr == SeatState.Occupied && occCount >= TOO_MANY_PPL) {
            return SeatState.Empty;
        }

        return curr;
    }

    private static int countOccupiedSeats(SeatState[][] grid) {
        int occupiedSeats = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                 if (grid[r][c] == SeatState.Occupied) {
                     occupiedSeats++;
                 }
            }
        }
        return occupiedSeats;
    }

    private static void task1(SeatState[][] grid) {
        // Create two new grids with the same values as grid (don't modify grid)
        SeatState[][] prev = copyGrid(grid);
        SeatState[][] curr = copyGrid(grid);

        // Simulate until stable
        do {
            copyGridValues(curr, prev);
            for (int r = 0; r < prev.length; r++) {
                for (int c = 0; c < prev[r].length; c++) {
                    curr[r][c] = task1GetNextState(new Coord(r, c), prev);
                }
            }
        } while (!gridsEqual(prev, curr));

        System.out.println(String.format("Task 1: %d", countOccupiedSeats(curr)));
    }

    private static SeatState task2GetNextState(Coord coord, SeatState[][] grid) {
        int r = coord.r;
        int c = coord.c;

        Coord[] dirs = new Coord[]{
            new Coord(-1,  0), // up
            new Coord(-1, +1), // up-right
            new Coord(0 , +1), // right
            new Coord(+1, +1), // down-right
            new Coord(+1,  0), // down
            new Coord(+1, -1), // down-left
            new Coord(0 , -1), // left
            new Coord(-1, -1), // up-left
        };
        SeatState curr = grid[r][c];

        int occCount = 0;
        // Scan outwards in every direction
        for (Coord dir : dirs) {
            int dist = 1;
            while (true) {
                int r1 = r + dist*dir.r;
                int c1 = c + dist*dir.c;
                // stop when we can look no further
                if (!isInBounds(new Coord(r1, c1), grid)) {
                    break;
                }
                // stop at first seat seen
                if (grid[r1][c1] == SeatState.Occupied) {
                    occCount++;
                    break;
                } else if (grid[r1][c1] == SeatState.Empty) {
                    break;
                }
                // look further
                dist++;
            }
        }

        final int TOO_MANY_PPL = 5;
        if (curr == SeatState.Empty && occCount == 0) {
            return SeatState.Occupied;
        } else if (curr == SeatState.Occupied && occCount >= TOO_MANY_PPL) {
            return SeatState.Empty;
        }

        return curr;
    }

    private static void task2(SeatState[][] grid) {
        // Create two new grids with the same values as grid (don't modify grid)
        SeatState[][] prev = copyGrid(grid);
        SeatState[][] curr = copyGrid(grid);

        // Simulate until stable
        do {
            copyGridValues(curr, prev);
            for (int r = 0; r < prev.length; r++) {
                for (int c = 0; c < prev[r].length; c++) {
                    curr[r][c] = task2GetNextState(new Coord(r, c), prev);
                }
            }
        } while (!gridsEqual(prev, curr));

        System.out.println(String.format("Task 2: %d", countOccupiedSeats(curr)));
    }
    

    public static void main(String[] args) {
        SeatState[][] grid = readData();
        task1(grid);
        task2(grid);
    }
}
