package me.kirillirik.candidate;

import java.util.Set;

public final class Rule {

    private final String name;
    private final Set<Frame> frames;
    private final String result;

    public Rule(String name, Set<Frame> frames, String result) {
        this.name = name;
        this.frames = frames;
        this.result = result;
    }
}
