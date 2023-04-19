package me.kirillirik.candidate;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;

public final class Rule {

    private final String expression;
    private final String answer;

    public Rule(String expression, String answer) {
        this.expression = expression;
        this.answer = answer;
    }

    private void processOperator(Deque<Integer> numbers, String operation) {
        int right = numbers.removeLast();
        int left = numbers.removeLast();
        switch (operation) {
            case "или", "ИЛИ" -> numbers.add(left + right);
            case "и", "И" -> numbers.add(left * right);
        }
    }

    public boolean parse(Set<String> signs) {
        final Deque<Integer> numbers = new ArrayDeque<>();
        final Deque<String> operations = new ArrayDeque<>();

        final String[] parts = expression.split(" ");

        for (final String str : parts) {
            switch (str) {
                case "(" -> operations.add(str);
                case ")" -> {
                    while (!operations.getLast().equals("(")) {
                        processOperator(numbers, operations.removeLast());
                    }

                    operations.removeLast();
                }
                case "и", "И", "или", "ИЛИ" -> {
                    while (!operations.isEmpty() && operationPriority(operations.getLast()) >= operationPriority(str)) {
                        processOperator(numbers, operations.removeLast());
                    }

                    operations.add(str);
                }
                default -> {
                    if (signs.contains(str)) {
                        numbers.add(1);
                    } else {
                        numbers.add(0);
                    }
                }
            }
        }

        System.out.println(numbers);


        while (!operations.isEmpty()) {
            System.out.println(operations.getLast());
            processOperator(numbers, operations.removeLast());
        }

        return numbers.getFirst() >= 1;
    }

    private int operationPriority(String operation) {
        return switch (operation) {
            case "или", "ИЛИ" -> 1;
            case "и", "И" -> 2;
            default -> -1;
        };
    }
}
