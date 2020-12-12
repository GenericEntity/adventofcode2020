import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ShipState {
    public Vector2 position;
    public int heading;

    public ShipState(int east, int north, int heading) {
        this.position = new Vector2(east, north);
        this.heading = heading;
    }
}

enum Direction {
    N, S, E, W,
    L, R,
    F,
}

class Instruction {
    public final Direction dir;
    public final int val;

    public Instruction(Direction dir, int val) {
        this.dir = dir;
        this.val = val;
    }
}

public class Day12 {
    private static List<Instruction> readData() {
        Scanner sc = new Scanner(System.in);
        ArrayList<Instruction> data = new ArrayList<>();

        Pattern instPattern = Pattern.compile("([A-Z])(\\d+)");

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            Matcher m = instPattern.matcher(line);
            if (m.find()) {
                char dirChar = m.group(1).charAt(0);
                int val = Integer.parseInt(m.group(2));
                Direction dir;
                switch (dirChar) {
                    case 'N': dir = Direction.N;
                        break;
                    case 'S': dir = Direction.S;
                        break;
                    case 'E': dir = Direction.E;
                        break;
                    case 'W': dir = Direction.W;
                        break;
                    case 'L': dir = Direction.L;
                        break;
                    case 'R': dir = Direction.R;
                        break;
                    case 'F': dir = Direction.F;
                        break;
                    default: throw new UnsupportedOperationException();
                }
                data.add(new Instruction(dir, val));
            }
        }

        sc.close();

        return data;
    }

    private static void task1(List<Instruction> instructions) {
        // Ship's starting position is the origin; facing east
        ShipState ship = new ShipState(0, 0, 90);

        for (Instruction i : instructions) {
            switch (i.dir) {
                case N: ship.position = ship.position.move(0, i.val);
                    break;
                case S: ship.position = ship.position.move(0, -i.val);
                    break;
                case E: ship.position = ship.position.move(i.val, 0);
                    break;
                case W: ship.position = ship.position.move(-i.val, 0);
                    break;
                case L: 
                    ship.heading = (ship.heading + 360 - (i.val % 360)) % 360;
                    break;
                case R:
                    ship.heading = (ship.heading + (i.val % 360)) % 360;
                    break;
                case F:
                    if (ship.heading == 0) {
                        ship.position = ship.position.move(0, i.val);
                    } else if (ship.heading == 90) {
                        ship.position = ship.position.move(i.val, 0);
                    } else if (ship.heading == 180) {
                        ship.position = ship.position.move(0, -i.val);
                    } else if (ship.heading == 270) {
                        ship.position = ship.position.move(-i.val, 0);
                    } else {
                        System.err.println(String.format("Unexpected heading value: %d", ship.heading));
                    }
                    break;
                default: throw new UnsupportedOperationException();

            }
        }

        System.out.println(String.format("Task 1: %d", ship.position.manhattanDist()));
    }

    private static void task2(List<Instruction> instructions) {
        // Matrix representation of a 90-degree clockwise rotation about 
        //  the origin in 2D
        final Mat2x2 cw90 = new Mat2x2(0, 1, -1, 0);
        // Matrix representation of a 90-degree anticlockwise rotation about 
        //  the origin in 2D
        final Mat2x2 acw90 = new Mat2x2(0, -1, 1, 0);

        // Ship's starting position is the origin; facing east
        ShipState ship = new ShipState(0, 0, 90);
        // Waypoint's position is always relative to the ship
        Vector2 waypoint = new Vector2(10, 1);

        for (Instruction i : instructions) {
            switch (i.dir) {
                case N: waypoint = waypoint.move(0, i.val);
                    break;
                case S: waypoint = waypoint.move(0, -i.val);
                    break;
                case E: waypoint = waypoint.move(i.val, 0);
                    break;
                case W: waypoint = waypoint.move(-i.val, 0);
                    break;
                /**
                 * rotating the waypoint about its origin (the ship) left by 
                 * 90k degrees (k is some positive integer) is the same as
                 * getting the kth power of the matrix that rotates ACW by 90
                 * degrees and then postmultiplying it by the waypoint's 
                 * position vector. A right rotation is similar.
                 */ 
                case L: waypoint = acw90.power((i.val % 360)/90).postMultiplyBy(waypoint);
                    break;
                case R: waypoint = cw90.power((i.val % 360)/90).postMultiplyBy(waypoint);
                    break;
                case F:
                    ship.position = ship.position.move(i.val * waypoint.x, i.val * waypoint.y);
                    break;
                default: throw new UnsupportedOperationException();
            }
        }

        System.out.println(String.format("Task 2: %d", ship.position.manhattanDist()));
    }
    

    public static void main(String[] args) {
        List<Instruction> instructions = readData();
        task1(instructions);
        task2(instructions);
    }
}
