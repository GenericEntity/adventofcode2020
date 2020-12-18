import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import java.util.function.Function;

class Data {
    public final ArrayList<String> expressions;

    public Data(ArrayList<String> expressions) {
        this.expressions = expressions;
    }
}

public class Day18 {
    private static Data readData() {
        Scanner sc = new Scanner(System.in);
        ArrayList<String> expressions = new ArrayList<>();

        while (sc.hasNextLine()) {
            expressions.add(sc.nextLine());
        }

        sc.close();
        return new Data(expressions);
    }

    private static boolean isDigit(char c) {
        return c == '0' || c == '1' || c == '2' || c == '3' || c == '4' ||
               c == '5' || c == '6' || c == '7' || c == '8' || c == '9';
    }

    private static boolean isOperator(char c) {
        return c == '*' || c == '+';
    }

    private static boolean isOpenParenthesis(char c) {
        return c == '(';
    }

    private static boolean isCloseParenthesis(char c) {
        return c == ')';
    }

    private static String infixToPostfix(String expr, Function<Character, Integer> getOpPrecedence) {
        /**
         * Assumes all tokens are a single character
         * 
         * Convert infix to postfix (RPN) using simplified Shunting-yard algorithm
         */
        Stack<Character> ops = new Stack<>();
        StringBuilder postfixBuilder = new StringBuilder();

        for (int i = 0; i < expr.length(); i++) {
            char token = expr.charAt(i);
            if (isDigit(token)) {
                postfixBuilder.append(token);
            } else if (isOpenParenthesis(token)) {
                ops.push(token);
            } else if (isCloseParenthesis(token)) {
                while (!ops.isEmpty() && !isOpenParenthesis(ops.peek())) {
                    postfixBuilder.append(ops.pop());
                }
                if (!ops.isEmpty() && isOpenParenthesis(ops.peek())) {
                    ops.pop();
                }
            } else if (isOperator(token)) {
                while (!ops.isEmpty() && 
                       !isOpenParenthesis(ops.peek()) &&
                       getOpPrecedence.apply(token) <= getOpPrecedence.apply(ops.peek())) {
                    postfixBuilder.append(ops.pop());
                }
                ops.push(token);
            } else {
                System.err.println(String.format("Invalid token: %c", token));
            }
        }
        while (!ops.isEmpty()) {
            postfixBuilder.append(ops.pop());
        }
        return postfixBuilder.toString();
    }

    private static long evaluatePostfix(String postfix) {        
        // Evaluate postfix notation version of expression
        Stack<Long> stk = new Stack<>();
        for (int i = 0; i < postfix.length(); i++) {
            char token = postfix.charAt(i);
            if (isDigit(token)) {
                stk.push(Long.parseLong(String.valueOf(token)));
            } else {
                long operand1 = stk.pop();
                long operand2 = stk.pop();
                if (token == '*') {
                    stk.push(operand1 * operand2);
                } else if (token == '+') {
                    stk.push(operand1 + operand2);
                } else {
                    System.err.println(String.format("Invalid operator: %c", token));
                }
            }
        }
        long exprResult = stk.pop();
        return exprResult;
    }

    private static void task1(Data data) {
        Function<Character, Integer> getOpPrecedence = op -> {
            if (op == '*' || op == '+') {
                return 0;
            } else {
                System.err.println(String.format("Invalid operator: %c", op));
                return -99;
            }
        };

        BigInteger sum = BigInteger.ZERO;

        for (String expr : data.expressions) {
            // remove all whitespace
            expr = expr.replaceAll("\\s+", "");
            String postfix = infixToPostfix(expr, getOpPrecedence);
            long exprResult = evaluatePostfix(postfix);
            sum = sum.add(BigInteger.valueOf(exprResult));
        }
        
        System.out.println(String.format("Task 1: %s", sum.toString()));
    }

    private static void task2(Data data) {
        Function<Character, Integer> getOpPrecedence = op -> {
            if (op == '*') {
                return 0;
            } else if (op == '+') {
                return 1;
            } else {
                System.err.println(String.format("Invalid operator: %c", op));
                return -99;
            }
        };

        BigInteger sum = BigInteger.ZERO;

        for (String expr : data.expressions) {
            // remove all whitespace
            expr = expr.replaceAll("\\s+", "");
            String postfix = infixToPostfix(expr, getOpPrecedence);
            long exprResult = evaluatePostfix(postfix);
            sum = sum.add(BigInteger.valueOf(exprResult));
        }
        System.out.println(String.format("Task 2: %s", sum.toString()));
    }

    public static void main(String[] args) {
        Data data = readData();
        task1(data);
        task2(data);
    }
}
