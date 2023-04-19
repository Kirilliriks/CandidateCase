package me.kirillirik.candidate;

import java.util.*;

public final class RuleBase {

    private final List<String> signs = new ArrayList<>();
    private final List<Rule> rules = new ArrayList<>();

    public void addRule(Rule rule) {
        rules.add(rule);
    }
}
