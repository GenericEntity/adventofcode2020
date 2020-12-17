import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Data {
    public final HashMap<String, Pair<IntRange, IntRange>> fieldRanges;
    public final ArrayList<Integer> yourTicket;
    public final ArrayList<ArrayList<Integer>> nearbyTickets;

    public Data(HashMap<String, Pair<IntRange, IntRange>> fieldRanges,
                ArrayList<Integer> yourTicket,
                ArrayList<ArrayList<Integer>> nearbyTickets) {
        this.fieldRanges = fieldRanges;
        this.yourTicket = yourTicket;
        this.nearbyTickets = nearbyTickets;
    }
}

public class Day16 {
    private static Data readData() {
        Scanner sc = new Scanner(System.in);

        Pattern fieldPattern = Pattern.compile("(.*): (\\d+)-(\\d+) or (\\d+)-(\\d+)");
        HashMap<String, Pair<IntRange, IntRange>> fieldRanges = new HashMap<>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            Matcher fieldMatcher = fieldPattern.matcher(line);
            if (fieldMatcher.find()) {
                String field = fieldMatcher.group(1);
                int rng1Lower = Integer.parseInt(fieldMatcher.group(2));
                int rng1Upper = Integer.parseInt(fieldMatcher.group(3));
                int rng2Lower = Integer.parseInt(fieldMatcher.group(4));
                int rng2Upper = Integer.parseInt(fieldMatcher.group(5));

                fieldRanges.put(field, 
                                new Pair<>(new IntRange(rng1Lower, rng1Upper), 
                                           new IntRange(rng2Lower, rng2Upper)));
            }

            if (line.isEmpty()) {
                break;
            }
        }

        // ignore next line: your ticket label
        sc.nextLine();

        String[] yourTicketStrs = sc.nextLine().split(",");
        ArrayList<Integer> yourTicket = new ArrayList<>();
        for (int i = 0; i < yourTicketStrs.length; i++) {
            yourTicket.add(Integer.parseInt(yourTicketStrs[i]));
        }

        // ignore next blank line
        sc.nextLine();
        
        // ignore next line: nearby tickets label
        sc.nextLine();

        ArrayList<ArrayList<Integer>> nearbyTickets = new ArrayList<>();

        while (sc.hasNextLine()) {
            String[] ticketStrs = sc.nextLine().split(",");
            ArrayList<Integer> ticket = new ArrayList<>();
            for (int i = 0; i < ticketStrs.length; i++) {
                ticket.add(Integer.parseInt(ticketStrs[i]));
            }
            nearbyTickets.add(ticket);
        }

        sc.close();

        return new Data(fieldRanges, yourTicket, nearbyTickets);
    }

    private static ArrayList<ArrayList<Integer>> task1(Data data) {
        int errorRate = 0;
        ArrayList<ArrayList<Integer>> validTickets = new ArrayList<>();
        for (ArrayList<Integer> ticket : data.nearbyTickets) {
            boolean isValid = true;
            for (int value : ticket) {
                boolean inNoRange = true;
                for (Pair<IntRange, IntRange> rangePair : data.fieldRanges.values()) {
                    if (rangePair.fst.isInRange(value) || rangePair.snd.isInRange(value)) {
                        inNoRange = false;
                        break;
                    }
                }
                if (inNoRange) {
                    errorRate += value;
                    isValid = false;
                }
            }
            if (isValid) {
                validTickets.add(ticket);
            }
        }
        System.out.println(String.format("Task 1: %d", errorRate));
        return validTickets;
    }

    private static void task2(Data data, ArrayList<ArrayList<Integer>> validTickets) {
        /**
         * Create a mapping of each field index to its possible field names
         */
        ArrayList<ArrayList<String>> possibilities = new ArrayList<>();
        for (int i = 0; i < data.fieldRanges.size(); i++) {
            possibilities.add(new ArrayList<>());
        }
        for (String field : data.fieldRanges.keySet()) {
            Pair<IntRange, IntRange> ranges = data.fieldRanges.get(field);

            // try the field as each position in the ticket data
            for (int i = 0; i < data.fieldRanges.size(); i++) {
                boolean possible = true;
                for (ArrayList<Integer> ticket : validTickets) {
                    int value = ticket.get(i);
                    if (!ranges.fst.isInRange(value) && !ranges.snd.isInRange(value)) {
                        possible = false;
                        break;
                    }
                }
                // if it works, record the field as a candidate for that index
                if (possible) {
                    possibilities.get(i).add(field);
                }
            }
        }

        /**
         * NOTE: This part assumes that there is exactly one answer
         * Approach: find a field index with exactly one possibility. Take that
         * as the answer and remove it from consideration. Repeat this until we
         * have found all the field index answers.
         */
        String[] answer = new String[data.fieldRanges.size()];
        int foundCount = 0;
        while (foundCount < answer.length) {            
            for (int i = 0; i < data.fieldRanges.size(); i++) {
                ArrayList<String> p = possibilities.get(i);
                if (p.size() == 1) {
                    String field = p.get(0);
                    
                    answer[i] = field;
                    foundCount++;
    
                    for (int j = 0; j < possibilities.size(); j++) {
                        possibilities.get(j).remove(field);
                    }

                    break;
                }
            }
        }

        // Get the indices of all fields that begin with the target prefix
        final String TARGET_PREFIX = "departure";
        ArrayList<Integer> relevantFields = new ArrayList<>();
        for (int i = 0; i < data.fieldRanges.size(); i++) {
            if (answer[i].startsWith(TARGET_PREFIX)) {
                relevantFields.add(i);
            }
        }

        // Get the product of all relevant fields in yourTicket
        BigInteger product = BigInteger.ONE;
        for (int i : relevantFields) {
            product = product.multiply(BigInteger.valueOf(data.yourTicket.get(i)));
        }

        System.out.println(String.format("Task 2: %s", product.toString()));
    }

    public static void main(String[] args) {
        Data data = readData();
        ArrayList<ArrayList<Integer>> validTickets = task1(data);
        task2(data, validTickets);
    }
}
