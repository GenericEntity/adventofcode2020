public class IntRange {
    public final int lower;
    public final int upper;

    public IntRange(int lower, int upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public boolean isInRange(int x) {
        return x <= upper && x >= lower;
    }
}
