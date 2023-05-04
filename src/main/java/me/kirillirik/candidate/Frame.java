package me.kirillirik.candidate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class Frame {

    private final String name;
    private final Map<String, Object> slots;
    private String extendName;
    private Frame extend;
    private Function<Frame, String> daemon;

    public Frame(String name) {
        this.name = name;
        this.slots = new HashMap<>();
    }

    public Frame extend(String extendName) {
        this.extendName = extendName;
        return this;
    }

    public Frame extend(Frame extend) {
        this.extend = extend;

        if (extend != null) {
            extendName = extend.getName();
            slots.putAll(extend.getSlots());
        }

        return this;
    }

    public Frame slot(String name, Object value) {
        slots.put(name, value);
        return this;
    }

    public Frame daemon(Function<Frame, String> daemon) {
        this.daemon = daemon;
        return this;
    }

    public boolean checkCondition(Set<Pair<String, String>> inputs) {
        final Map<String, Object> copy = new HashMap<>(slots);

        for (final var entry : slots.entrySet()) {
            for (final Pair<String, String> pair : inputs) {
                if (!pair.getLeft().equalsIgnoreCase(entry.getKey()) ||
                        !pair.getRight().equalsIgnoreCase(entry.getValue().toString())) {
                    continue;
                }

                copy.remove(entry.getKey());
                break;
            }
        }

        return copy.isEmpty();
    }

    public String callDaemon() {
        if (daemon != null) {
            return daemon.apply(this) + (extend != null ? " " + extend.callDaemon() : "");
        }

        return null;
    }

    public void register(DataBase base) {
        if (extendName != null && extend == null) {
            extend(base.getFrames().get(extendName));
        }
    }

    public String getServiceInfo() {
        return this.toString() + (extend != null ? " " + extend.getServiceInfo() : "");
    }

    public String getName() {
        return name;
    }

    public Frame getExtend() {
        return extend;
    }

    public Map<String, Object> getSlots() {
        return slots;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "name='" + name + '\'' +
                ", slots=" + slots +
                ", daemon=" + daemon +
                '}';
    }
}
