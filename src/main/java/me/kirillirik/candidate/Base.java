package me.kirillirik.candidate;

import imgui.ImColor;
import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.extension.imnodes.ImNodes;
import imgui.extension.imnodes.ImNodesStyle;
import imgui.extension.imnodes.flag.ImNodesColorStyle;
import imgui.extension.imnodes.flag.ImNodesMiniMapLocation;
import imgui.flag.*;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import me.kirillirik.Window;
import me.kirillirik.tree.Color;
import me.kirillirik.tree.Node;

import java.util.*;

public final class Base {

    private int linkID = 0;

    private final SignBase signBase;
    private final RuleBase ruleBase;

    private final ImString first;
    private final ImString second;
    private String answer;
    private final List<Rule> foundRules = new ArrayList<>();
    private final Set<String> inputSigns = new HashSet<>();
    private final Set<String> uniqueString = new HashSet<>();

    private State state;
    private String error;

    private boolean positioned;
    private Part root;

    public Base() {
        signBase = new SignBase();
        ruleBase = new RuleBase(signBase);
        first = new ImString(1000);
        second = new ImString(1000);

        setState(State.INIT);
        load();
    }

    public void init() {
        ImGui.styleColorsLight();

        final ImGuiStyle style = ImGui.getStyle();
        style.setWindowRounding(1.0f);

        final ImNodesStyle nodesStyle = ImNodes.getStyle();
        nodesStyle.setGridSpacing(0.0f);
    }

    private void updateRoot(Rule rule) {
        Node.NODE_COUNTER = 0;
        linkID = 0;

        root = rule.getRoot();
        root.setX(100);
        root.setY(100);
        root.updateWidth();
        root.updatePos();

        positioned = false;
    }

    public void update() {
        linkID = 0;

        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;

        ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.Always);
        ImGui.setNextWindowSize(Window.getWidth(), Window.getHeight());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        windowFlags |= ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.begin("Кандидат", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(2);

        ImGui.beginMenuBar();

        if (ImGui.menuItem("Получить признаки из ответа (Обратный вывод)")) {
            setState(State.BACK);
        }

        if (ImGui.menuItem("Просмотр правил")) {
            setState(State.RULES);
        }

        if (ImGui.menuItem("Просмотр признаков")) {
            setState(State.SIGNS);
        }

        if (ImGui.menuItem("Просмотр графа")) {
            setState(State.TREE);
        }

        if (error != null) {
            ImGui.text(error);
        }

        ImGui.endMenuBar();

        switch (state) {
            case BACK -> back();
            case BACK_PROCESS -> backProcess();
            case RULES -> rules();
            case SIGNS -> signs();
            case TREE -> tree();
        }

        ImGui.end();
    }

    private void back() {
        ImGui.text("Введите результат");
        ImGui.inputText("##first", first);

        if (!ImGui.button("Поиск")) {
            return;
        }

        answer = first.get();
        foundRules();
    }

    private void foundRules() {
        foundRules.clear();
        foundRules.addAll(ruleBase.getRulesByAnswerAndSigns(answer, inputSigns));
        if (foundRules.isEmpty()) {
            error = "Не найдены правила для такого результата";
            return;
        }

        error = null;

        first.clear();
        state = State.BACK_PROCESS;

        if (foundRules.size() == 1) {
            final Rule rule = foundRules.get(0);
            updateRoot(rule);
            return;
        }

        final Map<String, Integer> stringIntegerMap = new HashMap<>();
        for (final Rule rule : foundRules) {
            for (final String string : signBase.singsFrom(rule)) {
                final int size = stringIntegerMap.merge(string, 1, Integer::sum);

                if (size >= foundRules.size()) {
                    stringIntegerMap.remove(string);
                    inputSigns.add(string);
                }
            }
        }

        uniqueString.addAll(stringIntegerMap.keySet());
        uniqueString.removeAll(inputSigns);
    }

    private void backProcess() {
        if (foundRules.size() == 1) {
            final Rule rule = foundRules.get(0);
            ImGui.text("Признаки для этого результата: " + String.join(", ", signBase.singsFrom(rule)));
        } else {
            ImGui.text("Наборов признаков для этого результата больше чем 1");
            ImGui.text("Уточните есть ли один из таких признаков: " + String.join(", ", uniqueString));

            ImGui.text("Введите признак");
            ImGui.inputText("##first", first);

            if (!ImGui.button("Добавить признак")) {
                return;
            }

            final String sign = first.get();
            if (!signBase.contains(sign.toLowerCase())) {
                error = "Такого признака нет в базе данных!";
                return;
            }

            inputSigns.add(sign);

            foundRules();
        }
    }

    private void rules() {
        ImGui.text("ЕСЛИ ");
        ImGui.sameLine();
        ImGui.setNextItemWidth(250);
        ImGui.inputText("##ESLI", first);
        ImGui.sameLine();
        ImGui.text(" ТО ");
        ImGui.sameLine();
        ImGui.setNextItemWidth(250);
        ImGui.inputText("##TO", second);

        ImGui.spacing();
        if (ImGui.button("Добавить")) {
            final String expression = first.get();
            if (expression == null || expression.isEmpty()) {
                error = "Введите правило в поле ЕСЛИ";
                return;
            }

            final String answer = second.get();
            if (answer == null || answer.isEmpty()) {
                error = "Введите результат в поле ТО";
                return;
            }

            try {
                final Rule rule = new Rule(expression, answer);
                ruleBase.addRule(rule);

                updateRoot(rule);
            } catch (Throwable ignore) {
                error = "Ошибка в водимом правиле";
            }
        }

        ImGui.spacing();
        ImGui.spacing();
        ImGui.spacing();

        final int flags = ImGuiTableFlags.Resizable | ImGuiTableFlags.Reorderable | ImGuiTableFlags.Hideable |
                ImGuiTableFlags.RowBg | ImGuiTableFlags.Borders | ImGuiTableFlags.NoBordersInBody | ImGuiTableFlags.ScrollY;

        if (ImGui.beginTable("#table", 4, flags)) {
            ImGui.tableSetupColumn("Выражение");
            ImGui.tableSetupColumn("Результат");

            ImGui.tableHeadersRow();

            Iterator<Rule> ruleIterator = ruleBase.getRules().iterator();
            while (ruleIterator.hasNext()){
                final Rule rule = ruleIterator.next();

                ImGui.tableNextRow(ImGuiTableFlags.None, 10);

                ImGui.tableSetColumnIndex(0);
                ImGui.text(rule.getExpression());

                ImGui.tableSetColumnIndex(1);
                ImGui.text(rule.getAnswer());

                ImGui.tableSetColumnIndex(2);
                if (ImGui.button("Удалить правило###1" + rule.getExpression())) {
                    ruleIterator.remove();
                }

                ImGui.tableSetColumnIndex(3);
                if (rule.getRoot() == root) {
                    ImGui.button("Просмотратриваемое дерево###3");
                } else if (ImGui.button("Просмотр дерева###2" + rule.getExpression())) {
                    updateRoot(rule);
                }
            }

            ImGui.endTable();
        }
    }

    private void signs() {
        final int flags = ImGuiTableFlags.Resizable | ImGuiTableFlags.Reorderable | ImGuiTableFlags.Hideable |
                ImGuiTableFlags.RowBg | ImGuiTableFlags.Borders | ImGuiTableFlags.NoBordersInBody | ImGuiTableFlags.ScrollY;

        if (ImGui.beginTable("#table", 1, flags)) {
            ImGui.tableSetupColumn("Признак");

            ImGui.tableHeadersRow();

            for (final var sign : signBase.getSigns()) {

                ImGui.tableNextRow(ImGuiTableFlags.None, 10);

                ImGui.tableSetColumnIndex(0);
                ImGui.text(sign);
            }

            ImGui.endTable();
        }
    }

    private void tree() {
        ImNodes.beginNodeEditor();

        if (root != null) {
            displayTreeNode(root);
            linkTreeNode(root);
        }

        ImNodes.miniMap(0.2f, ImNodesMiniMapLocation.BottomRight);
        ImNodes.endNodeEditor();

        if (!positioned) {
            positioned = true;
        }
    }

    public void setState(State state) {
        this.state = state;

        if (state == State.TREE) {
            return;
        }

        error = null;

        first.clear();
        second.clear();
        foundRules.clear();
        answer = null;
        uniqueString.clear();
        inputSigns.clear();
    }

    private void linkNode(Node node) {
        for (final int id : node.getLinks()) {
            ImNodes.link(linkID++, node.getOutputID(), id);
        }
    }

    private void displayNode(Node node) {
        final Color color = node.getColor();
        ImNodes.pushColorStyle(ImNodesColorStyle.NodeBackground,
                ImColor.intToColor(color.getR(), color.getG(), color.getB()));

        final int id = node.getID();
        ImNodes.beginNode(id);
        if (!positioned) {
            ImNodes.setNodeGridSpacePos(id, node.getX(), node.getY());
        }

        ImNodes.beginNodeTitleBar();
        ImGui.text(node.getTitle());
        ImNodes.endNodeTitleBar();

        ImNodes.beginInputAttribute(node.getInputID());
        ImNodes.endInputAttribute();

        ImNodes.beginOutputAttribute(node.getOutputID());
        ImNodes.endOutputAttribute();

        ImNodes.endNode();
    }

    private void linkTreeNode(Part node) {
        linkNode(node);
        for (final var child : node.getChildren()) {
            linkTreeNode(child);
        }
    }

    private void displayTreeNode(Part node) {
        displayNode(node);
        for (final var child : node.getChildren()) {
            displayTreeNode(child);
        }
    }

    public void load() {
        signBase.load();
        ruleBase.load();
    }

    public void save() {
        signBase.save();
        ruleBase.save();
    }

    public enum State {
        INIT,
        RULES,
        SIGNS,
        BACK,
        BACK_PROCESS,
        TREE
    }
}
