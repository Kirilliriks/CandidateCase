package me.kirillirik.candidate;

import java.util.*;

public final class DataBase {

    private final Map<String, Frame> frames = new HashMap<>();
    private final Set<Rule> rules = new HashSet<>();

    public DataBase() {
        final Frame freeFrame = new Frame("Чёткие требования")
                .slot("Образование", "Высшее")
                .slot("Свободное время", "Есть")
                .daemon(frame -> "Основные требования к соискателям");

        addFrames(
                freeFrame,
                new Frame("Пригласить на собеседование")
                        .slot("Резюме", "Есть")
                        .slot("Номер телефона", "Есть")
                        .daemon(frame -> "Компания пригласит на собеседование"),
                new Frame("Подходящий студент")
                        .extend(freeFrame)
                        .slot("Курс", 4)
                        .daemon(frame -> "Компания принимает студентов с " + frame.getSlots().get("Курс") + " курса"),
                new Frame("Подходящий стажёр")
                        .extend(freeFrame)
                        .slot("Навык программирования", "Есть")
                        .daemon(frame -> "Компания принимает стажёров с навыками программирования")
        );

        addRule(
                new Rule("Прошёл собеседование стажёром", "Вы прошли собеседование на позицию: стажёр")
                        .addFrame(frames.get("Подходящий стажёр")),
                new Rule("Прошёл собеседование практикантом", "Вы прошли собеседование на позицию: практикант")
                        .addFrame(frames.get("Подходящий студент"))
        );
    }

    public void addFrames(Frame... inputFrames) {
        for (final Frame frame : inputFrames) {
            frames.put(frame.getName(), frame);
        }
    }

    public void addRule(Rule... inputRules) {
        rules.addAll(Arrays.asList(inputRules));
    }

    public String findAnswer(Set<Pair<String, String>> inputs) {
        final Set<Frame> foundFrames = new HashSet<>();

        for (final Frame frame : frames.values()) {
            if (frame.checkCondition(inputs)) {
                foundFrames.add(frame);
            }
        }

        if (foundFrames.isEmpty()) {
            return null;
        }

        for (final Rule rule : rules) {
            if (rule.checkCondition(foundFrames)) {
                return rule.getResult();
            }
        }

        return null;
    }

    public Map<String, Frame> getFrames() {
        return frames;
    }

    public Set<Rule> getRules() {
        return rules;
    }
}
