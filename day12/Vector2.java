/**
 * Immutable 2-component vector
 */
public class Vector2 {
    public final int x, y;

    /**
     * Returns a new immutable 2-component vector with the given entries
     * @param x the x-component
     * @param y the y-component
     */
    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns a new Vector2 with x and y offset from this Vector2
     * @param dx the x offset
     * @param dy the y offset
     * @return the resulting vector
     */
    public Vector2 move(int dx, int dy) {
        return new Vector2(x + dx, y + dy);
    }

    /**
     * Returns the Manhattan Distance of the tip of this vector from the origin
     * @return the Manhattan Distance of the tip of this vector from the origin
     */
    public int manhattanDist() {
        return Math.abs(x) + Math.abs(y);
    }
}
