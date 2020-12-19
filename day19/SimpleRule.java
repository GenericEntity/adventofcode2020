import java.util.Arrays;

/**
 * A SequenceRule represents a rule where an exact match from a given starting point
 * in a string must be found
 * 
 * The string being parsed can be longer than what it is supposed to match
 */
public class SimpleRule implements Rule {

    private final String exact;

    public SimpleRule(String exact) {
        this.exact = exact;
    }

    @Override
    public ParseResult parse(String s, int start) {
        // If we have to match more characters than there are left, it's not a match
        if (s.length() - start < exact.length()) {
            return ParseResult.NO_MATCH;
        }

        // Otherwise check for an exact match
        int i_s = start;
        for (int i = 0; i < exact.length(); i++) {
            if (exact.charAt(i) != s.charAt(i_s)) {
                return ParseResult.NO_MATCH;
            }
            i_s++;
        }

        return new ParseResult(Arrays.asList(i_s));
    }
}
