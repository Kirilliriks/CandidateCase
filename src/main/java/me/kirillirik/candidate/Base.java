package me.kirillirik.candidate;

import imgui.ImColor;
import imgui.ImGui;
import imgui.extension.imnodes.ImNodes;
import imgui.extension.imnodes.flag.ImNodesColorStyle;
import imgui.extension.imnodes.flag.ImNodesMiniMapLocation;
import imgui.type.ImString;
import me.kirillirik.tree.Color;
import me.kirillirik.tree.Node;
import me.kirillirik.tree.TreeNode;

public final class Base {

    private int linkID = 0;

    private final SignBase signBase;
    private final RuleBase ruleBase;

    private final ImString first;
    private final ImString second;
    private State state;
    private String error;

    private boolean positioned;
    private Part root;

    public Base() {
        signBase = new SignBase();
        ruleBase = new RuleBase(signBase);
        first = new ImString();
        second = new ImString();

        Node.NODE_COUNTER = 0;

        state = State.INIT;
        //test = new Rule("Идёт снег и Б или (В и Д или Г и (Кажется дождь начинается и С))", "check");

        load();
    }

    private void colorPath(float r, float g, float b, TreeNode node) {
        if (node == null) {
            return;
        }

        node.getColor().set(r, g, b);
        colorPath(r, g, b, node.getParent());
    }

    private void updateRoot() {
        root = ruleBase.getLastRule().getRoot();
        root.setX(100);
        root.setY(100);
        root.updateWidth();
        root.updatePos();

        positioned = false;
    }

    public void update() {
        linkID = 0;

        ImGui.begin("Кандидат");
        if (error != null) {
            ImGui.text(error);
        }

        switch (state) {
            case INIT -> {
                ImGui.text("Выберете действие");
                ImGui.sameLine();
                if (ImGui.button("Добавить правило")) {
                    state = State.NEW_RULE;
                }
                ImGui.sameLine();
                if (ImGui.button("Получить ответ")) {
                    state = State.FORWARD;
                }
                ImGui.sameLine();
                if (ImGui.button("Получить признаки из ответа")) {
                    state = State.BACK;
                }
            }
            case NEW_RULE ->  {
                ImGui.text("Введите новое правило в формате \"ЕСЛИ x1 ТО x2 \"");
                ImGui.text("Где x1 - признак1 и/или признак2 и/или .... и/или признакN");
                ImGui.text("Где x2 - строка результат");
                ImGui.setNextItemWidth(400);
                ImGui.inputText("ЕСЛИ ", first);
                ImGui.setNextItemWidth(400);
                ImGui.inputText("ТО ", second);

                if (ImGui.button("Добавить")) {
                    state = State.INIT;

                    ruleBase.addRule(new Rule(first.get(), second.get()));
                    first.clear();
                    second.clear();

                    updateRoot();
                }
            }
        }

        ImGui.end();

        ImGui.begin("Дерево");
        ImNodes.beginNodeEditor();

        if (root != null) {
            displayTreeNode(root);
            linkTreeNode(root);
        }

        ImNodes.miniMap(0.2f, ImNodesMiniMapLocation.BottomRight);
        ImNodes.endNodeEditor();
        ImGui.end();


        if (!positioned) {
            positioned = true;
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

    private void linkNode(Node node) {
        for (final int id : node.getLinks()) {
            ImNodes.link(linkID++, node.getOutputID(), id);
        }
    }

    private void displayNode(Node node) {
        final Color color = node.getColor();
        ImNodes.pushColorStyle(ImNodesColorStyle.NodeBackground,
                ImColor.floatToColor(color.getR(), color.getG(), color.getB()));

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

    public enum State {
        INIT,
        NEW_RULE,
        FORWARD,
        BACK
    }
}
