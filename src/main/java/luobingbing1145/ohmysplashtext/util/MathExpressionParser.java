package luobingbing1145.ohmysplashtext.util;

import java.util.*;
import java.util.function.DoubleUnaryOperator;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class MathExpressionParser {
    private static final Logger logger = Logger.getLogger(MathExpressionParser.class.getName());

    // 入口方法：返回一个 Lambda
    public static DoubleUnaryOperator parse(String input) {
        Node ast;
        try {
            ast = parseExpression(tokenize(input.replaceAll("\\s+", "")));
        } catch (Exception e) {
            // 打印错误信息到日志
            logger.severe("表达式解析错误：" + e.getMessage());

            // 返回默认值表达式
            ast = parseExpression(tokenize("1.8-abs(sin(n%(2000/2)/1000*2*pi)*0.1)"));
        }
        return ast::eval;
    }

    // Tokenizer
    private static List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                int j = i + 1;
                while (j < expr.length() && (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) j++;
                tokens.add(expr.substring(i, j));
                i = j;
            } else if (Character.isLetter(c)) {
                int j = i + 1;
                while (j < expr.length() && Character.isLetter(expr.charAt(j))) j++;
                tokens.add(expr.substring(i, j));
                i = j;
            } else if ("+-*/()%".indexOf(c) >= 0 || c == ',') {
                tokens.add(Character.toString(c));
                i++;
            } else {
                throw new RuntimeException("非法字符: " + c);
            }
        }
        return tokens;
    }

    // AST 节点接口
    interface Node {
        double eval(double n);
    }

    // 数字节点
    static class NumberNode implements Node {
        double value;

        NumberNode(double value) {
            this.value = value;
        }

        public double eval(double n) {
            return value;
        }
    }

    // 变量节点（只支持 n）
    static class VariableNode implements Node {
        public double eval(double n) {
            return n;
        }
    }

    // 二元操作节点
    static class BinaryOpNode implements Node {
        Node left, right;
        String op;

        BinaryOpNode(String op, Node left, Node right) {
            this.op = op;
            this.left = left;
            this.right = right;
        }

        public double eval(double n) {
            double a = left.eval(n);
            double b = right.eval(n);
            return switch (op) {
                case "+" -> a + b;
                case "-" -> a - b;
                case "*" -> a * b;
                case "/" -> a / b;
                case "%" -> a % b;
                default -> throw new RuntimeException("未知操作: " + op);
            };
        }
    }

    // 一元函数节点
    static class FuncNode implements Node {
        String func;
        Node arg;

        FuncNode(String func, Node arg) {
            this.func = func;
            this.arg = arg;
        }

        public double eval(double n) {
            double x = arg.eval(n);
            return switch (func) {
                case "sin" -> Math.sin(x);
                case "cos" -> Math.cos(x);
                case "abs" -> Math.abs(x);
                default -> throw new RuntimeException("未知函数: " + func);
            };
        }
    }

    // 表达式语法分析器（Shunting Yard + 递归下降）
    private static Node parseExpression(List<String> tokens) {
        return parseExpr(new LinkedList<>(tokens));
    }

    private static Node parseExpr(Queue<String> tokens) {
        return parseAddSub(tokens);
    }

    private static Node parseAddSub(Queue<String> tokens) {
        Node left = parseMulDiv(tokens);
        while (!tokens.isEmpty() && (tokens.peek().equals("+") || tokens.peek().equals("-"))) {
            String op = tokens.poll();
            Node right = parseMulDiv(tokens);
            left = new BinaryOpNode(op, left, right);
        }
        return left;
    }

    private static Node parseMulDiv(Queue<String> tokens) {
        Node left = parseFactor(tokens);
        while (!tokens.isEmpty() && (tokens.peek().equals("*") || tokens.peek().equals("/") || tokens.peek().equals("%"))) {
            String op = tokens.poll();
            Node right = parseFactor(tokens);
            left = new BinaryOpNode(op, left, right);
        }
        return left;
    }

    private static Node parseFactor(Queue<String> tokens) {
        String token = tokens.poll();
        if (token == null) throw new RuntimeException("表达式不完整");

        // 处理负号前缀
        if (token.equals("-")) {
            // 如果负号后面是数字或变量，解析它为负数
            if (tokens.peek() != null && isNumber(tokens.peek())) {
                return new NumberNode(-Double.parseDouble(tokens.poll()));
            }
            // 如果负号后面是变量 "n"
            if (tokens.peek() != null && tokens.peek().equals("n")) {
                tokens.poll(); // 移除 "n"
                return new VariableNode();
            }
            // 如果负号后面是函数
            if (tokens.peek() != null && isFunction(tokens.peek())) {
                String func = tokens.poll();
                if (!Objects.equals(tokens.poll(), "(")) throw new RuntimeException("函数调用缺少括号");
                Node arg = parseExpr(tokens);
                if (!Objects.equals(tokens.poll(), ")")) throw new RuntimeException("函数调用括号未闭合");
                return new FuncNode(func, arg); // 创建负的函数调用
            }
            // 如果负号后面是括号表达式，递归解析括号里的内容
            if (tokens.peek() != null && tokens.peek().equals("(")) {
                Node inner = parseExpr(tokens);
                if (!Objects.equals(tokens.poll(), ")")) throw new RuntimeException("括号未闭合");
                return new BinaryOpNode("-", new NumberNode(0), inner); // 负号可以视为 -1 * 表达式
            }

            throw new RuntimeException("非法负号后缀: " + token);
        }

        if (token.equals("(")) {
            Node inner = parseExpr(tokens);
            if (!Objects.equals(tokens.poll(), ")")) throw new RuntimeException("括号未闭合");
            return inner;
        }

        if (isNumber(token)) return new NumberNode(Double.parseDouble(token));
        if (token.equals("n")) return new VariableNode();
        // 新增支持 pi
        if (token.equalsIgnoreCase("pi")) return new NumberNode(Math.PI);

        if (isFunction(token)) {
            if (!Objects.equals(tokens.poll(), "(")) throw new RuntimeException("函数调用缺少括号");
            Node arg = parseExpr(tokens);
            if (!Objects.equals(tokens.poll(), ")")) throw new RuntimeException("函数调用括号未闭合");
            return new FuncNode(token, arg);
        }

        throw new RuntimeException("无法识别的标记: " + token);
    }

    private static boolean isNumber(String s) {
        return s.matches("\\d+(\\.\\d+)?");
    }

    private static boolean isFunction(String s) {
        return s.equals("sin") || s.equals("cos") || s.equals("abs");
    }

    static {
        try {
            LogManager.getLogManager().readConfiguration();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
