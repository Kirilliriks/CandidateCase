package me.kirillirik.candidate;

import java.util.HashSet;
import java.util.Set;

public final class Rule {

    private final String name;
    private final String result;
    private final Set<Frame> frames = new HashSet<>();


    public Rule(String name, String result) {
        this.name = name;
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public String getResult() {
        return result;
    }

    public Set<Frame> getFrames() {
        return frames;
    }

    public Rule addFrame(Frame frame) {
        frames.add(frame);
        return this;
    }

    public boolean checkCondition(Set<Frame> frames) {
        return frames.containsAll(this.frames);
    }
}
