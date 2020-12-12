/**
 * Immutable 2x2 matrix
 */
public class Mat2x2 {
    private final int a, b, c, d;

    public static final Mat2x2 ID = new Mat2x2(1, 0, 0, 1);

    /**
     * Returns a new immutable 2x2 matrix with the given entries
     * @param a the top-left entry
     * @param b the top-right entry
     * @param c the bottom-left entry
     * @param d the bottom-right entry
     */
    public Mat2x2(int a, int b, int c, int d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    /**
     * Post-multiplies this matrix by v and returns the result as a new vector
     * @param v the vector to post-multiply
     * @return the resulting vector
     */
    public Vector2 postMultiplyBy(Vector2 v) {
        return new Vector2(
            a * v.x + b * v.y, 
            c * v.x + d * v.y);
    }

    /**
     * Pre-multiplies this matrix by m and returns the result as a new matrix
     * @param m the matrix to pre-multiply
     * @return the resulting matrix
     */
    public Mat2x2 preMultiplyBy(Mat2x2 m) {
        return new Mat2x2(
            m.a * this.a + m.b * this.c,
            m.a * this.b + m.b * this.d,
            m.c * this.a + m.d * this.c,
            m.c * this.b + m.d * this.d
        );
    }

    /**
     * Post-multiplies this matrix by m and returns the result as a new matrix
     * @param m the matrix to post-multiply
     * @return the resulting matrix
     */
    public Mat2x2 postMultiplyBy(Mat2x2 m) {
        return new Mat2x2(
            this.a * m.a + this.b * m.c,
            this.a * m.b + this.b * m.d,
            this.c * m.a + this.d * m.c,
            this.c * m.b + this.d * m.d
        );
    }

    /**
     * Computes the kth integer power of this 2x2 matrix in O(k) time and returns
     * the result as a new matrix
     * @param k the nonnegative integer power wanted
     * @return the kth power of this matrix
     */
    public Mat2x2 power(int k) {
        Mat2x2 result = ID;
        for (int i = 0; i < k; i++) {
            result = result.preMultiplyBy(this);
        }
        return result;
    }
}
