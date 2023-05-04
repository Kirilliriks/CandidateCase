package me.kirillirik.candidate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Frame {

    private final String name;
    private final Map<String, Object> slots;
    private Function<Frame, String> daemon;

    public Frame(String name) {
        this.name = name;
        this.slots = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public Frame slot(String name, Object value) {
        slots.put(name, value);
        return this;
    }

    public Frame daemon(Function<Frame, String> daemon) {
        this.daemon = daemon;
        return this;
    }

    public boolean checkCondition(Set<String> input) {
        for (final Object value : slots.values()) {
            if (input.contains(value.toString())) {
                continue;
            }

            return false;
        }

        return true;
    }

    public void callDaemon() {
        if (daemon != null) {
            daemon.apply(this);
        }
    }
}
