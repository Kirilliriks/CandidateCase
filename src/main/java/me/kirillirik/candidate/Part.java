package me.kirillirik.candidate;

import me.kirillirik.tree.Node;

import java.util.ArrayList;
import java.util.List;

public final class Part extends Node {

    private Part parent;
    private final List<Part> children = new ArrayList<>();
    private final boolean sign;
    private int width = 0;

    public Part(String part) {
        super(part, 0, 0);

        switch (part) {
            case "и", "И", "или", "ИЛИ" -> sign = false;
            default -> sign = true;
        }
    }

    public boolean isSign() {
        return sign;
    }

    public void addPart(String add) {
        title += " " + add;
    }

    public void setParent(Part parent) {
        this.parent = parent;
    }

    public void updatePos() {
        if (children.isEmpty()) {
            return;
        }

        if (children.size() == 1) {
            final Part child = children.get(0);
            child.y = y;
            child.updatePos();
            return;
        }

        final int childrenSize = children.size();
        final double offset = width / (double) childrenSize;

        int i = -childrenSize / 2;
        for (final Part child : children) {
            child.y = (int) Math.round(y + i * offset * Y_OFFSET);
            child.x = x + X_OFFSET;
            child.updatePos();
            i++;
        }
    }

    public void addChild(Part part) {
        part.setParent(this);
        addLink(part.getID());

        children.add(part);
    }

    public int updateWidth() {
        width = 1;

        for (final Part child : children) {
            width += child.updateWidth();
        }

        return width;
    }

    public List<Part> getChildren() {
        return children;
    }
}
