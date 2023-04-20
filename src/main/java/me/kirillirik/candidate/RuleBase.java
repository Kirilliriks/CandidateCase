package me.kirillirik.candidate;

import java.io.*;
import java.util.*;

public final class RuleBase {

    private static final String BASE_FILE = "rulebase.txt";

    private final SignBase signBase;
    private final List<Rule> rules = new ArrayList<>();

    public RuleBase(SignBase signBase) {
        this.signBase = signBase;
    }

    public void addRule(Rule rule) {
        signBase.addIfNeedSigns(rule);

        rules.add(rule);
    }

    public List<Rule> getRulesByAnswerAndSigns(String answer, Set<String> signs) {
        final List<Rule> result = new ArrayList<>();
        for (final Rule rule : rules) {
            if (!rule.getAnswer().equalsIgnoreCase(answer)) {
                continue;
            }

            if (!signs.isEmpty()) {
                if (!rule.solve(signs)) {
                    continue;
                }

                if (signBase.singsFrom(rule).containsAll(signs)) {
                    result.clear();
                    result.add(rule);
                    return result;
                }
            }

            result.add(rule);
        }

        return result;
    }

    public void save() {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(BASE_FILE))) {
            for (final Rule rule : rules) {
                writer.write(rule.getExpression() + ":" + rule.getAnswer());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void load() {
        final File file = new File(BASE_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (final BufferedReader reader = new BufferedReader(new FileReader(BASE_FILE))) {
            String line = reader.readLine();

            while (line != null) {
                final String[] split = line.split(":");

                addRule(new Rule(split[0], split[1]));

                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Rule> getRules() {
        return rules;
    }
}
