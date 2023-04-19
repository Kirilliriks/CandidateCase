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

import java.util.*;

public final class Base {

    private final List<Node> nodes = new ArrayList<>();
    private int linkID = 0;



    private final RuleBase ruleBase;
    private State state;
    private String error;

    private boolean needClose;
    private boolean positioned;


    public Base() {
        ruleBase = new RuleBase();

        Node.NODE_COUNTER = 0;

        state = State.INIT;
    }

    private void colorPath(float r, float g, float b, TreeNode node) {
        if (node == null) {
            return;
        }

        node.getColor().set(r, g, b);
        colorPath(r, g, b, node.getParent());
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
            }
            case NEW_RULE ->  {
                ImGui.text("Введите новое правило в формате \"ЕСЛИ x1 ТО x2 \"");
                ImGui.text("Где x1 - признак1 и/или признак2 и/или .... и/или признакN");
                ImGui.text("Где x2 - строка результат");
                ImGui.setNextItemWidth(600);
                ImGui.inputText("ЕСЛИ ", new ImString());
                ImGui.inputText("ТО ", new ImString());
            }
        }

        ImGui.end();

        ImGui.begin("Дерево");
        ImNodes.beginNodeEditor();

        //displayTreeNode(root);
        //linkTreeNode(root);

        ImNodes.miniMap(0.2f, ImNodesMiniMapLocation.BottomRight);
        ImNodes.endNodeEditor();
        ImGui.end();


        if (!positioned) {
            positioned = true;
        }
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

    private void linkTreeNode(TreeNode node) {
        linkNode(node);
        for (final var child : node.getChildren()) {
            linkTreeNode(child);
        }
    }

    private void displayTreeNode(TreeNode node) {
        displayNode(node);
        for (final var child : node.getChildren()) {
            displayTreeNode(child);
        }
    }

    public boolean isNeedClose() {
        return needClose;
    }


    public enum State {
        INIT,
        NEW_RULE,
        GET_INFO
    }
}
