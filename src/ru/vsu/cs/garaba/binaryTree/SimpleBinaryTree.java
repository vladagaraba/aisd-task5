package ru.vsu.cs.garaba.binaryTree;

import java.util.function.Function;

public class SimpleBinaryTree<Integer> implements IBinaryTree<Integer> {
    private class SimpleTreeNode implements TreeNode<java.lang.Integer> {
        public int value;
        public SimpleTreeNode left;
        public SimpleTreeNode right;

        public SimpleTreeNode(int value, SimpleTreeNode left, SimpleTreeNode right) {
            this.value = value;
            this.left = left;
            this.right = right;
        }

        public SimpleTreeNode(int value) {
            this(value, null, null);
        }

        @Override
        public java.lang.Integer getValue() {
            return value;
        }

        @Override
        public TreeNode<java.lang.Integer> getLeft() {
            return left;
        }

        @Override
        public TreeNode<java.lang.Integer> getRight() {
            return right;
        }

    }

    private SimpleTreeNode root = null;

    private Function<String, Integer> fromStrFunc;
    private Function<Integer, String> toStrFunc;

    public SimpleBinaryTree(Function<String, Integer> fromStrFunc, Function<Integer, String> toStrFunc) {
        this.fromStrFunc = fromStrFunc;
        this.toStrFunc = toStrFunc;
    }

    public SimpleBinaryTree(Function<String, Integer> fromStrFunc) {
        this(fromStrFunc, Object::toString);
    }

    @Override
    public TreeNode<Integer> getRoot() {
        return (TreeNode<Integer>) root;
    }

    public SimpleBinaryTree() {
        this(null);
    }

    public void refactorTree() {
        insideToTreeWithRef(this.root);
    }

    private void insideToTreeWithRef(SimpleTreeNode node) {
        if (isLeaf(node)) {
            addAChild(node);
            return;
        }

        if (!(node.left == null)) insideToTreeWithRef(node.left);
        if (!(node.right == null)) insideToTreeWithRef(node.right);
    }

    private boolean isLeaf(SimpleTreeNode node) {
        if (node.left == null && node.right == null) return true;
        return false;
    }

    private void addAChild(SimpleTreeNode node) {
        int value = (int) node.value;
        if (value % 2 == 0) node.left = new SimpleTreeNode(1);
        else node.right = new SimpleTreeNode(-1);
    }

    public void clear() {
        root = null;
    }

    private static class IndexWrapper {
        public int index = 0;
    }

    private void skipSpaces(String bracketStr, IndexWrapper iw) {
        while (iw.index < bracketStr.length() && Character.isWhitespace(bracketStr.charAt(iw.index))) {
            iw.index++;
        }
    }

    private Integer fromStr(String s) throws Exception {
        s = s.trim();
        if (s.length() > 0 && s.charAt(0) == '"') {
            s = s.substring(1);
        }
        if (s.length() > 0 && s.charAt(s.length() - 1) == '"') {
            s = s.substring(0, s.length() - 1);
        }
        if (fromStrFunc == null) {
            throw new Exception("Не определена функция конвертации строки в T");
        }
        return fromStrFunc.apply(s);
    }

    private Integer readValue(String bracketStr, IndexWrapper iw) throws Exception {
        skipSpaces(bracketStr, iw);
        if (iw.index >= bracketStr.length()) {
            return null;
        }
        int from = iw.index;
        boolean quote = bracketStr.charAt(iw.index) == '"';
        if (quote) {
            iw.index++;
        }
        while (iw.index < bracketStr.length() && (
                quote && bracketStr.charAt(iw.index) != '"' ||
                        !quote && !Character.isWhitespace(bracketStr.charAt(iw.index)) && "(),".indexOf(bracketStr.charAt(iw.index)) < 0
        )) {
            iw.index++;
        }
        if (quote && bracketStr.charAt(iw.index) == '"') {
            iw.index++;
        }
        String valueStr = bracketStr.substring(from, iw.index);
        Integer value = fromStr(valueStr);
        skipSpaces(bracketStr, iw);
        return value;
    }

    private SimpleTreeNode fromBracketStr(String bracketStr, IndexWrapper iw) throws Exception {
        Integer parentValue = readValue(bracketStr, iw);
        SimpleTreeNode parentNode = new SimpleTreeNode((java.lang.Integer) parentValue);
        if (bracketStr.charAt(iw.index) == '(') {
            iw.index++;
            skipSpaces(bracketStr, iw);
            if (bracketStr.charAt(iw.index) != ',') {
                parentNode.left = fromBracketStr(bracketStr, iw);
                skipSpaces(bracketStr, iw);
            }
            if (bracketStr.charAt(iw.index) == ',') {
                iw.index++;
                skipSpaces(bracketStr, iw);
            }
            if (bracketStr.charAt(iw.index) != ')') {
                parentNode.right = fromBracketStr(bracketStr, iw);
                skipSpaces(bracketStr, iw);
            }
            if (bracketStr.charAt(iw.index) != ')') {
                throw new Exception(String.format("Ожидалось ')' [%d]", iw.index));
            }
            iw.index++;
        }

        return parentNode;
    }

    public void fromBracketNotation(String bracketStr) throws Exception {
        IndexWrapper iw = new IndexWrapper();
        SimpleTreeNode root = fromBracketStr(bracketStr, iw);
        if (iw.index < bracketStr.length()) {
            throw new Exception(String.format("Ожидался конец строки [%d]", iw.index));
        }
        this.root = root;
    }
}
