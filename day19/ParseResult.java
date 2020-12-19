import java.util.ArrayList;
import java.util.List;

/**
 * The result of parsing is:
 *  an empty list: if no matches were found
 *  a non-empty list containing 1 or more indices to continue parsing from: one index per match found
 */
public class ParseResult {
    public static final ParseResult NO_MATCH = new ParseResult(new ArrayList<>());

    public final List<Integer> parsedUntil;

    public ParseResult(List<Integer> parsedUntil) {
        this.parsedUntil = parsedUntil;
    }

    public boolean hasNoMatch() {
        return this.parsedUntil.size() == 0;
    }
}
