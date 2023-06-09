package me.kirillirik.candidate;

import me.kirillirik.tree.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class Part extends Node {

    private Part parent;
    private final List<Part> children = new ArrayList<>();
    private final boolean sign;
    private final boolean answer;
    private int width = 0;

    public Part(String part) {
        this(part, false);
    }

    public Part(String part, boolean answer) {
        super(part, 0, 0);

        this.answer = answer;

        if (answer) {
            sign = false;
            color.set(0, 255, 0);
            return;
        }

        switch (part.toLowerCase()) {
            case "и", "или" -> {
                sign = false;
                color.set(255, 0, 0);
            }
            default -> {
                sign = true;
                color.set(255, 0, 255);
            }
        }
    }

    public boolean solve(Set<String> signs) {
        if (answer) {
            return children.get(0).solve(signs);
        }

        if (sign) {
            return signs.contains(title.toLowerCase());
        }

        switch (title.toLowerCase()) {
            case "и" -> {
                for (final Part child : children) {
                    if (!child.solve(signs)) {
                        return false;
                    }
                }

                return true;
            }
            case "или" -> {
                for (final Part child : children) {
                    if (child.solve(signs)) {
                        return true;
                    }
                }

                return false;
            }
        }

        throw new IllegalStateException("ISE " + title.toLowerCase());
    }

    public boolean isSign() {
        return sign && !answer;
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
            child.x = x + X_OFFSET;
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
