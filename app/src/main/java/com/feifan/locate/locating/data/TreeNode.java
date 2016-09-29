package com.feifan.locate.locating.data;

/**
 * Created by bianying on 16/9/24.
 */
public class TreeNode<T> {

    private T value;
    private TreeNode left;
    private TreeNode right;
    private int height;

    public TreeNode(T value) {
        this.value = value;
    }

    public void setLeft(TreeNode left) {
        this.left = left;
    }

    public void setRight(TreeNode right) {
        this.right = right;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public TreeNode<T> getLeft() {
        return left;
    }

    public TreeNode<T> getRight() {
        return right;
    }

    public T getValue() {
        return value;
    }

    public int getHeight() {
        return height;
    }
}
