package me.kirillirik.candidate;

import imgui.ImGui;
import imgui.flag.ImGuiTableFlags;
import imgui.type.ImString;

import java.util.HashSet;
import java.util.Set;

public final class Base {

    private final DataBase dataBase;
    private final Set<Pair<String, String>> values = new HashSet<>();

    private final ImString first;
    private final ImString second;

    private State state;

    private String answer;


    public Base() {
        dataBase = new DataBase();
        state = State.MENU;

        first = new ImString();
        second = new ImString();
    }

    public void update() {
        ImGui.begin("Кандидат");

        if (ImGui.button("Начать ввод значений")) {
            setState(State.INPUT_SLOTS);
        }
        ImGui.sameLine();
        if (ImGui.button("Показать фреймы")) {
            setState(State.SHOW_FRAMES);
        }
        ImGui.sameLine();
        if (ImGui.button("Показать правила")) {
            setState(State.SHOW_RULES);
        }

        switch (state) {
            case INPUT_SLOTS -> inputSlots();
            case SHOW_ANSWER -> showAnswer();
            case SHOW_FRAMES -> showFrames();
            case SHOW_RULES -> showRules();
        }

        ImGui.end();
    }

    private void inputSlots() {
        ImGui.text("Значения: " +
                String.join(", ",
                        values.stream().map(entry -> entry.getLeft() + ":" + entry.getRight()).toList()
                )
        );
        ImGui.inputText("Введите название признака(слота)", first);
        ImGui.inputText("Введите значение", second);

        if (ImGui.button("Ввести ещё одно значение")) {
            addInput();
        }
        ImGui.sameLine();
        if (ImGui.button("Закончить ввод значений")) {
            addInput();
            answer = dataBase.findAnswer(values);

            setState(State.SHOW_ANSWER);
        }
    }

    private void showAnswer() {
        if (answer == null) {
            ImGui.text("На заданные значения ответ не найден");
            return;
        }

        ImGui.text("Ответ: " + answer);
    }

    private void showRules() {
        final int flags = ImGuiTableFlags.Resizable | ImGuiTableFlags.Hideable | ImGuiTableFlags.RowBg |
                ImGuiTableFlags.Borders | ImGuiTableFlags.NoBordersInBody | ImGuiTableFlags.ScrollY;

        if (ImGui.beginTable("#table", 3, flags)) {
            ImGui.tableSetupColumn("Название");
            ImGui.tableSetupColumn("Название фреймов");
            ImGui.tableSetupColumn("Результат");

            ImGui.tableHeadersRow();

            for (final Rule rule : dataBase.getRules()) {
                ImGui.tableNextRow(ImGuiTableFlags.None, 10);

                ImGui.tableSetColumnIndex(0);
                ImGui.text(rule.getName());

                ImGui.tableSetColumnIndex(1);
                ImGui.text(String.join(", ", rule.getFrames().stream().map(Frame::getName).toList()));

                ImGui.tableSetColumnIndex(2);
                ImGui.text(rule.getResult());
            }

            ImGui.endTable();
        }
    }

    private void showFrames() {
        final int flags = ImGuiTableFlags.Resizable | ImGuiTableFlags.Hideable | ImGuiTableFlags.RowBg |
                ImGuiTableFlags.Borders | ImGuiTableFlags.NoBordersInBody | ImGuiTableFlags.ScrollY;

        if (ImGui.beginTable("#table", 3, flags)) {
            ImGui.tableSetupColumn("Название");
            ImGui.tableSetupColumn("Слоты");
            ImGui.tableSetupColumn("Результат выполнения демона");

            ImGui.tableHeadersRow();

            for (final var entry : dataBase.getFrames().entrySet()) {
                ImGui.tableNextRow(ImGuiTableFlags.None, 10);

                ImGui.tableSetColumnIndex(0);
                ImGui.text(entry.getKey());

                ImGui.tableSetColumnIndex(1);
                ImGui.text(String.join(", ", entry.getValue().getSlots().values().stream().map(Object::toString).toList()));

                ImGui.tableSetColumnIndex(2);
                ImGui.text(entry.getValue().callDaemon());
            }

            ImGui.endTable();
        }
    }

    private void addInput() {
        final String firstString = first.get();
        first.clear();

        if (firstString == null || firstString.isEmpty()) {
            return;
        }

        final String secondString = second.get();
        second.clear();

        if (secondString == null || secondString.isEmpty()) {
            return;
        }

        values.add(new Pair<>(firstString.toLowerCase(), secondString.toLowerCase()));
    }

    public void setState(State state) {
        this.state = state;

        values.clear();
        first.clear();
        second.clear();
    }

    public enum State {
        MENU,
        INPUT_SLOTS,
        SHOW_ANSWER,
        SHOW_FRAMES,
        SHOW_RULES
    }
}
