/**
 * A parsing rule
 */
public interface Rule {
    ParseResult parse(String s, int start);
}
