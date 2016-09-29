package com.feifan.locate.locating.data;

import com.feifan.baselib.utils.LogUtils;

/**
 * Created by bianying on 16/9/24.
 */
public class AvlTree<T extends Comparable<? super T>> {

    private TreeNode<T> root;

    public AvlTree() {

    }

    /**
     * 插入节点
     * <p>
     *     插入重复元素时失败
     * </p>
     * @param value
     * @return 成功返回true,失败返回false
     */
    public boolean insert(T value) {
        try {
            root = _insert(value, root);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String serialize() {
        StringBuilder str = new StringBuilder();
        serializePrefix (root, str, " ", "#");
        return str.toString();
    }

//    public void deSerialize(String content, Class<T> clazz) {
//
//        String[] contentArray = content.split(" ");
//        for(String value : contentArray) {
//            insert((T)Integer.valueOf(value));
//        }
//    }

    public boolean contains(T value) {
        return _contains(value, root);
    }

    private TreeNode<T> _insert(T value, TreeNode<T> root) throws Exception {
        if(root == null) {
            root = new TreeNode<T>(value);
        }else if(value.compareTo(root.getValue()) < 0) {     // 递归左子树插入
            root.setLeft(_insert(value, root.getLeft()));

            // 平衡判定
            if(height(root.getLeft()) - height(root.getRight()) == 2) { // 不平衡

                if(value.compareTo(root.getLeft().getValue()) < 0) {  // 左子树直线
                    root = r_rotate(root);
                }else {
                    root = l_r_rotate(root);
                }
            }

        }else if(value.compareTo(root.getValue()) > 0) {     // 递归右子树插入
            root.setRight(_insert(value, root.getRight()));

            // 平衡判定
            if(height(root.getRight()) - height(root.getLeft()) == 2) {  // 不平衡

                if(value.compareTo(root.getRight().getValue()) > 0) { // 右子树直线
                    root = l_rotate(root);
                }else {
                    root = r_l_rotate(root);
                }
            }
        }else {
            throw new Exception("duplicate value is not allowed");
        }
        root.setHeight(max(height(root.getLeft()), height(root.getRight())) + 1);
        return root;
    }

    private int max(int a, int b) {
        return a > b ? a : b;
    }

    private int height(TreeNode<T> node) {
        return node == null ? -1 : node.getHeight();
    }

    // 左向右旋,即顺时针
    private TreeNode<T> r_rotate(TreeNode<T> node) {
        TreeNode<T> left = node.getLeft();
        node.setLeft(left.getRight());
        left.setRight(node);
        node.setHeight(max(height(node.getLeft()), height(node.getRight())) + 1);
        left.setHeight(max(height(left.getLeft()), node.getHeight()) + 1);
        return left;
    }

    // 右向左旋,即逆时针
    private  TreeNode<T> l_rotate(TreeNode<T> node) {
        TreeNode<T> right = node.getRight();
        node.setRight(right.getLeft());
        right.setLeft(node);
        node.setHeight(max(height(node.getLeft()), height(node.getRight())) + 1);
        right.setHeight(max(right.getLeft().getHeight(), height(right.getRight())) + 1);
        return right;
    }

    // 左右旋,先左向右旋,再右向左旋
    private TreeNode<T> r_l_rotate(TreeNode<T> node) {
        node.setRight(r_rotate(node.getRight()));
        return l_rotate(node);
    }

    // 右左旋,先右向左旋,再左向右旋
    private TreeNode<T> l_r_rotate(TreeNode<T> node) {
        node.setLeft(l_rotate(node.getLeft()));
        return r_rotate(node);
    }

    private void serializePrefix(TreeNode<T> t, StringBuilder str, String sep, String empty){
        if (t != null){
            str.append(t.getValue().toString());
            str.append(sep);
            serializePrefix (t.getLeft(), str, sep, empty);
            serializePrefix (t.getRight(), str, sep, empty);
        }else {
//            str.append(empty);
//            str.append(sep);
        }
    }

    private boolean _contains(T value, TreeNode<T> node) {
        if(node == null) {
            return false;
        }

        if(value.compareTo(node.getValue()) == 0) {
            return true;
        }else if(value.compareTo(node.getValue()) < 0 ) {
            return _contains(value, node.getLeft());
        }else {
            return _contains(value, node.getRight());
        }
    }
}
