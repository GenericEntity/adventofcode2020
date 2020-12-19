import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * A SequenceRule represents a collection of rules where only if every rule is matched
 * then the SequenceRule as a whole is matched.
 * 
 * A SequenceRule lazily references its sub-rules because the sub-rules may not be
 * fully instantiated upon creation of the SequenceRule.
 */
public class SequenceRule implements Rule {

    private final Supplier<List<Rule>> sequenceGetter;
    private List<Rule> sequence;

    public SequenceRule(Supplier<List<Rule>> rules) {
        this.sequenceGetter = rules;
        this.sequence = null;
    }

    @Override
    public ParseResult parse(String s, int start) {
        // Lazy evaluation + caching
        if (sequence == null) {
            sequence = sequenceGetter.get();
        }

        List<Integer> starts = Arrays.asList(start);
        for (Rule rule : sequence) {
            ArrayList<Integer> possibleContinuations = new ArrayList<>();
            // Try every starting position to match current rule
            for (int strt : starts) {
                ParseResult res = rule.parse(s, strt);
                possibleContinuations.addAll(res.parsedUntil);
            }
            // If every starting position failed for any sub-rule then the entire
            // sequence rule fails
            if (possibleContinuations.isEmpty()) {
                return ParseResult.NO_MATCH;
            }
            // possible continuations from this sub-rule are the starting points
            // for the next sub-rule
            starts = possibleContinuations;
        }
        return new ParseResult(starts);
    }
    
}
