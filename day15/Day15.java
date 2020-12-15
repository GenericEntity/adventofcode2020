import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class TurnHistory {
    public final int lastTurn;
    public final int lastLastTurn;

    public TurnHistory(int lastTurn, int lastLastTurn) {
        this.lastTurn = lastTurn;
        this.lastLastTurn = lastLastTurn;
    }
}

class Data {
    public final ArrayList<Integer> numbers;

    public Data(ArrayList<Integer> numbers) {
        this.numbers = numbers;
    }
}

public class Day15 {
    private static Data readData() {
        Scanner sc = new Scanner(System.in);
        
        Data data = new Data(new ArrayList<>());
        String[] numStrs = sc.nextLine().split(",");
        for (String numStr : numStrs) {
            data.numbers.add(Integer.parseInt(numStr));
        }

        sc.close();

        return data;
    }

    private static int solve(Data data, int targetTurn) {
        // Indicates that a turn (in turn history) does not exist
        final int INVALID_TURN = -1;

        // Starting numbers
        ArrayList<Integer> starting = data.numbers;

        // Most Recent Number spoken
        int mrn = Integer.MIN_VALUE;

        // Maps numbers to their turn history
        HashMap<Integer, TurnHistory> history = new HashMap<>();

        for (int turn = 0; turn < targetTurn; turn++) {
            // Compute next number spoken
            int numSpoken;
            if (turn < starting.size()) {
                numSpoken = starting.get(turn);
            } else {
                // most recent number is guaranteed to always be in history
                // because it's the number that was spoken last turn so we must 
                // have added it into history (or updated it)
                TurnHistory mrnTurnHist = history.get(mrn);
                if (mrnTurnHist.lastLastTurn == INVALID_TURN) {
                    numSpoken = 0;
                } else {
                    numSpoken = mrnTurnHist.lastTurn - mrnTurnHist.lastLastTurn;
                }
            }

            // Update history
            if (history.containsKey(numSpoken)) {
                TurnHistory numSpokenTurnHist = history.get(numSpoken);
                history.put(numSpoken, new TurnHistory(turn, numSpokenTurnHist.lastTurn));
            } else {
                history.put(numSpoken, new TurnHistory(turn, INVALID_TURN));
            }
            mrn = numSpoken;
        }

        return mrn;
    }

    private static void task1(Data data) {
        int ans = solve(data, 2020);
        System.out.println(String.format("Task 1: %d", ans));
    }

    private static void task2(Data data) {
        int ans = solve(data, 30000000);
        System.out.println(String.format("Task 2: %d", ans));
    }

    public static void main(String[] args) {
        Data data = readData();
        task1(data);
        task2(data);
    }
}
