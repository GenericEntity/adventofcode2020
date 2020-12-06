import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class Day6 {
    private static List<String> getInput() {
        ArrayList<String> groups = new ArrayList<>();
        Scanner sc = new Scanner(System.in);

        StringBuilder answer = new StringBuilder();
        answer.setLength(0);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (!line.isEmpty()) {
                if (answer.length() > 0) {
                    answer.append(" ");
                }
                answer.append(line);
            } else {
                groups.add(answer.toString());
                answer.setLength(0);
            }
        }
        
        // In case the file does not end with a blank line
        if (answer.length() > 0) {
            groups.add(answer.toString());
        }

        sc.close();
        return groups;
    }

    private static void task1(List<String> groups) {
        // We only care about counting unique chars in the alphabet
        final HashSet<Character> alphabet = new HashSet<>();
        for (char letter = 'a'; letter <= 'z'; letter++) {
            alphabet.add(letter);
        }

        int totalCount = 0;
        for (String group : groups) {
            HashSet<Character> uniqueLetters = new HashSet<>();
            for (char c : group.toCharArray()) {
                if (!alphabet.contains(c)) {
                    continue;
                }

                if (!uniqueLetters.contains(c)) {
                    uniqueLetters.add(c);
                }
            }
            totalCount += uniqueLetters.size();
        }

        // Stream answer:
        // long totalCount = groups.stream()
        //     .map(
        //         str -> str.chars().filter(c -> alphabet.contains((char)c)).distinct().count())
        //     .mapToLong(Long::longValue)
        //     .sum();
        
        System.out.println(String.format("Task 1 sum of group counts: %d", totalCount));
    }

    

    private static void task2(List<String> groups) {
        int totalCount = 0;
        for (String group : groups) {
            // Maps questions to the number of people who answered them in a group
            HashMap<Character, Integer> histogram = new HashMap<>();
            for (char letter = 'a'; letter <= 'z'; letter++) {
                histogram.put(letter, 0);
            }

            // Assumes members' answers are space-separated
            int groupSize = group.split(" ").length;

            // Build histogram of questions answered in group
            for (char c : group.toCharArray()) {
                if (!histogram.containsKey(c)) {
                    continue;
                }

                histogram.put(c, histogram.get(c) + 1);
            }

            // Only count questions answered by everyone in group
            totalCount += histogram.entrySet()
                .stream()
                .filter(e -> e.getValue().equals(groupSize))
                .count();
        }
        
        System.out.println(String.format("Task 2 sum of group counts: %d", totalCount));
    }

    public static void main(String[] args) {
        List<String> groups = getInput();
        task1(groups);
        task2(groups);
    }
}
