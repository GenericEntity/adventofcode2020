import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Config {
    public final int right;
    public final int down;

    public Config(int right, int down) {
        this.right = right;
        this.down = down;
    }
}

public class Day3 {
    private static List<String> getInput() {
        ArrayList<String> input = new ArrayList<>();
        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            input.add(sc.nextLine());
        }

        sc.close();
        return input;
    }

    private static void task1(List<String> data) {
        final int RIGHT_SHIFT = 3;
        final char TREE = '#';
        int treeCount = 0;
        if (data.size() > 0) {
            int rowSize = data.get(0).length();
            for (int row = 0; row < data.size(); row++) {
                int col = (RIGHT_SHIFT * row) % rowSize;
                if (data.get(row).charAt(col) == TREE) {
                    treeCount++;
                }
            }
        }

        System.out.println(String.format("Trees encountered: %d", treeCount));
    }

    private static void task2(List<Config> configs, List<String> data) {
        final char TREE = '#';
        // Big integer because it could exceed long's capacity for a large enough input
        BigInteger product = BigInteger.ONE;

        if (data.size() > 0) {
            final int rowSize = data.get(0).length();

            // Get the count for every config and multiply it into the product
            for (Config cf : configs) {
                final int right = cf.right;
                final int down = cf.down;

                int treeCount = 0;
                for (int row = 0; row < data.size(); row += down) {
                    int col = (right * row/down) % rowSize;
                    if (data.get(row).charAt(col) == TREE) {
                        treeCount++;
                    }
                }
                product = product.multiply(BigInteger.valueOf(treeCount));
            }
        }

        System.out.println(String.format("Product of trees encountered: %s", product.toString()));
    }

    public static void main(String[] args) {
        List<String> input = getInput();
        task1(input);

        List<Config> configs = new ArrayList<Config>();
        configs.add(new Config(1, 1));
        configs.add(new Config(3, 1));
        configs.add(new Config(5, 1));
        configs.add(new Config(7, 1));
        configs.add(new Config(1, 2));
        task2(configs, input);
    }
}
