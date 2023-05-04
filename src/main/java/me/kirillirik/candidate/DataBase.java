package me.kirillirik.candidate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class DataBase {

    private final Map<String, Frame> frames = new HashMap<>();
    private final Set<Rule> rules = new HashSet<>();

    public DataBase() {
        addFrame(
                new Frame("Тестовый фрейм")
                        .slot("Тестовое число", 123)
                        .slot("Тестовая строка", "Строка")
                        .daemon(frame -> "Тестовый демон " + frame.getName())
        );


    }

    public void addFrame(Frame frame) {
        frames.put(frame.getName(), frame);
    }
}
