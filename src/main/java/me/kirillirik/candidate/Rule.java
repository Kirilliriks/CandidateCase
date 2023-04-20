package me.kirillirik.candidate;

import java.util.*;

public final class Rule {

    private final String expression;
    private final String answer;
    private Part root;


    public Rule(String expression, String answer) {
        this.expression = expression;
        this.answer = answer;
        parse();
    }

    public boolean solve(Set<String> signs) {
        return root.solve(signs);
    }

    public String getExpression() {
        return expression;
    }

    public String getAnswer() {
        return answer;
    }

    public Part getRoot() {
        return root;
    }

    private void processOperator(Deque<Part> parts, String operation) {
        final Part right = parts.removeLast();
        final Part left = parts.removeLast();

        final Part operationPart = new Part(operation);
        switch (operation.toLowerCase()) {
            case "или", "и" -> {
                operationPart.addChild(right);
                operationPart.addChild(left);
                parts.add(operationPart);
            }
        }
    }

    private void parse() {
        final Deque<Part> parts = new ArrayDeque<>();
        final Deque<String> operations = new ArrayDeque<>();
        final String[] stringParts = expression.split(" ");

        boolean operation = false;
        for (final String str : stringParts) {
            operation = process(str, operations, parts, operation);
        }

        while (!operations.isEmpty()) {
            processOperator(parts, operations.removeLast());
        }

        root = parts.getLast();
    }

    private boolean process(String str, Deque<String> operations, Deque<Part> parts, boolean operation) {
        if (str.length() > 1 && str.startsWith("(")) {
            process("(", operations, parts, operation);
            operation = process(str.substring(1), operations, parts, operation);
            return operation;
        }

        if (str.length() > 1 && str.endsWith(")")) {
            operation = process(str.substring(0, str.length() - 1), operations, parts, operation);
            process(")", operations, parts, operation);
            return operation;
        }

        switch (str) {
            case "(" -> operations.add(str);
            case ")" -> {
                while (!operations.getLast().equals("(")) {
                    processOperator(parts, operations.removeLast());
                }

                operations.removeLast();
            }
            case "и", "И", "или", "ИЛИ" -> {
                while (!operations.isEmpty() && operationPriority(operations.getLast()) >= operationPriority(str)) {
                    processOperator(parts, operations.removeLast());
                }

                operations.add(str);
            }
            default -> {
                if (operation) {
                    parts.getLast().addPart(str);
                } else {
                    parts.add(new Part(str));
                }

                return true;
            }
        }

        return false;
    }

    private int operationPriority(String operation) {
        return switch (operation) {
            case "или", "ИЛИ" -> 1;
            case "и", "И" -> 2;
            default -> -1;
        };
    }
}
