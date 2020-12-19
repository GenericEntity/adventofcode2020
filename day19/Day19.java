import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Data {
    public final HashMap<Integer, Rule> ruleMap;
    public final List<String> messages;

    public Data(HashMap<Integer, Rule> ruleMap, List<String> messages) {
        this.ruleMap = ruleMap;
        this.messages = messages;
    }
}

public class Day19 {
    private static Data readData() {
        Scanner sc = new Scanner(System.in);
        HashMap<Integer, Rule> ruleMap = new HashMap<>();

        Pattern simpleRulePattern = Pattern.compile("(\\d+): \"(\\w+)\"");
        Pattern sequenceRulePattern = Pattern.compile("(\\d+): ((?:\\d+\\s*)+)$");
        Pattern choiceRulePattern = Pattern.compile("(\\d+): ((?:\\d+\\s*)+) \\| ((?:\\d+\\s*)+)$");

        /**
         * Parse the rules into a map. Rules may be constructed before their dependencies
         * so compound rules (sequence and choice) lazily evaluate their dependencies:
         * for each compound rule, we construct a function that, once the whole rule 
         * map has been constructed, will get all the dependencies for that compound
         * rule.
         */
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.isEmpty()) {
                break;
            }

            Matcher m;
            if ((m = choiceRulePattern.matcher(line)).find()) {
                int ruleNum = Integer.parseInt(m.group(1));
                String[] choice1Seq = m.group(2).split(" ");
                String[] choice2Seq = m.group(3).split(" ");

                Rule subrule1 = new SequenceRule(() ->
                    Stream.of(choice1Seq)
                          .mapToInt(Integer::parseInt)
                          .mapToObj(i -> ruleMap.get(i))
                          .collect(Collectors.toList())
                );
                Rule subrule2 = new SequenceRule(() ->
                    Stream.of(choice2Seq)
                          .mapToInt(Integer::parseInt)
                          .mapToObj(i -> ruleMap.get(i))
                          .collect(Collectors.toList())
                );
                Rule rule = new ChoiceRule(() -> Arrays.asList(subrule1, subrule2));

                ruleMap.put(ruleNum, rule);
            } else if ((m = sequenceRulePattern.matcher(line)).find()) {
                int ruleNum = Integer.parseInt(m.group(1));
                String[] seq = m.group(2).split(" ");
                
                Rule rule = new SequenceRule(() -> 
                    Stream.of(seq)
                          .mapToInt(Integer::parseInt)
                          .mapToObj(i -> ruleMap.get(i))
                          .collect(Collectors.toList())
                );

                ruleMap.put(ruleNum, rule);
            } else if ((m = simpleRulePattern.matcher(line)).find()) {
                int ruleNum = Integer.parseInt(m.group(1));
                String exact = m.group(2);
                Rule r = new SimpleRule(exact);

                ruleMap.put(ruleNum, r);
            } else {
                System.err.println(String.format("Unparseable rule: '%s'", line));
            }
        }

        // Parse inputs
        ArrayList<String> messages = new ArrayList<>();
        while (sc.hasNextLine()) {
            messages.add(sc.nextLine());
        }

        sc.close();
        return new Data(ruleMap, messages);
    }

    private static void bothTasks(Data data) {
        final int TARGET_RULE_NUM = 0;
        final Rule TARGET_RULE = data.ruleMap.get(TARGET_RULE_NUM);
        int matchCount = 0;
        for (String message : data.messages) {
            ParseResult result = TARGET_RULE.parse(message, 0);

            // If any branch of rule matches entire string, it counts
            for (int i : result.parsedUntil) {
                if (i == message.length()) {
                    matchCount++;
                    break;
                }
            }
        }
        
        System.out.println(String.format("Match Count: %d", matchCount));
    }

    public static void main(String[] args) {
        Data data = readData();
        bothTasks(data);
    }
}
