import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * A ChoiceRule represents a collection of rules where if any rule is matched
 * then the ChoiceRule as a whole is matched.
 * 
 * A ChoiceRule lazily references its sub-rules because the sub-rules may not be
 * fully instantiated upon creation of the ChoiceRule.
 */
public class ChoiceRule implements Rule {

    private final Supplier<List<Rule>> choicesGetter;
    private List<Rule> choices;

    public ChoiceRule(Supplier<List<Rule>> choices) {
        this.choicesGetter = choices;
        this.choices = null;
    }

    @Override
    public ParseResult parse(String s, int start) {
        // Lazy evaluation + caching
        if (choices == null) {
            choices = choicesGetter.get();
        }
        // Try parsing every choice. Add every possible continuation so rules that
        // use this rule can explore those branches
        List<Integer> possibleContinuations = new ArrayList<>();
        for (Rule rule : choices) {
            ParseResult res = rule.parse(s, start);
            possibleContinuations.addAll(res.parsedUntil);
        }
        return new ParseResult(possibleContinuations);
    }
}
