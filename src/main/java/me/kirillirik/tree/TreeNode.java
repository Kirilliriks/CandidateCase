package me.kirillirik.tree;

import java.util.ArrayList;
import java.util.List;

public class TreeNode extends Node {

    private final TreeNode parent;
    private final List<TreeNode> children = new ArrayList<>();

    private int width = 0;

    public TreeNode(TreeNode parent, String title, int x, int y) {
        super(title, x, y);
        this.parent = parent;
    }

    public void updatePos() {
        if (children.isEmpty()) {
            return;
        }

        if (children.size() == 1) {
            final TreeNode child = children.get(0);
            child.y = y;
            child.updatePos();
            return;
        }

        final int childrenSize = children.size();
        final double offset = width / (double) childrenSize;

        int i = -childrenSize / 2;
        for (final TreeNode child : children) {
            child.y = (int) Math.round(y + i * offset * Y_OFFSET);
            child.updatePos();
            i++;
        }
    }

    public TreeNode addChild(String title, int dataID) {
        final var newChild = new TreeNode(this, title, x + X_OFFSET, y);
        addLink(newChild.getID());

        children.add(newChild);
        return newChild;
    }

    public int updateWidth() {
        width = 1;

        for (final TreeNode child : children) {
            width += child.updateWidth();
        }

        return width;
    }

    public TreeNode getParent() {
        return parent;
    }

    public int getWidth() {
        return width;
    }

    public List<TreeNode> getChildren() {
        return children;
    }
}
